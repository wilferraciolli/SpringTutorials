package com.wiltech.insurly.config;

import com.wiltech.insurly.admin.access.AdminGuardInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminGuardInterceptor adminGuard;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // Path patterns match after server.servlet.context-path (/api) is stripped.
        registry.addInterceptor(adminGuard).addPathPatterns("/admin/**");
    }
}
