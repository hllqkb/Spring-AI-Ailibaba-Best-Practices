package com.springai.springai.service.impl;

import core.pojo.entity.SystemUser;
import com.springai.springai.mapper.SystemUserMapper;
import com.springai.springai.service.ISystemUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {

}
