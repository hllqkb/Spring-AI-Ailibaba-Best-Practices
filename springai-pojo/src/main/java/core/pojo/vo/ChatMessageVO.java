package core.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 对话消息给前端的VO对象
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
public class ChatMessageVO implements Serializable {

    @ApiModelProperty(value = "信息ID，唯一标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "对话ID")
    private String conversationId;
    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "角色，发生消息的角色")
    private String role;
    //前端展示聊天引用了什么资源
    @ApiModelProperty(value = "资源ID")
    private List<String> resourceIds;
    //连表查询时使用
    @ApiModelProperty(value = "资源列表")
    private List<ResourceVO> resources;
/*
	@ApiModelProperty(value = "消息序列号")
	private Integer messageNo;*/
/*
	@ApiModelProperty(value = "是否携带附件")
	private Boolean hasMedia;*/



//	private Boolean isClean;
//
//	@ApiModelProperty(value = "记录创建时间")
//	private LocalDateTime createTime;
//
//	@ApiModelProperty(value = "记录更新时间")
//	private LocalDateTime updateTime;

//	@ApiModelProperty(value = "是否被逻辑删除（软删除）")
//	private Boolean deleted;
//
//	private String creator;
//
//	private String updater;

}
