package com.wenbo.marketing.config;

import com.wenbo.marketing.aspect.IPBlockInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author changwenbo
 * @date 2024/8/31 15:24
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private IPBlockInterceptor ipBlockInterceptor;

    // 配置拦截规则
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(ipBlockInterceptor)
                .addPathPatterns("/**");
    }
}
