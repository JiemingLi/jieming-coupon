package com.jieming.coupon.service.impl;

import com.jieming.coupon.constant.Constant;
import com.jieming.coupon.dao.UserDao;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.service.IRedisService;
import com.jieming.coupon.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IRedisService redisService;


    @Override
    public String login(User user) {
        String account = user.getAccount();
        String password = user.getPassword();
        if(account == null || password == null) return null;

        User dbUser = userDao.findByAccount(account);
        String token = null;
        if(dbUser != null && password.equals(dbUser.getPassword())){
            // write to redis
            token = generateToken(account,password);
            redisService.saveToRedis(token,dbUser);
            log.info("成功加入redis");
        }
        return token;
    }

    private String generateToken(String account,String password){
        return Constant.RedisPrefix.TEMPLATE_USER_ + account + password;
    }

    @Override
    public User register(User user) {
        String account = user.getAccount();
        String password = user.getPassword();
        if(account == null || password == null) return null;
        User dbUser = userDao.findByAccount(account);
        if(dbUser != null) return null;
        User saveUser = userDao.save(user);
        return saveUser;
    }
}
