package com.jieming.coupon.service.impl;

import com.jieming.coupon.constant.Constant;
import com.jieming.coupon.constant.CouponStatus;
import com.jieming.coupon.constant.ProductLine;
import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.dao.UserDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.feign.DistributionClient;
import com.jieming.coupon.service.IAsyncService;
import com.jieming.coupon.service.IBuildTemplateService;
import com.jieming.coupon.service.IRedisService;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.TemplateRequest;
import com.jieming.coupon.vo.TemplateRule;
import com.jieming.coupon.vo.TemplateUpdateInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <h1>构建优惠券模板接口实现</h1>
 * Created by Jieming.
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    /** 异步服务 */
    private final IAsyncService asyncService;

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private IRedisService redisService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DistributionClient distributionClient;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService,
                                    CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * <h2>创建优惠券模板</h2>
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException {

        // 参数合法性校验
        if (!request.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        // 判断同名的优惠券模板是否存在
        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    @Override
    public CouponTemplate updateTemplate(TemplateRequest request) throws CouponException{
        if(!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        CouponTemplate template = templateDao.findByName(request.getName());
        if(null == template){
            throw new CouponException("Not Exist The CouponTemplate");
        }

        // 更新数据库部分字段，不更新createTime和数量
        //templateSDK.getRule().getExpiration().getDeadline() < curTime
        long curTime = new Date().getTime();
        if(template.getRule().getExpiration().getDeadline() > curTime){
            template.setAvailable(true);
            template.setExpired(false);
        }else{
            template.setAvailable(false);
            template.setExpired(true);
        }

        template.setDesc(request.getDesc());
        template.setLogo(request.getLogo());
        template.setProductLine(ProductLine.of(request.getProductLine()));
        TemplateRule oldRule = template.getRule();
        TemplateRule newRule = request.getRule();
        oldRule.setDiscount(newRule.getDiscount());
        oldRule.setExpiration(newRule.getExpiration());
        oldRule.setWeight(newRule.getWeight());
        oldRule.setLimitation(request.getRule().getLimitation());

        List<User> users = userDao.findAll();
        List<Long> userIds = new ArrayList<>();

        for(User user : users){
            userIds.add(Long.valueOf(user.getAccount()));
        }
        CouponTemplate result = templateDao.save(template);
        // 更新缓存
        CouponTemplateSDK updateCouponTemplateSDK = couponTemplate2CouponTemplateSDK(result);
        TemplateUpdateInfo info = new TemplateUpdateInfo(userIds,updateCouponTemplateSDK);
        distributionClient.updateCacheTemplate(info);
        return result;
    }

    // 为某些类型增加一些数量
    @Override
    public CouponTemplate addCouponTemplateCount(TemplateRequest request) {
        Integer count = request.getCount();
        String name = request.getName();
        CouponTemplate template = templateDao.findByName(name);
        template.setAvailable(true);
        template.setCount(count + template.getCount());
        template = templateDao.save(template);
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId());
        AsyncServiceImpl asyncServiceImpl = new AsyncServiceImpl();
        Set<String> couponCodes = asyncServiceImpl.addCouponCode(template, count);
        redisTemplate.opsForList().rightPushAll(redisKey,couponCodes);
        return template;
    }

    // 为模板类型减少一定量的优惠券
    @Override
    public CouponTemplate subCouponTemplateCount(TemplateRequest request) {
        Integer count = request.getCount();
        CouponTemplate template = templateDao.findByName(request.getName());
        count = template.getCount() > count ? count : template.getCount();
        Integer leftNum = template.getCount() - count;
        template.setCount(leftNum);
        template = templateDao.save(template);
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId());
        for(Integer i = 0;i<count;i++){
            redisTemplate.opsForList().leftPop(redisKey);
        }
        return template;
    }

    private String status2RedisKey(Integer status, Long userId) {

        String redisKey = null;

        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }

        return redisKey;
    }


    private CouponTemplateSDK couponTemplate2CouponTemplateSDK(CouponTemplate template){
        CouponTemplateSDK sdk = new CouponTemplateSDK();
        sdk.setRule(template.getRule());
        sdk.setCategory(template.getCategory().getCode());
        sdk.setDesc(template.getDesc());
        sdk.setId(template.getId());
        sdk.setKey(template.getKey());
        sdk.setLogo(template.getLogo());
        sdk.setName(template.getName());
        sdk.setUserId(template.getUserId());
        sdk.setTarget(template.getTarget().getCode());
        sdk.setProductLine(template.getProductLine().getCode());
        return sdk;
    }

    /**
     * <h2>将 TemplateRequest 转换为 CouponTemplate</h2>
     * */
    private CouponTemplate requestToTemplate(TemplateRequest request) {

        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
