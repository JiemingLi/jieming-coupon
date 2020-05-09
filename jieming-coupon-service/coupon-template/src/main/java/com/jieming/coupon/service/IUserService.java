package com.jieming.coupon.service;

import com.jieming.coupon.entity.User;

public interface IUserService {
    public String login(User user);
    public User register(User user);
}
