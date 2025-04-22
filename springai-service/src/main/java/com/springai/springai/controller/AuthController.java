package com.springai.springai.controller;

import com.springai.springai.service.AuthService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.AuthVO;
import core.pojo.vo.UserLoginVO;
import core.pojo.vo.UserRegisterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 登录接口
     * @param userLoginVO 用户登录信息
     * @return 登录成功返回token，失败返回错误信息
     * @throws Exception
     */
    @PostMapping("/login")
    public BaseResponse<AuthVO> login(@RequestBody UserLoginVO userLoginVO) {
        return ResultUtils.success(authService.login(userLoginVO));
    }
/**
 * 获取用户信息接口
 * @return 用户信息
 */
    @GetMapping("/userInfo")
    public BaseResponse<AuthVO> getUserInfo() {
        return ResultUtils.success(authService.userInfo());
    }
/**
 * 登出接口
 * @return 登出成功返回成功信息，失败返回错误信息
 */
    @PostMapping("/logout")
    public BaseResponse<String> logout() {
        return ResultUtils.success(authService.logout());
    }

    /**
     * 注册接口
     * @param userRegisterVO
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody UserRegisterVO userRegisterVO) {
        return ResultUtils.success(authService.register(userRegisterVO));
    }
}
