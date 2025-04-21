package com.springai.springai.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.springai.springai.mapper.SystemUserMapper;
import core.pojo.entity.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaTokenUtil {
    private final SystemUserMapper systemUserMapper;
    public SystemUser getLoginUser(){
        Long userId = StpUtil.getLoginIdAsLong();
        SystemUser systemUser = systemUserMapper.selectById(userId);
        if(systemUser == null){
            return null;
        }
        return systemUser;
    }
}
