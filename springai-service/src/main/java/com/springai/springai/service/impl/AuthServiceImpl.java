package com.springai.springai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springai.springai.mapper.SystemUserMapper;
import com.springai.springai.service.AuthService;
import core.common.CoreCode;
import core.encryption.BaseEncryption;
import core.exception.BusinessException;
import core.pojo.entity.SystemRole;
import core.pojo.entity.SystemUser;
import core.pojo.vo.AuthVO;
import core.pojo.vo.UserLoginVO;
import core.pojo.vo.UserRegisterVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final SystemUserMapper systemUserMapper;

    @Override
    public AuthVO login(UserLoginVO userloginVO) {
        //登录返回AuthVO
        String username = userloginVO.getUsername();
        String password = userloginVO.getPassword();
        password = BaseEncryption.encrypt(password);
        log.info("login username:{}, password:{}", username, password);
        QueryWrapper<SystemUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted",false);
        //执行查询
        SystemUser systemUser = systemUserMapper.selectOne(queryWrapper);
        if (systemUser == null) {
            log.error("未找到用户");
            return new AuthVO();
        }
        String authUsername = systemUser.getUsername();
        String authPassword = systemUser.getPassword();
        AuthVO authVO = new AuthVO();
        if (authUsername.equals(username) && authPassword.equals(password)) {
            {
                //设置用户信息
                authVO.setUsername(authUsername);
                List<SystemRole> roles = systemUser.getRoles();
                if(roles==null||roles.isEmpty()){
                    authVO.setRoles(new ArrayList<>());
                }else{
                    //设置角色
                    authVO.setRoles(roles.stream().map(
                            SystemRole::getName).toList());
                }
            }
        }else{
            log.error("用户名或密码错误");
            return new AuthVO();
//            throw new BusinessException(CoreCode.USER_ACCOUNT_ERROR,"用户名或密码错误");
        }
        StpUtil.login(systemUser.getId());
        authVO.setToken(StpUtil.getTokenValue());
        return authVO;
    }

        @Override
        public AuthVO userInfo () {
             SystemUser systemUser = systemUserMapper.selectById(StpUtil.getLoginIdAsLong());
             AuthVO authVO = new AuthVO();
             authVO.setUsername(systemUser.getUsername());
             List<SystemRole> roles = systemUser.getRoles();
             if(roles==null||roles.isEmpty()){
                 //防止空指针异常
                 authVO.setRoles(new ArrayList<>());
             }else {
                 //设置角色
                 authVO.setRoles(roles.stream().map(SystemRole::getName).toList());
             }
             authVO.setToken(StpUtil.getTokenValue());
             return authVO;
        }

    @Override
    public String logout() {
        StpUtil.logout();
        return "退出成功";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(UserRegisterVO userRegisterVO) {
        SystemUser systemUser = new SystemUser();
        String username = userRegisterVO.getUsername();
        String password = userRegisterVO.getPassword();
        systemUser.setUsername(username);
        systemUser.setPassword(BaseEncryption.encrypt(password));
//        QueryWrapper<SystemUser> queryWrapper = new QueryWrapper<>();
        try {
            systemUserMapper.insert(systemUser);
        } catch (Exception e) {
            log.error("注册失败", e);
            return "用户名已存在";
        }
        log.info("注册成功,用户信息:{}", systemUser);
        return "注册成功";
    }
}
