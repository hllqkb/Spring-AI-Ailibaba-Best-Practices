package core.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色-权限关联表
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_role_permission")
@ApiModel(value = "SystemRolePermission对象", description = "角色-权限关联表")
public class SystemRolePermission implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "角色ID")
	private Long roleId;

	@ApiModelProperty(value = "权限ID")
	private Long permissionId;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	private Boolean deleted;

	@ApiModelProperty(value = "创建人")
	private String creator;

	private String updater;

}
