package org.example.rq_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @GetMapping("/menu/list")
    private List<MenuDTO> getAllMenus(@RequestParam(required = false) MenuDTO menu) {
//        LambdaQueryWrapper<MenuDTO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(menu.getTitle() != null, MenuDTO::getTitle, menu.getTitle());
//        queryWrapper.eq(menu.getUrl() != null, MenuDTO::getUrl, menu.getUrl());

        return menuService.getMenusWithRolesDTO();
    }

    @GetMapping("/menu/without-role")
    private List<Menu> getMenusCommonList() {
        return menuService.getMenusWithChildren();
    }

    @GetMapping("/menu/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    @PostMapping("/menu-add")
    public void menuAdd(@RequestBody Menu menu) {
        menuService.addMenu(menu);
    }

    @PutMapping("/menu-update")
    public void updateMenu(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
    }

    @DeleteMapping("/menu/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
