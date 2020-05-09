package com.jieming.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateInfo {
    List<Long> userIds;
    CouponTemplateSDK templateSDK;
}
