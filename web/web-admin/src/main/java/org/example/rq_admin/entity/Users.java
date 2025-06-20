package org.example.rq_admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.IsEnabled;

import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    // 是否启用
    private int isEnabled;
    // 角色id
    private BigInteger roleId;
    // 部门id
    private BigInteger departmentId;

    private boolean auditStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    public Users() {}

    public Users(String name, String password) {
        IsEnabled enabled = IsEnabled.ENABLED;

        this.username = name;
        this.password = password;
        this.isEnabled = enabled.getStatusCode();
        this.auditStatus = false;
    }
}
