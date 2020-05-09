package com.jieming.coupon.service.impl;

import com.jieming.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean isExist(String token) {

        return redisTemplate.opsForValue().get(token) != null;
    }
}
