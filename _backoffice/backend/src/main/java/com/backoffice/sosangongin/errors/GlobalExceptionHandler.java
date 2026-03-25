package com.backoffice.sosangongin.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 로그인 실패 (아이디/비밀번호 불일치) 등 잘못된 인자로 요청 시 발생
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("로그인 실패: {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    /**
     * 잠긴 계정으로 로그인 시도 등 부적절한 상태에서 요청 시 발생
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        log.warn("접근 거부: {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    /**
     * 처리되지 않은 나머지 예외들을 처리
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("서버 내부 오류 발생", e);
        return ErrorResponse.builder()
                .message("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.")
                .build();
    }
}
