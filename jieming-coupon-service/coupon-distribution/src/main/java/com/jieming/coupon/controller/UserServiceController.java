package com.jieming.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.dao.UseInfoDao;
import com.jieming.coupon.entity.Coupon;
import com.jieming.coupon.entity.UseInfo;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.service.IUserService;
import com.jieming.coupon.vo.AcquireTemplateRequest;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.SettlementInfo;
import com.jieming.coupon.vo.TemplateUpdateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>用户服务 Controller</h1>
 * Created by Jieming.
 */
@Slf4j
@RestController
@CrossOrigin
public class UserServiceController {

    /** 用户服务接口 */
    private final IUserService userService;

    @Autowired
    private UseInfoDao useInfoDao;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }


    // 更新优惠券的状态 待测试
    @GetMapping("/update/coupons")
    public void updateCouponStatusById(
            @RequestParam("id") Integer id,
            @RequestParam("status") Integer status) throws CouponException {
        userService.updateCouponStatus(id, status);
    }

    /**
     * <h2>根据用户 id 和优惠券状态查找用户优惠券记录</h2>
     * 成功
     * */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("status") Integer status) throws CouponException {

        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }



    /**
     * <h2>根据用户 id 查找当前可以领取的优惠券模板</h2>
     * 成功
     * */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam("userId") Long userId) throws CouponException {

        log.info("Find Available Template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }


    // 成功
    @PostMapping(value = "/update/cache/template")
    public void updateCacheTemplate(@RequestBody TemplateUpdateInfo info)
    throws CouponException{
        log.info("更新模板，现在更新缓存");
        userService.updateCacheTemplate(info.getUserIds(),info.getTemplateSDK());
    }

    /**
     * <h2>用户领取优惠券</h2> 成功
     * */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException {

        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * <h2>结算(核销)优惠券</h2>
     * */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody  SettlementInfo info)
            throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        SettlementInfo settlementInfo =  userService.settlement(info);
        saveInfo(settlementInfo);
        return settlementInfo;
    }

    public void saveInfo(SettlementInfo info){
        log.info("保存使用记录");
        UseInfo useInfo = new UseInfo();
        useInfo.setCost(info.getCost());
        useInfo.setUserId(info.getUserId());
        StringBuffer stringBuffer = new StringBuffer();
        List<SettlementInfo.CouponAndTemplateInfo> couponAndTemplateInfos = info.getCouponAndTemplateInfos();
        for(SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo : couponAndTemplateInfos){
            stringBuffer.append(couponAndTemplateInfo.getTemplate().getName() + " ");
        }
        useInfo.setCoupons(stringBuffer.toString());
        useInfoDao.save(useInfo);
    }
}
