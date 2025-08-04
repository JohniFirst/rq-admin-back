package org.example.rq_admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.rq_admin.entity.UserInfo;
import org.example.rq_admin.mapper.UsersInfoMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 注入用户数据访问层（根据实际项目调整）
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersInfoMapper usersInfoMapper; // 假设的用户数据访问接口

    public UserDetailsServiceImpl(UsersInfoMapper usersInfoMapper) {
        this.usersInfoMapper = usersInfoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        UserInfo user = usersInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：: " + username);
        }

        // 转换为Spring Security的UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 数据库中存储的应该是加密后的密码
                .roles(user.getRoles().toArray(new String[0])) // 角色集合转换为数组
                .build();
    }
}
    