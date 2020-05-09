package com.jieming.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInfo {
    private Long userId;
    private Integer templateId;
    private String status;
}
