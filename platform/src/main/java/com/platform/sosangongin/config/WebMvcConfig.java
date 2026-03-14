package com.platform.sosangongin.config;

import com.platform.sosangongin.api.interceptor.MetricsInterceptor;
import com.platform.sosangongin.api.resolver.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // MetricsInterceptor는 stg, prod 프로파일에서만 빈으로 등록되므로, 주입이 선택적으로 이루어지도록 설정
    @Autowired(required = false)
    private MetricsInterceptor metricsInterceptor;
    
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // metricsInterceptor가 주입되었을 때만 (stg, prod 프로파일일 때만) 인터셉터로 등록
        if (metricsInterceptor != null) {
            registry.addInterceptor(metricsInterceptor)
                    .addPathPatterns("/api/**");
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }
}
