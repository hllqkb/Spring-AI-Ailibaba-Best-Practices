package core.pojo;

import lombok.Data;

@Data
public class PageParam {

	private static final Integer PAGE_NO = 1;

	private static final Integer PAGE_SIZE = 10;

	public static final Integer PAGE_SIZE_NONE = -1;

	private Integer pageNo = PAGE_NO;

	private Integer pageSize = PAGE_SIZE;

}
