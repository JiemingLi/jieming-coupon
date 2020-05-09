package com.jieming.coupon.dao;


import com.jieming.coupon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Integer> {
    User findByAccount(String account);
}
