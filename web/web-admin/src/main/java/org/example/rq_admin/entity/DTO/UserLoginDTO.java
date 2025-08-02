package org.example.rq_admin.entity.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @Schema(description = "用户名")
    @NotNull
    private String username;

    @Schema(description = "密码")
    @NotNull
    private String password;

    @Schema(description = "验证码")
    @NotNull
    private String verificationCode;

}
