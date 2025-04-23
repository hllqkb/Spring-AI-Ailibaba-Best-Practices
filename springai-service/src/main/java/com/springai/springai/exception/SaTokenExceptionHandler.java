package com.springai.springai.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenContextException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SaTokenExceptionHandler {

    //捕获未获取上下文的异常，通常是因为Ai对话异常导致的
    @ExceptionHandler(SaTokenContextException.class)
    public ResponseEntity<String> handleSaTokenContextException(SaTokenContextException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ai对话异常");
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<String> handleNotLoginException(NotLoginException nle) {
        String message = "";
        if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未提供 Token";
        } else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "Token 无效";
        } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "Token 已过期，请重新登录";
        } else if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "您的账户已在另一台设备上登录，如非本人操作，请立即修改密码";
        } else if (nle.getType().equals(NotLoginException.KICK_OUT)) {
            message = "您已被系统强制下线";
        } else {
            message = "当前会话未登录";
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}