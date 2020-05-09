package com.jieming.coupon.feign;

import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.TemplateUpdateInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "eureka-client-coupon-distribution")
public interface DistributionClient {

    @RequestMapping(value = "/coupon-distribution/update/cache/template",method = RequestMethod.POST)
    void updateCacheTemplate(@RequestBody TemplateUpdateInfo info) throws CouponException;

}
