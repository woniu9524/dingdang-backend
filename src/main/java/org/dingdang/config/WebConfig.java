package org.dingdang.config;

import org.dingdang.common.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 0:13 星期二
 * @Description: Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // 允许所有来源
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的HTTP方法
                        .allowedHeaders("*") // 允许所有请求头
                        .allowCredentials(false) // 是否允许发送Cookie
                        .maxAge(3600); // 预检请求的缓存时间
            }
        };
    }
}
