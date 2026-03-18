package com.platform.sosangongin.api.advices;

import com.platform.sosangongin.cases.CommonResultTemplate;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResultResponseAdvice implements ResponseBodyAdvice<CommonResultTemplate> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return CommonResultTemplate.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public CommonResultTemplate beforeBodyWrite(CommonResultTemplate body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null && body.getHttpStatus() != null) {
            response.setStatusCode(body.getHttpStatus());
        }
        return body;
    }
}
