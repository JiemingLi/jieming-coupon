package com.jieming.coupon.service;

import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.TemplateRequest;

/**
 * <h1>构建优惠券模板接口定义</h1>
 * Created by Qinyi.
 */
public interface IBuildTemplateService {

    /**
     * <h2>创建优惠券模板</h2>
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * */
    CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException;

    CouponTemplate updateTemplate(TemplateRequest request)
            throws CouponException;

    CouponTemplate addCouponTemplateCount(TemplateRequest request);

    CouponTemplate subCouponTemplateCount(TemplateRequest request);

}
