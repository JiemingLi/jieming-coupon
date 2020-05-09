package com.jieming.coupon.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.text.SimpleDateFormat;

/**
 * <h1>Jackson 的自定义配置</h1>
 * Created by Jieming.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper getObjectMapper() {
        // 修改一下数据时间日期的转换
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
        ));
        return mapper;
    }
}
