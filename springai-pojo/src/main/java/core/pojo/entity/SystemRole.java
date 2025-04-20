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
 * 系统角色表
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_role")
@ApiModel(value = "SystemRole对象", description = "系统角色表")
public class SystemRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "角色id")
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "角色名")
	private String name;

	@ApiModelProperty(value = "角色描述")
	private String description;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "更新时间")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "是否删除（false-未删除，true-已删除）")
	private Boolean deleted;

	@ApiModelProperty(value = "创建人")
	private String creator;

	@ApiModelProperty(value = "更新人")
	private String updater;

}
