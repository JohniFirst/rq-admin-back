package org.example.rq_admin.entity.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @Schema(description = "用户名")
    @NotNull
    private String username;

    // 密码
    private String password;
    // 电子邮箱
    private String email;
    // 电话号码
    private String phone;
    // 昵称
    private String nickname;
    // 头像
    private String avatar;
}
