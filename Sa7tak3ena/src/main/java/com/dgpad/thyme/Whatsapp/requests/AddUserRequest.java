package com.dgpad.thyme.Whatsapp.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddUserRequest {
    @NotBlank(message = "number cannot be blank")
    private String number;
    @NotBlank(message = "password cannot be blank")
    private String password;
}
