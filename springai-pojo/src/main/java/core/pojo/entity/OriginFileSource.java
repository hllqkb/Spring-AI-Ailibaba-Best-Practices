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
import core.service.objectstore.StorageFile;

/**
 * <p>
 * 存储原始文件资源的表
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("origin_file_source")
@ApiModel(value = "OriginFileSource对象", description = "存储原始文件资源的表")
public class OriginFileSource implements Serializable, StorageFile {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "文件唯一标识")
	@TableId(value = "id", type = IdType.AUTO)
	private String id;

	@ApiModelProperty(value = "文件名")
	private String fileName;

	@ApiModelProperty(value = "文件存储路径")
	private String path;

	@ApiModelProperty(value = "是否为图片文件")
	private Boolean isImage;

	@ApiModelProperty(value = "对象存储桶名称")
	private String bucketName;

	@ApiModelProperty(value = "对象存储中的文件名")
	private String objectName;

	@ApiModelProperty(value = "文件的 MIME 类型")
	private String contentType;

	@ApiModelProperty(value = "文件大小（字节）")
	private Long size;

	@ApiModelProperty(value = "文件 MD5 哈希值")
	private String md5;

	@ApiModelProperty(value = "文档内包含的图片列表（JSON 数组）")
	private String images;

	@ApiModelProperty(value = "记录创建时间")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "记录更新时间")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "是否被逻辑删除（软删除）")
	private Boolean deleted;

	private String creator;

	private String updater;

}
