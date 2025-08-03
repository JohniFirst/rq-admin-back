package org.example.rq_admin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息实体类，用于接收和返回用户相关数据")
public class ThymeleafFormTest {

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "年龄", example = "18")
    private int age;

    @Schema(description = "电子邮箱", example = "1733098850@qq.com")
    private String email;

    @Schema(description = "备注信息")
    private String remarks;
}
