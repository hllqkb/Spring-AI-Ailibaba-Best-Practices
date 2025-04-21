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

/**
 * @Project: me.pgthinker.core.exception
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/30 17:08
 * @Description:
 */
@RestControllerAdvice
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotLoginException.class)
	public ResponseEntity<String> handleNotLoginException(NotLoginException e) {
		return new ResponseEntity<>("未能读取到有效 token", HttpStatus.UNAUTHORIZED);
//		return new ResponseEntity<String>(ResultUtils.error(CoreCode.NOT_LOGIN, e.getMessage()), null, 401);
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
