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

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<AuthVO> login(@RequestBody UserLoginVO userLoginVO) {
        return ResultUtils.success(authService.login(userLoginVO));
    }

    @GetMapping("/userInfo")
    public BaseResponse<AuthVO> getUserInfo() {
        return ResultUtils.success(authService.userInfo());
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout() {
        return ResultUtils.success(authService.logout());
    }

    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody UserRegisterVO userRegisterVO) {
        return ResultUtils.success(authService.register(userRegisterVO));
    }
}
