package com.dgpad.thyme.Whatsapp;
import com.dgpad.thyme.Whatsapp.requests.VerifyUserRequest;
import com.dgpad.thyme.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class  WhatsappController{
    @Autowired
    private PatientService patientService;
    @Autowired
    private VerificationSender verificationSender;
    @GetMapping(value = "/verify-user")
    public String  verifyUer(@Valid @ModelAttribute VerifyUserRequest verifyUserRequest){
        try {
            if (patientService.verifyUser(verifyUserRequest.getNumber(), verifyUserRequest.getCode())){
                verificationSender.sendVerificationCode(verifyUserRequest.getNumber()," We are delighted to welcome you to our community dedicated to the noble cause of saving lives during the golden hours,Your account is now set up and ready to go. To get started, please log in and verify your profile to move forward ");
                return  "redirect:/home";
            }
            return "account/verificationError";

        }catch (Exception e){
            return "account/error";
        }
    }
}
