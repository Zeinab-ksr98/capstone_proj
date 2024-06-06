package com.dgpad.thyme.Whatsapp;
import com.squareup.okhttp.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VerificationSender {
    private final OkHttpClient client;

    private VerificationSender(){
        this.client = new OkHttpClient();
    }
    public boolean sendVerificationCode(String number, String message){
        try {
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject object = new JSONObject();
            object.put("session","default");
            object.put("text",message);
            object.put("chatId","961"+number+"@c.us");

            RequestBody body = RequestBody.create(mediaType, object.toString());

            Request request = new Request.Builder()
                    .url("http://localhost:3000/api/sendText")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
              return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
