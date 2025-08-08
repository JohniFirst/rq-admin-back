package org.example.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserInfo {

    @NotNull(message = "请提供用户名")
    private String username;

    @NotNull(message = "请提供密码")
    private String password;
}
