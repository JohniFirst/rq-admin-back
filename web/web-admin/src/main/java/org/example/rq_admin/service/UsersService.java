package org.example.rq_admin.service;

import org.example.PaginationConfig;
import org.example.rq_admin.entity.Role;
import org.example.rq_admin.entity.Users;
import org.example.enums.IsEnabled;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.repository.RoleRepository;
import org.example.rq_admin.repository.UsersRepository;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    public UsersService(UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 保存用户到数据库
     */
    public void saveUsers(Users user) {
        usersRepository.save(user);
    }

    /** 查询待审核的列表 */
    public List<Users> getPendingAuditUsers() {
        return usersRepository.findByAuditStatus(false);
    }

    /** 审核用户 */
    public void approveUser(Long userId, List<Long> roleIds) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAuditStatus(true);

        List<Role> roles = roleRepository.findAllById(roleIds);
        user.setRoles(roles);

        usersRepository.save(user);
    }

    /**
     * 判断该用户是否已经注册
     *
     * @param username 用户名
     */
    public boolean checkUserExists(String username) {
        return usersRepository.existsByUsername(username);
    }

    /**
     * 判断密码是否正确
     *
     * @param username 用户名
     * @param password 密码
     */
    public boolean checkPassword(String username, String password) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return false; // 用户不存在
        }

        // 在此处进行密码的比较和验证逻辑
        return password.equals(user.getPassword());
    }

    /**
     * 判断用户的登录密码是否正确，判断用户 是否启用
     * @param username 用户名
     * @param password 密码
     * @return boolean
     */
    public boolean handleLogin(String username, String password) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return false; // 用户不存在
        }

        IsEnabled enabled = IsEnabled.ENABLED;

        // 在此处进行密码的比较和验证逻辑
        return password.equals(user.getPassword()) && (enabled.getStatusCode() == user.getIsEnabled());
    }

    /**
     * 查询所有用户
     * @param pageNumber 页码（从1开始）
     * @param pageSize 每页大小
     */
    public PaginationConfig<Users> getAllUser(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageSize == null) {
            Page<Users> page = new PageImpl<>(usersRepository.findAll());
            return PaginationConfig.fromPage(page);
        }
        int springPageNumber = PaginationConfig.convertToSpringPageNumber(pageNumber);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(springPageNumber, pageSize, sort);
        Page<Users> page = usersRepository.findAll(pageRequest);
        return PaginationConfig.fromPage(page);
    }

    /**
     * 根据用户id，设置当前用户启用/禁用
     */
    public FormatResponseData updateUserIsEnabled(Long userId, int isEnabled) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setIsEnabled(isEnabled);
            usersRepository.save(user);
            return new FormatResponseData(ResponseStatus.SUCCESS);
        }

        return new FormatResponseData(ResponseStatus.FAILURE);
    }
}
