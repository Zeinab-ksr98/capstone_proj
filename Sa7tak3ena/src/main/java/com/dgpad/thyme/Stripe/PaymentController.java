package com.dgpad.thyme.Stripe;

import com.dgpad.thyme.Whatsapp.VerificationSender;
import com.dgpad.thyme.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationSender verificationSender;
    @PostMapping("/create-payment-intent")

    public StripeResponse createPaymentIntent(@RequestBody StripeRequest request)
            throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(request.getAmount() * 100L)
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                        .build();

        PaymentIntent intent = PaymentIntent.create(params);
        verificationSender.sendVerificationCode(userService.getCurrentUser().getPhone(),
                "Thank you for your generous donation to support our ambulance services. Your contribution helps us provide critical care to those in need. Wishing you a swift and smooth recovery."
        );

        return new StripeResponse(intent.getId(), intent.getClientSecret());

    }

}