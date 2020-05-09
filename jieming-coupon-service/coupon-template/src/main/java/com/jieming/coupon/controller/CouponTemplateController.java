package com.jieming.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jieming.coupon.dao.UserDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.service.IBuildTemplateService;
import com.jieming.coupon.service.ITemplateBaseService;
import com.jieming.coupon.service.IUserService;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.TemplateNumber;
import com.jieming.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板相关的功能控制器</h1>
 * Created by Jieming.
 */
@Slf4j
@RestController
public class CouponTemplateController {

    /** 构建优惠券模板服务 */
    private final IBuildTemplateService buildTemplateService;

    /** 优惠券模板基础服务 */
    private final ITemplateBaseService templateBaseService;

    /** 用户登录 */
    private final IUserService userService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService,
                                    ITemplateBaseService templateBaseService,
                                    IUserService userService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
        this.userService = userService;
    }

    /**
     * <h2>构建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/jieming/coupon-template/template/build
     * 成功
     * */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * <h2>查询优惠券模板详情</h2>
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * 127.0.0.1:9000/jieming/coupon-template/template/info?id=1
     * 成功
     * */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * 127.0.0.1:9000/jieming/coupon-template/template/sdk/all
     * 成功
     * */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    // 查找全部优惠券模板 成功
    @GetMapping("/template/all")
    public List<CouponTemplateSDK> findAll() {
        log.info("Find All Templates.");
        return templateBaseService.findAll();
    }

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     * 127.0.0.1:9000/jieming/coupon-template/template/sdk/infos?ids=1,2
     * 成功
     * */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids
    ) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }

    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody User user){
       return userService.login(user);
    }

    @ResponseBody
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }


    // 成功
    @ResponseBody
    @GetMapping("/find/template/nums")
    public TemplateNumber findLeftNumByCategory(@RequestParam("category") String category){
        return templateBaseService.findLeftNumByCategory(category);
    }

    //更新优惠券模板
    // 待测试，需要调用分发微服务
    @PostMapping("/template/update")
    public CouponTemplate updateTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Update Template: {}", JSON.toJSONString(request));
        return buildTemplateService.updateTemplate(request);
    }

    // 增加一定数量的优惠券
    // 本地测试成功
    @PostMapping("/template/addCoupons")
    public CouponTemplate addCoupons(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Update Template: {}", JSON.toJSONString(request));
        return buildTemplateService.addCouponTemplateCount(request);
    }

    // 减少一定数量的优惠券
    // 本地测试成功
    @PostMapping("/template/subCoupons")
    public CouponTemplate subCoupons(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Update Template: {}", JSON.toJSONString(request));
        return buildTemplateService.subCouponTemplateCount(request);
    }

    //  本地测试成功
    @GetMapping("/template/findLeftNum")
    TemplateNumber findLeftNum(@RequestParam("category") String category){
        return templateBaseService.findLeftNumByCategory(category);
    }
}
