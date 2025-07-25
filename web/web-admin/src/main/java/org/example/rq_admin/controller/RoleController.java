package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.entity.Role;
import org.example.rq_admin.service.RoleService;
import org.example.rq_admin.service.RoleSimpleInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "系统角色管理的相关接口")
@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/role/list")
    public List<Role> list() {
        return roleService.getAllRoles();
    }

    @GetMapping("/role-enum-list")
    public List<RoleSimpleInfo> getRoleEnumList() {
        return roleService.getSimpleRoles();
    }

    @GetMapping("/role{id}")
    public Role get(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PostMapping("/role/add")
    public Role add(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @PutMapping("/role/update")
    public void update(@RequestBody Role role) {
        roleService.updateRole(role);
    }

    @DeleteMapping("/role/{id}")
    public void del(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
