package core.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 对话消息
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_message")
@ApiModel(value = "ChatMessage对象", description = "对话消息")
public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "信息ID，唯一标识")
	@TableId(value = "id", type = IdType.AUTO)
	private String id;

	@ApiModelProperty(value = "对话ID")
	private String conversationId;

	@ApiModelProperty(value = "消息序列号")
	private Integer messageNo;

	@ApiModelProperty(value = "是否携带附件")
	private Boolean hasMedia;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "角色")
	private String role;
	/**
	 * TableField注解的typeHandler属性指定了JacksonTypeHandler，用于将List<String>类型字段序列化为JSON字符串，方便数据库字段映射
	 */
	@ApiModelProperty(value = "资源ID集合")
	@TableField(typeHandler = JacksonTypeHandler.class)
	private List<String> resourceIds=new ArrayList<>();

	private Boolean isClean;

	@ApiModelProperty(value = "记录创建时间")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "记录更新时间")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "是否被逻辑删除（软删除）")
	private Boolean deleted;

	private String creator;

	private String updater;

}
