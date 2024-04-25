package org.dingdang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 2:45 星期二
 * @Description:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private long expiration;  // 过期时间，单位秒
    private boolean infiniteExpiration;  // 是否无限过期
}