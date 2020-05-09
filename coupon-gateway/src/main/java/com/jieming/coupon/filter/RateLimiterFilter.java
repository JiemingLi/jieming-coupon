package com.jieming.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>限流过滤器</h1>
 * 比如说单个ip地址一秒内访问多少次；或者整个系统来说可以接受多少请求
 * Created by Jieming.
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RateLimiterFilter extends AbstractPreZuulFilter {

    /** 每秒可以获取到两个令牌 */
    // 限流器
    RateLimiter rateLimiter = RateLimiter.create(2.0);

    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();

        // 目前是对所有IP地址进行限流
        // 扩展：只对某个URL进行限流
        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        } else {
            log.error("rate limit: {}", request.getRequestURI());
            return fail(402, "error: rate limit");
        }
    }

    /**
     * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
     * important for a filter. filterOrders do not need to be sequential.
     *
     * @return the int order of a filter
     */
    @Override
    public int filterOrder() {
        return 2;
    }
}
