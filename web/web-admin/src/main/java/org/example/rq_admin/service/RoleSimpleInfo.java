package org.example.rq_admin.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleSimpleInfo {
    private int value;
    private String label;

    public RoleSimpleInfo(int id, String roleName) {
        this.value = id;
        this.label = roleName;
    }
}
