package com.dgpad.thyme.Stripe;

import jakarta.validation.constraints.*;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StripeRequest {

    private int amount;
    @Email
    private String email;
}