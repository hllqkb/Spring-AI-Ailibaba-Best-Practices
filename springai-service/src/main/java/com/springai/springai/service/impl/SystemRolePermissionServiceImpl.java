package com.springai.springai.service.impl;

import core.pojo.entity.SystemRolePermission;
import com.springai.springai.mapper.SystemRolePermissionMapper;
import com.springai.springai.service.ISystemRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色-权限关联表 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class SystemRolePermissionServiceImpl extends ServiceImpl<SystemRolePermissionMapper, SystemRolePermission>
		implements ISystemRolePermissionService {

}
