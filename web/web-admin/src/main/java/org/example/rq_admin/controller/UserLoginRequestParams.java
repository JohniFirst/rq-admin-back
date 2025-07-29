package org.example.rq_admin.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginRequestParams {

    @NotNull
    private String username;

    @NotNull
    private String password;

}
