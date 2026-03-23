package com.platform.sosangongin.api.interceptor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Profile("stg | prod") // stg 또는 prod 프로파일에서만 빈으로 등록
public class MetricsInterceptor implements HandlerInterceptor {

    private final Counter successCounter;
    private final Counter clientErrorCounter;
    private final Counter serverErrorCounter;

    public MetricsInterceptor(MeterRegistry meterRegistry) {
        // 커스텀 메트릭 정의: app.response.status.total
        this.successCounter = Counter.builder("app.response.status.total")
                .description("Total number of successful responses")
                .tag("type", "2xx_3xx")
                .register(meterRegistry);

        this.clientErrorCounter = Counter.builder("app.response.status.total")
                .description("Total number of client error responses")
                .tag("type", "4xx")
                .register(meterRegistry);

        this.serverErrorCounter = Counter.builder("app.response.status.total")
                .description("Total number of server error responses")
                .tag("type", "5xx")
                .register(meterRegistry);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int status = response.getStatus();

        if (status >= 400 && status < 500) {
            clientErrorCounter.increment();
        } else if (status >= 500) {
            serverErrorCounter.increment();
        } else {
            successCounter.increment();
        }
    }
}
