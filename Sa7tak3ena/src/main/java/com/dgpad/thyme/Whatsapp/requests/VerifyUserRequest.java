package com.dgpad.thyme.Whatsapp.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserRequest {
    @NotBlank(message = "number cannot be blank")
    private String number;
    @NotBlank(message = "code cannot be blank")
    private String code;
}
