package com.jieming.coupon.constant;

/**
 * <h1>常用常量定义</h1>
 * Created by Jieming.
 */
public class Constant {

    /** Kafka 消息的 Topic */
    public static final String TOPIC = "jieming_user_coupon_op";

    /**
     * <h2>Redis Key 前缀定义</h2>
     * */
    public static class RedisPrefix {

        /** 优惠券码 key 前缀 */
        public static final String COUPON_TEMPLATE =
                "jieming_coupon_template_code_";

        /** 用户当前所有可用的优惠券 key 前缀 */
        public static final String USER_COUPON_USABLE =
                "jieming_user_coupon_usable_";

        /** 用户当前所有已使用的优惠券 key 前缀 */
        public static final String USER_COUPON_USED =
                "jieming_user_coupon_used_";

        /** 用户当前所有已过期的优惠券 key 前缀 */
        public static final String USER_COUPON_EXPIRED =
                "jieming_user_coupon_expired_";

        public static final String TEMPLATE_USER_ = "Jieming_Template_User_";
    }
}
