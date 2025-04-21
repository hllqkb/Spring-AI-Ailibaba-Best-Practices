package core.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatConversationVO {
    private String id;
    private String title;
    private LocalDateTime createTime;
    private List<ChatMessageVO> messages;
}
