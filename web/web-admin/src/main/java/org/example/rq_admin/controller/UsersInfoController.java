package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.entity.DTO.UserRegisterDTO;
import org.example.rq_admin.entity.UserInfo;
import org.example.rq_admin.enums.ResponseStatus;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.impl.UsersInfoServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

// 约定 放在这个模块的都是不需要认证的接口，以auth开头
@Tag(name = "用户认证", description = "包括登录、注册等功能")
@RestController
@RequestMapping("/auth")
public class UsersInfoController {

    private final UsersInfoServiceImpl usersInfoService;

    public UsersInfoController(UsersInfoServiceImpl usersInfoService) {
        this.usersInfoService = usersInfoService;
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public FormatResponseData handleLogin(@RequestBody UserLoginDTO user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "请输入用户名或密码");
        }

        String token = usersInfoService.checkLoginInfo(user);

        if (Objects.equals(token, "")) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "请输入正确的用户名或密码");
        } else {
            return new FormatResponseData<>(ResponseStatus.SUCCESS, "登录成功", token);
        }
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public FormatResponseData handleRegistry(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO.getUsername().isEmpty()) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "用户名不能为空");
        }

        if (usersInfoService.checkUserExists(userRegisterDTO.getUsername())) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "用户名已经被注册");
        }

        UserInfo userInfo = new UserInfo();

        BeanUtils.copyProperties(userRegisterDTO, userInfo);

        boolean save = usersInfoService.save(userInfo);

        return new FormatResponseData<>(save ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }


}
