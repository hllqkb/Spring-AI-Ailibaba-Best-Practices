package core.service.objectstore;

/**
 * @Project: 
 * @Author: hllqkb
 * @Github: https://github.com/hllqkb
 * @Date: 2025/3/29 02:34
 * @Description:
 */
public interface StorageFile {

	String getId();

	String getBucketName();

	String getObjectName();

	String getContentType();

	String getFileName();

	String getPath();

	Long getSize();

	String getMd5();

}
