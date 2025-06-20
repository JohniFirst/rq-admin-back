package org.example.rq_admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private List<Users> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_menus",
    joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<Menu> menus;

    // 添加一个接受 id 参数的构造函数
    public Role(int id) {
        this.id = id;
    }

    // 无参构造函数，确保 JPA 正常工作
    public Role() {}
}
