package com.jieming.coupon.filter;

import com.jieming.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * <h1>校验请求中传递的 Token 是否存在</h1>
 * Created by Jieming.
 */
@Slf4j
//@Component
public class TokenFilter extends AbstractPreZuulFilter {

    @Autowired
    private IRedisService redisService;

    // 校验 token
    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));

        String token = (String) request.getParameter("token");

        // 没有token则表示用户没有权限访问
        if (null == token) {
            log.error("error: token is empty");
            return fail(401, "error: token is empty");
        }

        if(! redisService.isExist(token)){
            return fail(401, "error: user is not  login");
        }

        return success();
    }

    /**
     * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
     * important for a filter. filterOrders do not need to be sequential.
     *
     * @return the int order of a filter
     */
    @Override
    public int filterOrder() {
        return 1;
    }
}
