package com.jieming.coupon.advice;

import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理</h1>
 * Created by Jieming.
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * <h2>对 CouponException 进行统一处理</h2>
     * */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest req, CouponException ex
    ) {

        CommonResponse<String> response = new CommonResponse<>(
                -1, "business error");
        response.setData(ex.getMessage());
        return response;
    }
}
