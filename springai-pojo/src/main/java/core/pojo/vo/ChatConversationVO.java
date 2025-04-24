package core.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatConversationVO {
    private Long id;
    private String title;
    //添加@JsonFormat注解，格式化日期时间，支持多种格式化模式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<ChatMessageVO> messages;
    @ApiModelProperty(value = "资源ID")
    private List<String> resourceIds;
    //连表查询时使用
    @ApiModelProperty(value = "资源列表")
    private List<ResourceVO> resources;
}
