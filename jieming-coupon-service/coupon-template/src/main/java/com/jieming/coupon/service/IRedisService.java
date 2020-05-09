package com.jieming.coupon.service;

import com.jieming.coupon.entity.User;

public interface IRedisService {
    void saveToRedis(String token, User user);
}
