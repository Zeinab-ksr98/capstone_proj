package com.dgpad.thyme.Whatsapp;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Service;

@Service
public class VerificationSender {
    private final OkHttpClient client;

    private VerificationSender(){
        this.client = new OkHttpClient();
    }
    public boolean sendVerificationCode(String number, String message){
        try {
            String url = String.format("http://localhost:3000/send-message?number=%s&message=%s", number, message);

            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody.contains("Message sent successfully");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
