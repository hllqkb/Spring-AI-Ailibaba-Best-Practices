package com.springai.springai.utils;

// 引入核心模块的代码常量类
import core.common.CoreCode;
// 引入业务异常类，用于在业务逻辑中抛出异常
import core.exception.BusinessException;
// 引入系统用户的实体类，表示登录用户的信息
import core.pojo.entity.SystemUser;
// 使用Lombok注解，自动生成日志记录器
import lombok.extern.slf4j.Slf4j;
// 引入Spring Security的认证对象类，用于表示认证信息
import org.springframework.security.core.Authentication;
// 引入Spring Security的安全上下文类，用于存储安全相关的信息
import org.springframework.security.core.context.SecurityContext;
// 引入Spring Security的安全上下文持有者类，用于获取当前的安全上下文
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class SecurityFrameworkUtil {

	// 获取当前的认证信息
	public static Authentication getAuthentication() {
		// 从SecurityContextHolder中获取当前的安全上下文
		SecurityContext context = SecurityContextHolder.getContext();
		// 如果安全上下文为空，则抛出业务异常，使用CoreCode.OPERATION_ERROR作为错误码
		Optional.ofNullable(context).orElseThrow(() -> new BusinessException(CoreCode.OPERATION_ERROR));
		// 返回安全上下文中的认证信息
		return context.getAuthentication();
	}

	// 获取当前登录的系统用户
	public static SystemUser getLoginUser() {
		// 调用getAuthentication方法获取当前的认证信息
		Authentication authentication = getAuthentication();
		// 检查认证信息中的主体是否是SystemUser实例
		if (authentication.getPrincipal() instanceof SystemUser) {
			// 如果是，则返回该SystemUser实例
			return (SystemUser) authentication.getPrincipal();
		} else {
			// 如果不是，则抛出业务异常，使用CoreCode.OPERATION_ERROR作为错误码
			throw new BusinessException(CoreCode.OPERATION_ERROR);
		}
	}

	// 获取当前登录用户的ID
	public static Long getCurrUserId() {
		// 调用getLoginUser方法获取当前登录的系统用户
		SystemUser loginUser = getLoginUser();
		// 如果用户不为空，则返回用户的ID，否则返回null
		return loginUser != null ? loginUser.getId() : null;
	}

	// 尝试获取当前登录的系统用户，如果失败则返回空的Optional对象
	public static Optional<SystemUser> tryGetLoginUser() {
		try {
			// 调用getLoginUser方法获取当前登录的系统用户
			SystemUser loginUser = getLoginUser();
			// 如果用户不为空，则返回包含该用户的Optional对象
			return Optional.ofNullable(loginUser);
		} catch (Exception e) {
			// 如果发生异常，则记录日志并返回空的Optional对象
			log.error("Failed to get login user", e);
			return Optional.empty();
		}
	}

}
