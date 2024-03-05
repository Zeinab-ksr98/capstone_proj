package com.dgpad.thyme.Email;

import com.dgpad.thyme.model.users.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailService {
    @Value("${app.sendgrid.key}")
    private String sendGrid;
    public String  senddetailsEmail( User user) throws IOException {
        Email from = new Email("sa7tak3ena.lb@gmail.com");
        String subject = "Welcome to Sa7tak3ena";
        Email to = new Email(user.email);
        Content content = new Content("text/html", "<div style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #333333; font-size: 24px; font-weight: bold; margin-bottom: 20px;\">Welcome to our New Community</h2>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">We are delighted to welcome you to our community dedicated to the noble cause of saving lives during the golden hours.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Your account is now set up and ready to go. To get started, please log in using your email and the default password: <strong>123</strong>.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Thank you for joining us in this important mission. Together, we can make a real difference.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Best regards,<br>The Community Team</p>" +
                        "</div>");
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGrid);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }
        catch (IOException ex)
        { throw ex;
        }
        return "an email has been sent please check your inbox";

    }

//    public String  sendEmail( String email) throws IOException {
//
//        Email from = new Email("Sa7tak3enalb@gmail.com");
//        String subject = "Please verify Your account";
//        Email to = new Email(email);
//        Content content = new Content("text/html", "<h2> Varify your account </h2><br><h3></h3>" );
//        Mail mail = new Mail(from, subject, to, content);
//        SendGrid sg = new SendGrid(sendGrid);
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//        }
//        catch (IOException ex)
//        { throw ex;
//        }
//        return "an email has been sent please check your inbox";
//
//    }


}
