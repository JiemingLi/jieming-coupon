package com.jieming.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.constant.CouponStatus;
import com.jieming.coupon.dao.CouponDao;
import com.jieming.coupon.entity.Coupon;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.LogInfo;
import com.jieming.coupon.vo.TemplateUpdateInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <h1>用户服务功能测试用例</h1>
 * Created by Jieming.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    /** fake 一个 UserId */
    private Long fakeUserId = 1001L;

    @Autowired
    private IUserService userService;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    public void testStatus() {
        List<Coupon> coupons = couponDao.findAll();
        List<LogInfo> logInfos = new LinkedList<>();
        for(Coupon coupon:coupons){
            LogInfo info = new LogInfo();
            info.setStatus(coupon.getStatus().getDescription());
            info.setUserId(coupon.getUserId());
            info.setTemplateId(coupon.getTemplateId());
            logInfos.add(info);
        }
        System.err.println(logInfos);
    }


    @Test
    public void testExpired() throws CouponException{
        String redisKeyForExipred = "jieming_user_coupon_expired_1001";
//        redisTemplate.opsForHash().put(redisKeyForExipred,"1","11");
//        redisTemplate.opsForHash().put(redisKeyForExipred,"2","22");

        List<String> cleanKeys = new LinkedList<>();
        cleanKeys.add("1");
        cleanKeys.add("2");
        redisTemplate.opsForHash().delete(redisKeyForExipred,cleanKeys.toArray());
    }

    @Test
    public void testRedisTemplate() throws CouponException{
        Map<String,String> map = new HashMap<>();
        String redisKey = "lijieming";
        map.put("name","xiaomingtongxue");
        map.put("age","38");
        redisTemplate.opsForHash().putAll(redisKey,map);
    }


    @Test
    public void testcoupons() throws  CouponException{
        List<Coupon> all = couponDao.findAll();
        System.out.println();
    }


    @Test
    public void testFindCouponByStatus() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        fakeUserId,
                        CouponStatus.USABLE.getCode()
                )
        ));
//        System.out.println(JSON.toJSONString(
//                userService.findCouponsByStatus(
//                        fakeUserId,
//                        CouponStatus.USED.getCode()
//                )
//        ));
//        System.out.println(JSON.toJSONString(
//                userService.findCouponsByStatus(
//                        fakeUserId,
//                        CouponStatus.EXPIRED.getCode()
//                )
//        ));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findAvailableTemplate(fakeUserId)
        ));
    }
}
