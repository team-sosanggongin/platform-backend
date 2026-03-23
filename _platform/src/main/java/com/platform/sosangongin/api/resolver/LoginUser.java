package com.platform.sosangongin.api.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드의 파라미터에 사용하여,
 * JWT 토큰에서 추출된 인증된 사용자의 ID(UUID)를 주입받습니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
