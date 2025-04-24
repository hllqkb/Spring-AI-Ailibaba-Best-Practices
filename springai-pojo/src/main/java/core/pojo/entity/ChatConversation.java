package core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 对话表
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_conversation")
@ApiModel(value = "ChatConversation对象", description = "对话表")
public class ChatConversation implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "信息ID，唯一标识")
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "发起人")
	private Long userId;


	@ApiModelProperty(value = "记录创建时间")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "记录更新时间")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "是否被逻辑删除（软删除）")
	private Boolean deleted;
	// 创建人字段，自动填充
	@TableField(fill= FieldFill.INSERT)
	private String creator;
	// 更新人字段，自动填充
	@TableField(fill= FieldFill.UPDATE)
	private String updater;

}
