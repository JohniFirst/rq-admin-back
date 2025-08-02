package org.example.rq_admin.entity.DTO;

import lombok.Data;

@Data
public class UserRegisterDTO {
    // 用户名
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
