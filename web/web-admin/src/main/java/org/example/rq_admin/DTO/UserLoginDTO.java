package org.example.rq_admin.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;

}
