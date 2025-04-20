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
 * 用户-角色关联表
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_user_role")
@ApiModel(value = "SystemUserRole对象", description = "用户-角色关联表")
public class SystemUserRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "用户ID")
	private Long userId;

	@ApiModelProperty(value = "角色ID")
	private Long roleId;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	private Boolean deleted;

	@ApiModelProperty(value = "创建人")
	private String creator;

	private String updater;

}
