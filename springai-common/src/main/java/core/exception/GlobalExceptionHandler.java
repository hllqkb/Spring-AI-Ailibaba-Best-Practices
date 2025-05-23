package core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import core.common.BaseResponse;
import core.common.CoreCode;
import core.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
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
	@ExceptionHandler(BusinessException.class)
	public BaseResponse<?> businessExceptionHandler(BusinessException e) {
		e.printStackTrace();
		return ResultUtils.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
		e.printStackTrace();
		return ResultUtils.error(CoreCode.SYSTEM_ERROR, e.getMessage());
	}

}
