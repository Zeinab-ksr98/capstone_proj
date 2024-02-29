package com.dgpad.thyme.Email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailService {
    @Value("${app.sendgrid.key}")
    private String sendGrid;
    public String  sendEmail(String email) throws IOException {

        Email from = new Email("Sa7tak3enalb@gmail.com");
        String subject = "Please verify Your account";
        Email to = new Email(email);
        Content content = new Content("text/html", "<h2> Varify your account </h2><br><h3></h3>" );
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGrid);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        }
        catch (IOException ex)
        { throw ex;
        }
        return "an email has been sent please check your inbox";

    }


}
