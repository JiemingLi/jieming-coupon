package com.jieming.coupon.dao;

import com.jieming.coupon.constant.CouponStatus;
import com.jieming.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义</h1>
 * Created by Jieming.
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * <h2>根据 userId + 状态寻找优惠券记录</h2>
     * where userId = ... and status = ...
     * */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

    List<Coupon> findAllByUserId(Long userId);

//    Coupon findById(Integer id);
}
