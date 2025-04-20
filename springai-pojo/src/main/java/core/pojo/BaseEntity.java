package core.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	/**
	 * 是否删除
	 */
	@TableField(value = "deleted")
	private Boolean deleted;

	/**
	 * 创建人
	 */
	@TableField(value = "creator", fill = FieldFill.INSERT)
	private String creator;

	/**
	 * 更新人
	 */
	@TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
	private String updater;

}
