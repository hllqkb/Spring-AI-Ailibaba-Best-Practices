package com.springai.springai.service;

import core.pojo.vo.AuthVO;
import core.pojo.vo.UserLoginVO;
import core.pojo.vo.UserRegisterVO;

public interface AuthService {
    AuthVO login(UserLoginVO userloginVO);
    AuthVO userInfo();

    String logout();

    String register(UserRegisterVO userRegisterVO);
}
