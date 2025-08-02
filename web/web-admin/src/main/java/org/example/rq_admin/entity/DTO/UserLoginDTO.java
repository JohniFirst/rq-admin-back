package org.example.rq_admin.entity.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;

}
