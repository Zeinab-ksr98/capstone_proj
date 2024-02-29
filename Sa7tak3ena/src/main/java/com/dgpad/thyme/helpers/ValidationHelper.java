package com.dgpad.thyme.helpers;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
public class ValidationHelper {
    public static boolean isValidEmail(String email) {
        boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            return false;
        }
        return isValid;
    }
}
