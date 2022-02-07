package com.example.chat.Service;

import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DataService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAwc5eEsE:APA91bEwXKGuKlnCag57JBfrtYcxL1F-dIR5bBUF86oIv5yEv62OeucEDaBS-F0XqLlIOzvBB5E0lolapyoI0R5IDzP-sZfQMpdbt7jSpALopgC-IzdTJnGfCLjvOxJFe4Fi6XaY-bl6"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);

}
