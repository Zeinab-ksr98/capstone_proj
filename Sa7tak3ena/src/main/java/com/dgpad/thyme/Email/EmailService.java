package com.dgpad.thyme.Email;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {
    @Value("${app.sendgrid.key}")
    private String sendGrid;
    public String  senddetailsEmail( String email, int i) throws IOException {
        Email from = new Email("sa7tak3ena.lb@gmail.com");
        String subject = "Welcome to Sa7tak3ena";
        Email to = new Email(email);
        Content content;
        switch (i) {
            case 1:
                content = new Content("text/html", "<div style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #333333; font-size: 24px; font-weight: bold; margin-bottom: 20px;\">Welcome to our New Community</h2>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">We are delighted to welcome you to our community dedicated to the noble cause of saving lives during the golden hours.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Your account is now set up and ready to go. To get started, please log in using your email and the default password: <strong>123</strong>.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Thank you for joining us in this important mission. Together, we can make a real difference.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Best regards,<br>The Community Team</p>" +
                        "</div>");
                break;
            case 2:
                content = new Content("text/html", "<div style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #B03A2E; font-size: 24px; font-weight: bold; margin-bottom: 20px;\">Account Deactivation Notification</h2>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">We regret to inform you that your account has been deactivated.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">If you believe this deactivation is a mistake, please contact our support team immediately.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Thank you for your understanding.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Best regards,<br>The Sa7tak3ena Team</p>" +
                        "</div>");
                break;
            case 3:
                content = new Content("text/html", "<div style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #B03A2E; font-size: 24px; font-weight: bold; margin-bottom: 20px;\">Request Recieved</h2>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">please check your account a request has been recieved</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">pending request will be canceled within 10 min</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Thank you for saving lives.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Best regards,<br>The Sa7tak3ena Team</p>" +
                        "</div>");
                break;
            default:
                content = new Content("text/html", "<div style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #0E6EB8; font-size: 24px; font-weight: bold; margin-bottom: 20px;\">Account Activation Notification</h2>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Your account has been successfully activated.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">You can now log in and start using our services.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">If you have any questions or need assistance, feel free to contact our support team.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Thank you for choosing us.</p>" +
                        "<p style=\"color: #666666; font-size: 16px; line-height: 1.5;\">Best regards,<br>The Sa7tak3ena Team</p>" +
                        "</div>");

        }

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

    public String  sendEmail( String email, String subject, String massage) throws IOException {

        Email from = new Email("sa7tak3ena.lb@gmail.com");
        Email to = new Email(email);
        Content content = new Content("text/html", "<div style='font-family: Arial, sans-serif; color: #154360;'>"
                + "<p>Dear User,</p>"
                + "<p>We would like to inform you about the following:</p>"
                + "<p style='color: #21618C;'>" + massage + "</p>"
                + "<p>If you have any questions or concerns, feel free to contact us.</p>"
                + "<p style='color: #21618C;'>Best regards,</p>"
                + "<p style='color: #21618C;'>The Sa7tak3ena Team</p>"
                + "</div>");
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


