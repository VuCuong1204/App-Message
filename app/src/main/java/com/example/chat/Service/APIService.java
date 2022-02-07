package com.example.chat.Service;

public class APIService {

    private static String url = "https://fcm.googleapis.com/";

    public static DataService getService(){
         return APIRetrofitClient.getClient(url).create(DataService.class);
    }
}
