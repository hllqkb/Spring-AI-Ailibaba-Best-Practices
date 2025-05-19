package core.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import core.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "document_entity")
public class DocumentEntity extends BaseEntity {

	/**
	 *
	 */
	@TableId(value = "id")
	private Long id;

	/**
	 *
	 */
	@TableField(value = "file_name")
	private String fileName;

	/**
	 *
	 */
	@TableField(value = "path")
	private String path;

	/**
	 * 是否存储到了向量数据库中
	 */
	@TableField(value = "is_embedding")
	private Boolean isEmbedding;

	/**
	 * 知识库ID
	 */
	@TableField(value = "base_id")
	private Long baseId;

	/**
	 * 资源ID
	 */
	@TableField(value = "resource_id")
	private String resourceId;

}
