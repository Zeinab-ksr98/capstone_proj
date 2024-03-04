//package com.dgpad.thyme.Email;
//
//import com.dgpad.thyme.model.users.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//public class EmailController {
//    @Autowired
//    EmailService emailService;
//    @GetMapping("/sendEmail/{email}")
//    public String sendEmail(@PathVariable (value = "email",required = true) String email, User user) throws IOException {
//        return emailService.sendEmail(email);
//    }
//}
