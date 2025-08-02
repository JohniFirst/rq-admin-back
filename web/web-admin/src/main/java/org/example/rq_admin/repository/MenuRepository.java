package org.example.rq_admin.repository;

import org.example.rq_admin.entity.Menu;
import org.example.rq_admin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 查询某个角色关联的菜单
    @Query("SELECT m FROM Menu m JOIN m.roles r WHERE r.id = :roleId")
    List<Menu> findMenusByRoleId(Long roleId);

    // 修改菜单与角色的关联关系
    @Modifying
    @Query("UPDATE Menu m SET m.roles = :newRoles WHERE m.id = :menuId")
    void updateMenuRoles(Long menuId, List<Role> newRoles);
}
