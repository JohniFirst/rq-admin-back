package org.example.rq_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.entity.UserInfo;
import org.example.rq_admin.mapper.UsersInfoMapper;
import org.example.rq_admin.service.UsersInfoService;
import org.springframework.stereotype.Service;

@Service
public class UsersInfoServiceImpl extends ServiceImpl<UsersInfoMapper, UserInfo> implements UsersInfoService {

    public UsersInfoServiceImpl(UsersInfoMapper usersInfoMapper) {
        this.usersInfoMapper = usersInfoMapper;
    }

    private final UsersInfoMapper usersInfoMapper;

    /**
     * @param userLoginDTO 用户登录的用户名密码
     * @return 数据库查询回来的用户名密码
     */
    @Override
    public Boolean checkLoginInfo(UserLoginDTO userLoginDTO) {
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userInfoLambdaQueryWrapper
                .eq(UserInfo::getUsername, userLoginDTO.getUsername())
                .eq(UserInfo::getPassword, userLoginDTO.getPassword());

        return usersInfoMapper.selectCount(userInfoLambdaQueryWrapper) > 0;
    }

    /**
     * @param username 用户名
     * @return 是否存在用户名
     */
    @Override
    public Boolean checkUserExists(String username) {

        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userInfoLambdaQueryWrapper.eq(UserInfo::getUsername, username);

        Long l = usersInfoMapper.selectCount(userInfoLambdaQueryWrapper);

        return l > 0;
    }

}
