package org.example.rq_admin.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Long id;
    private String title;
    private String url;
    private String icon;
    private Integer menuOrder;
    private Long parent;
    private Long key;
    private List<RoleInfoDTO> roles = new ArrayList<>();  // 确保 roles 列表被初始化

    public boolean hasChildren() {
        return children!= null &&!children.isEmpty();
    }

    private List<MenuDTO> children;

}