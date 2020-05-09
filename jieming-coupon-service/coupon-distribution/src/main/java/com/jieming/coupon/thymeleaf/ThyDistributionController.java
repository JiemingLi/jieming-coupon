package com.jieming.coupon.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.dao.CouponDao;
import com.jieming.coupon.dao.UseInfoDao;
import com.jieming.coupon.entity.Coupon;
import com.jieming.coupon.entity.UseInfo;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.feign.TemplateClient;
import com.jieming.coupon.service.IUserService;
import com.jieming.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h1>优惠券分发 Controller</h1>
 * Created by Jieming.
 */
@Slf4j
@Controller
@RequestMapping("/distribution/thy")
public class ThyDistributionController {

    @Autowired
    private UseInfoDao useInfoDao;

    /** Coupon Dao */
    private final CouponDao couponDao;

    /** 用户相关服务 */
    private final IUserService userService;

    /** 模板微服务 */
    private final TemplateClient templateClient;

    public static String USERID;
    public static List<SettlementInfo.CouponAndTemplateInfo> cAtempalteInfos;

    @Autowired
    public ThyDistributionController(CouponDao couponDao, IUserService userService,
                                     TemplateClient templateClient) {
        this.couponDao = couponDao;
        this.userService = userService;
        this.templateClient = templateClient;
    }


    // 查看使用记录
    @GetMapping("/getUseInfos")
    public String useInfos(ModelMap map){
        List<UseInfo> useInfos = useInfoDao.findAll();
        map.addAttribute("useInfos",useInfos);
        return "useinfos_list";
    }

    // 查看所有日志
    @GetMapping("/getLogInfos")
    public String logInfos(ModelMap map){
        List<Coupon> coupons = couponDao.findAll();
        List<LogInfo> logInfos = new LinkedList<>();
        for(Coupon coupon:coupons){
            LogInfo info = new LogInfo();
            info.setStatus(coupon.getStatus().getDescription());
            info.setUserId(coupon.getUserId());
            info.setTemplateId(coupon.getTemplateId());
            logInfos.add(info);
        }
        map.addAttribute("loginfos",logInfos);
        return "loginfos_list";
    }


    // 跳转到购物车
    @GetMapping("/cart")
    public String toCart(ModelMap map){
        log.info("visit cart.");
        map.addAttribute("userId",USERID);
        map.addAttribute("cAtempalteInfos",cAtempalteInfos);
        return "template_cart";
    }

    /**
     * <h2>所有用户的优惠券信息</h2>
     * 127.0.0.1/coupon-distribution/distribution/thy/users
     * */
    @GetMapping("/users")
    public String users(ModelMap map) {

        log.info("view all user coupons.");

        List<Coupon> coupons = couponDao.findAll();
        List<ThyCouponInfo> infos = coupons.stream()
                .map(ThyCouponInfo::to).collect(Collectors.toList());
        map.addAttribute("coupons", infos);
        return "users_coupon_list";
    }

    /**
     * <h2>当前用户的所有优惠券信息</h2>
     * */
    @GetMapping("/user/{userId}")
    public String user(@PathVariable Long userId, ModelMap map) {

        log.info("view user: {} coupons.", userId);

        List<Coupon> coupons = couponDao.findAllByUserId(userId);
        List<ThyCouponInfo> infos = coupons.stream()
                .map(ThyCouponInfo::to).collect(Collectors.toList());
        map.addAttribute("coupons", infos);
        map.addAttribute("uid", userId);

        return "user_coupon_list";
    }

    /**
     * <h2>用户可以领取的优惠券模板</h2>
     * */
    @GetMapping("/template/{userId}")
    public String template(@PathVariable Long userId, ModelMap map) throws CouponException {

        log.info("view user: {} can acquire template.", userId);

        List<CouponTemplateSDK> templateSDKS = userService.findAvailableTemplate(userId);
        List<ThyTemplateInfo> infos = templateSDKS.stream()
                .map(ThyTemplateInfo::to).collect(Collectors.toList());
        infos.forEach(i -> i.setUserId(userId));

        map.addAttribute("templates", infos);

        return "template_list";
    }

    // 用户查看某一张优惠券的详情
    @GetMapping("/template/info")
        public String templateInfo(@RequestParam Long uid, @RequestParam Integer id, ModelMap map) {

            log.info("user view template info: {} -> {}", uid, id);

            Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                    Collections.singletonList(id)
            ).getData();

            if (MapUtils.isNotEmpty(id2Template)) {
            ThyTemplateInfo info = ThyTemplateInfo.to(id2Template.get(id));
            info.setUserId(uid);
            map.addAttribute("template", info);
        }

        return "template_detail";
    }

    // 用户领取优惠券
    @GetMapping("/acquire")
    public String acquire(@RequestParam Long uid, @RequestParam Integer tid) throws CouponException {

        log.info("user {} acquire template {}.", uid, tid);

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(tid)
        ).getData();
        if (MapUtils.isNotEmpty(id2Template)) {
            log.info("user acquire coupon: {}", JSON.toJSONString(userService.acquireTemplate(
                    new AcquireTemplateRequest(uid, id2Template.get(tid))
            )));
        }

        return "redirect:/distribution/thy/user/" + uid;
    }

    // 用户使用优惠券
    @PostMapping("/use/template")
    @ResponseBody
    public String getObjectParams(@RequestBody ObjectParams objectParams,ModelMap map){
        System.err.println(objectParams);
        // 获取页面传递来的参数
        String[] ids = objectParams.getIds();
        String userId = objectParams.getUserId();
        List<SettlementInfo.CouponAndTemplateInfo> couponAndTemplateInfos = new ArrayList<>();
        List<Coupon> coupons = new LinkedList<>();
        // 参数的类型转换
        List<Integer> couponsIds = new ArrayList<>();
        for(String id:ids){
            couponsIds.add(Integer.valueOf(id));
        }
        // 获取couponTemplateId
        List<Integer> couponTemplateIds = new ArrayList<>();
        for(Integer couponId:couponsIds){
            Coupon coupon = couponDao.findById(couponId).get();
            coupons.add(coupon);
            couponTemplateIds.add(coupon.getTemplateId());
        }

        // templateSDK 的 id 与 templateSDK的映射
        Map<Integer, CouponTemplateSDK> ids2TemplateSDK = templateClient.findIds2TemplateSDK(couponTemplateIds).getData();
        Iterator<Integer> iterator = ids2TemplateSDK.keySet().iterator();
        while (iterator.hasNext()){
            Integer templateId = iterator.next();
            for(Coupon coupon : coupons){
                if(coupon.getTemplateId() == templateId){
                    // 添加couponId和CouponTemplateSDK到info
                    SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo = new SettlementInfo.CouponAndTemplateInfo();
                    couponAndTemplateInfo.setId(coupon.getId());
                    CouponTemplateSDK templateInfor = ids2TemplateSDK.get(templateId);
                    couponAndTemplateInfo.setTemplate(templateInfor);
                    couponAndTemplateInfos.add(couponAndTemplateInfo);
                    break;
                }
            }
        }
        USERID = userId;
        cAtempalteInfos = couponAndTemplateInfos;
        return "template_cart";
    }
}
