package core.pojo.vo;

import lombok.Data;

import java.util.List;


@Data
public class AuthVO {

	private String username;

	private String token;

	private List<String> roles; // 角色

}
