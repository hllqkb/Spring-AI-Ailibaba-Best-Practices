package core.pojo.vo;

import core.pojo.entity.KnowledgeBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class KnowLedgeBaseVO {
    private String id;
    @NotBlank(message = "知识库名称不能为空")
    private String name;
    private String description;

}
