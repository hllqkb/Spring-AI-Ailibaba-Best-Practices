package core.pojo.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
/**
 * UserLoginVO
 * @author hllqk
 * */
@Data
public class UserRegisterVO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 30, message = "用户名长度必须介于5到30之间")
    private String username;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 6, max = 24, message = "密码长度必须介于6到24之间")
    private String password;


}
