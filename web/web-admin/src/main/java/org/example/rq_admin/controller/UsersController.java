package org.example.rq_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.PaginationConfig;
import org.example.rq_admin.DTO.UserLoginDTO;
import org.example.rq_admin.entity.Users;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.mapper.UsersMapper;
import org.example.rq_admin.repository.UsersRepository;
import org.example.rq_admin.service.UsersService;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户相关的接口，包括登录、注册、用户管理等功能")
@RestController
//@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    public UsersController(UsersService usersService, UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "400", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public FormatResponseData handleLogin(@Valid @RequestBody UserLoginDTO user) {
        if (!usersService.handleLogin(user.getUsername(), user.getPassword())) {
            return new FormatResponseData(ResponseStatus.FAILURE, "用户名或密码错误");
        }
        return new FormatResponseData(ResponseStatus.SUCCESS, "登录成功");
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "用户名已存在")
    })
    @PostMapping("/register")
    public FormatResponseData register(@Valid @RequestBody Users user) {
        if (usersService.checkUserExists(user.getUsername())) {
            return new FormatResponseData(ResponseStatus.FAILURE, "用户名已存在");
        }
        usersService.saveUsers(user);
        return new FormatResponseData(ResponseStatus.SUCCESS, "注册成功");
    }

    @Operation(summary = "审批用户", description = "审批用户并分配角色")
    @PutMapping("/user/{userId}/approve")
    public FormatResponseData approveUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "角色ID列表") @RequestBody List<Long> roleIds
    ) {
        usersService.approveUser(userId, roleIds);
        return new FormatResponseData(ResponseStatus.SUCCESS);
    }

    @Operation(summary = "获取用户列表", description = "分页获取所有用户")
    @PostMapping("/user/list")
    public FormatResponseData<PaginationConfig<Users>> getAllUsers(
            @Parameter(description = "页码（从1开始）") @RequestParam(defaultValue = "1") Integer pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PaginationConfig<Users> users = usersService.getAllUser(pageNumber, pageSize);

        return new FormatResponseData<>(ResponseStatus.SUCCESS, users);
    }

    @Operation(summary = "修改用户状态", description = "启用或禁用用户")
    @PutMapping("/user/update")
    public FormatResponseData updateUserIsEnabled(@Valid @RequestBody Users user) {
        return usersService.updateUserIsEnabled(user.getId(), user.getIsEnabled());
    }

    @Operation(summary = "模糊查询用户列表", description = "支持用户名的模糊查询")
    @GetMapping("/user/query/{username}")
    public FormatResponseData<List<Users>> queryUsers(@Parameter(description = "用户名") @PathVariable String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);

        List<Users> usersList = usersMapper.selectList(queryWrapper);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, usersList);
    }
}
