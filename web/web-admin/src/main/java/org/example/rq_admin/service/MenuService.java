package org.example.rq_admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.rq_admin.DTO.MenuDTO;
import org.example.rq_admin.DTO.RoleInfoDTO;
import org.example.rq_admin.entity.Menu;
import org.example.rq_admin.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * 获取所有菜单
     */
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    /**
     * 根据id查询菜单
     */
    public Menu getMenuById(long id) {
        return menuRepository.findById(id).orElse(null);
    }

    /**
     * 添加菜单
     */
    public void addMenu(Menu menu) {
        menuRepository.save(menu);
    }

    /**
     * 更新菜单
     */
    public void updateMenu(Menu menu) {
        menuRepository.save(menu);
    }

    /**
     * 删除菜单
     */
    public void deleteMenu(long id) {
        menuRepository.deleteById(id);
    }

    public List<Menu> getMenusWithChildren() {
        List<Menu> menus = menuRepository.findAll();
        return buildMenuTree(menus);
    }

    private List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> rootMenus = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParent() == null) {
                rootMenus.add(menu);
            } else {
                Menu parent = findParent(menu.getParent(), menus);
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }
        return rootMenus;
    }

    private Menu findParent(Long parentId, List<Menu> menus) {
        for (Menu menu : menus) {
            if (menu.getId().equals(parentId)) {
                return menu;
            }
        }
        return null;
    }

    public List<MenuDTO> getMenusWithRolesDTO() {
        List<Menu> menus = menuRepository.findAll();
        return buildMenuTreeDTO(menus);
    }

    private List<MenuDTO> buildMenuTreeDTO(List<Menu> menus) {
        Map<Long, MenuDTO> menuDTOMap = new HashMap<>();

        for (Menu menu : menus) {
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.setId(menu.getId());
            menuDTO.setTitle(menu.getTitle());
            menuDTO.setUrl(menu.getUrl());
            menuDTO.setIcon(menu.getIcon());
            menuDTO.setMenuOrder(menu.getMenuOrder());
            menuDTO.setParent(menu.getParent());
            menuDTO.setKey(menu.getId());

            // 检查角色是否为空，如果不为空则进行转换和设置
            if (menu.getRoles() != null && !menu.getRoles().isEmpty()) {
                menuDTO.setRoles(menu.getRoles().stream()
                        .map(role -> new RoleInfoDTO(role.getId(), role.getRoleName()))
                        .collect(Collectors.toList()));
            }

            menuDTOMap.put(menu.getId(), menuDTO);
        }

        List<MenuDTO> rootMenus = new ArrayList<>();

        for (MenuDTO menuDTO : menuDTOMap.values()) {
            Long parentId = menuDTO.getParent();
            if (parentId == null) {
                rootMenus.add(menuDTO);
            } else {
                MenuDTO parentMenuDTO = menuDTOMap.get(parentId);
                if (parentMenuDTO != null) {
                    if (parentMenuDTO.getChildren() == null) {
                        parentMenuDTO.setChildren(new ArrayList<>());
                    }
                    parentMenuDTO.getChildren().add(menuDTO);
                }
            }
        }

        return new ArrayList<>(rootMenus);
    }
}
