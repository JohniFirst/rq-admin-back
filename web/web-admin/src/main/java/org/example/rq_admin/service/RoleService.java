package org.example.rq_admin.service;

import org.example.rq_admin.entity.Role;
import org.example.rq_admin.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public void updateRole(Role role) {
        roleRepository.save(role);
    }

    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }

    public List<RoleSimpleInfo> getSimpleRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleSimpleInfo> roleSimpleInfos = new ArrayList<>();
        for (Role role : roles) {
            roleSimpleInfos.add(new RoleSimpleInfo(role.getId(), role.getRoleName()));
        }

        return roleSimpleInfos;
    }


}

