package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "角色列表", description = "获取所有的角色列表")
    @GetMapping("/role/list")
    public List<Role> list() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "角色枚举", description = "获取角色的枚举，用户绑定")
    @GetMapping("/role-enum-list")
    public List<RoleSimpleInfo> getRoleEnumList() {
        return roleService.getSimpleRoles();
    }

    @Operation(summary = "根据id获取角色", description = "根据id获取角色")
    @GetMapping("/role{id}")
    public Role get(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @Operation(summary = "新增角色", description = "角色新增")
    @PostMapping("/role/add")
    public Role add(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @Operation(summary = "编辑角色", description = "编辑角色")
    @PutMapping("/role/update")
    public void update(@RequestBody Role role) {
        roleService.updateRole(role);
    }

    @Operation(summary = "删除角色", description = "根据id删除角色")
    @DeleteMapping("/role/{id}")
    public void del(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
