package com.jieming.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * <h1>通用的抽象过滤器类</h1>
 * Created by Jieming.
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    // 用于在过滤器之间传递消息, 数据保存在每个请求的 ThreadLocal 中
    // 扩展了 Map，本质上就是扩展了ConCurrentHashMap线程安全的类
    RequestContext context;

    // 字段标识请求是否已经结束。
    private final static String NEXT = "next";

    /**
     * a "true" return from this method means that the run() method should be invoked
     *
     * @return true if the run() method should be invoked. false will not invoke the run() method
     */
    @Override
    public boolean shouldFilter() {

        // 获取当前线程的RequestContext
        RequestContext ctx = RequestContext.getCurrentContext();
        // SpringCloud过滤器是没有NEXT这个字段的，所以默认为true;如果有这个字段
        // 就根据获取到的字段判断
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    /**
     * if shouldFilter() is true, this method will be invoked. this method is the core method of a ZuulFilter
     *
     * @return Some arbitrary artifact may be returned. Current implementation ignores it.
     * @throws ZuulException if an error occurs during execution.
     */
    @Override
    public Object run() throws ZuulException {
        context = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    // 过滤失败的时候返回状态码和错误信息
    Object fail(int code, String msg) {

        context.set(NEXT, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format("{\"result\": \"%s!\"}", msg));

        return null;
    }

    // 成功过滤的时候的操作
    Object success() {

        context.set(NEXT, true);

        return null;
    }
}
