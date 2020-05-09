package com.jieming.coupon.service;

import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.TemplateNumber;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板基础(view, delete...)服务定义</h1>
 * Created by Jieming.
 */
public interface ITemplateBaseService {

    /**
     * <h2>根据优惠券模板 id 获取优惠券模板信息</h2>
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     * */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * @return {@link CouponTemplateSDK}s
     * */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids 模板 ids
     * @return Map<key: 模板 id， value: CouponTemplateSDK>
     * */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);

    // 找到所有优惠券的模板
    List<CouponTemplateSDK> findAll();

    // 找到没有用的优惠券
    List<CouponTemplateSDK> findUnUsableCouponTemplateSdk();

    // 按照分类查找优惠券还有多少张
    TemplateNumber findLeftNumByCategory(String category);



}
