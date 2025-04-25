package core.pojo.vo;

import lombok.Data;

/**
 * 资源对象
 */
@Data
public class ResourceVO {
    //资源ID
    private String resourceId;
    //资源名称
    private String fileName;
    //资源类型
    private String fileType;
    //资源路径
    private String filePath;
    //资源大小
    private long fileSize;
}
