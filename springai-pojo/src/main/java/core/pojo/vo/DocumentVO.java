package core.pojo.vo;

import core.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentVO extends PageParam {

	private Long knowledgeBaseId;

	private Long id;

	private String fileName;

	/**
	 * 下载路径
	 */
	private String path;

	/**
	 * 是否存储到了向量数据库中
	 */
	private Boolean isEmbedding;

	/**
	 * 知识库ID
	 */
	private Long baseId;

	/**
	 * 知识库名称
	 */
	private String knowledgeBaseName;

	/**
	 * 文件类型
	 */
	private String fileType;

	/**
	 * 上传时间
	 */
	private LocalDateTime uploadTime;

}
