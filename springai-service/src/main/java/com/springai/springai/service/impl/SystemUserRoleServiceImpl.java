package com.springai.springai.service.impl;

import core.pojo.entity.SystemUserRole;
import com.springai.springai.mapper.SystemUserRoleMapper;
import com.springai.springai.service.ISystemUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRole>
		implements ISystemUserRoleService {

}
