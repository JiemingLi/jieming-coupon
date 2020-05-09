package com.jieming.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.constant.CouponCategory;
import com.jieming.coupon.constant.DistributeTarget;
import com.jieming.coupon.constant.PeriodType;
import com.jieming.coupon.constant.ProductLine;
import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.TemplateRequest;
import com.jieming.coupon.vo.TemplateRule;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

/**
 * <h1>构造优惠券模板服务测试</h1>
 * Created by Jieming.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Autowired
    private CouponTemplateDao couponTemplateDao;


    // 更新优惠券模板测试用例
    @Test
    public void testUpdateCouponTemplate() throws  CouponException{
        TemplateRequest request = new TemplateRequest();
        CouponTemplate couponTemplate = couponTemplateDao.findById(1).get();
        request.setProductLine(2);
        request.setName(couponTemplate.getName());
        request.setDesc("这是新的优惠券啦111");
        request.setLogo(couponTemplate.getLogo());
        TemplateRule couponTemplateRule = couponTemplate.getRule();
        couponTemplateRule.setDiscount(new TemplateRule.Discount(25,50));
        couponTemplateRule.setLimitation(2);
        couponTemplateRule.setExpiration(couponTemplate.getRule().getExpiration());
        request.setRule(couponTemplateRule);
        request.setCategory(couponTemplate.getCategory().getCode());
        request.setTarget(couponTemplate.getTarget().getCode());
        request.setCount(couponTemplate.getCount());
        request.setUserId(couponTemplate.getUserId());
        CouponTemplate template = buildTemplateService.updateTemplate(request);
        System.err.println(JSON.toJSONString(template));
    }

    @Test
    public void testSubNums() throws CouponException{
        TemplateRequest request = new TemplateRequest();
        CouponTemplate couponTemplate = couponTemplateDao.findById(4).get();
        request.setProductLine(couponTemplate.getProductLine().getCode());
        request.setName(couponTemplate.getName());
        request.setDesc(couponTemplate.getDesc());
        request.setLogo(couponTemplate.getLogo());
        request.setRule(couponTemplate.getRule());
        request.setCategory(couponTemplate.getCategory().getCode());
        request.setTarget(couponTemplate.getTarget().getCode());
        request.setCount(10);
        CouponTemplate template = buildTemplateService.subCouponTemplateCount(request);
        System.err.println(JSON.toJSONString(template));
    }


    @Test
    public void testAddNums() throws CouponException {
        TemplateRequest request = new TemplateRequest();
        CouponTemplate couponTemplate = couponTemplateDao.findById(4).get();
        request.setProductLine(couponTemplate.getProductLine().getCode());
        request.setName(couponTemplate.getName());
        request.setDesc(couponTemplate.getDesc());
        request.setLogo(couponTemplate.getLogo());
        request.setRule(couponTemplate.getRule());
        request.setCategory(couponTemplate.getCategory().getCode());
        request.setTarget(couponTemplate.getTarget().getCode());
        request.setCount(10);
        CouponTemplate template = buildTemplateService.addCouponTemplateCount(request);
        System.err.println(JSON.toJSONString(template));
    }

    @Test
    public void testBuildTemplate() throws Exception {

        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        Thread.sleep(5000);
    }

    /**
     * <h2>fake TemplateRequest</h2>
     * */
    private TemplateRequest fakeTemplateRequest() {

        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("http://www.imooc.com");
        request.setDesc("这是一张优惠券模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);  // fake user id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        // 1 过期规则定义
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        // 1.1 折扣定义
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        // 1.2 运行领取多少
        rule.setLimitation(1);
        // 1.3 使用限制
        rule.setUsage(new TemplateRule.Usage(
                "安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList("文娱", "家居"))
        ));

        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
