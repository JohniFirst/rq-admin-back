package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.DTO.MenuDTO;
import org.example.rq_admin.entity.Menu;
import org.example.rq_admin.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单接口")
@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "菜单列表", description = "获取当前用户菜单")
    @GetMapping("/menu/list")
    private List<MenuDTO> getAllMenus(@RequestParam(required = false) MenuDTO menu) {
//        LambdaQueryWrapper<MenuDTO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(menu.getTitle() != null, MenuDTO::getTitle, menu.getTitle());
//        queryWrapper.eq(menu.getUrl() != null, MenuDTO::getUrl, menu.getUrl());

        return menuService.getMenusWithRolesDTO();
    }

    @Operation(summary = "所有菜单列表", description = "获取系统所有菜单")
    @GetMapping("/menu/without-role")
    private List<Menu> getMenusCommonList() {
        return menuService.getMenusWithChildren();
    }

    @Operation(summary = "用户菜单", description = "获取当前用户菜单")
    @GetMapping("/menu/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    @Operation(summary = "新增菜单", description = "新增一个用户菜单")
    @PostMapping("/menu-add")
    public void menuAdd(@RequestBody Menu menu) {
        menuService.addMenu(menu);
    }

    @Operation(summary = "修改菜单", description = "修改当前菜单项")
    @PutMapping("/menu-update")
    public void updateMenu(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
    }

    @Operation(summary = "删除菜单", description = "使用id删除菜单")
    @DeleteMapping("/menu/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
