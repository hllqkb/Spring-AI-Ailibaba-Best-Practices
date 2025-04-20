package core.pojo.vo;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hllqk
 * @description 前端展示的对话对象
 */
@Data
public class ChatRequestVO {

	@NotNull(message = "对话id不为空")
	private String conversationId;

	@NotNull(message = "对话内容不为空")
	private String content;

	private List<String> resourceIds;

	private List<String> knowledgeIds;

	@NotNull(message = "对话类型不为空")
	private String chatType;

}
