package core.common;

import lombok.Data;

/**
 * @Project: 
 * @Author: hllqkb
 * @Github: https://github.com/hllqkb
 * @Date: 2025/3/30 16:56
 * @Description: * 全局错误码，占用 [0, 999],业务异常错误码，占用 [1 000 000 000, +∞)
 *
 */
@Data
public class ErrorCode {

	/**
	 * 错误码
	 */
	private final Integer code;

	/**
	 * 错误提示
	 */
	private final String msg;

	public ErrorCode(Integer code, String message) {
		this.code = code;
		this.msg = message;
	}

}
