package com.example.heartcare;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    //interface de création de requêtes

    @POST("/login")
    Call<User> basicLogin(@Body User user);

    @Multipart
    @POST("/set_data")
    Call<ResponseBody> uploadData(
        @Part MultipartBody.Part text
    );
}
