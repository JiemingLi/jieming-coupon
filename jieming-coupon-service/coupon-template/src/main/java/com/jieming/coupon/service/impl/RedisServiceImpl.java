package com.jieming.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void saveToRedis(String token, User user) {
        redisTemplate.opsForValue().set(token,JSON.toJSONString(user),60 * 10 , TimeUnit.SECONDS);
    }
}
