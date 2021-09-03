package com.example.heartcare;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

class OkHttpRequest  {
    private String url;
    private OkHttpClient client;

    OkHttpRequest(String url, OkHttpClient client){
        this.client = client;
        this.url = url;
    }

    /**
     *
     * @param email email of the user
     * @param password password for the server side application
     * @param endpoint place the above parameters to be send to
     * @return a new call with body containing login credentials
     */
    okhttp3.Call basicLogin(String email, String password, String endpoint)  {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            json.put("password", password);
            json.put("email", email);
            RequestBody requestBody = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url+"/" + endpoint)
                    .post(requestBody)
                    .build();
            return client.newCall(request);

        }catch (JSONException j){
            Log.d("error", "json - " + j.toString());
            return null;
        }
    }

    /**
     * The function send to the request object url the file pointed by
     * @param fileUri -  file uri
     * @param  endpoint - endpoint of the server
     */
    Call uploadData(@NotNull Uri fileUri, String endpoint, Context context) {

        File file = FileUtils.getFile(context, fileUri);
        System.out.println(file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file,  MediaType.parse("text/plain")))
                .build();

        Request request = new Request.Builder()
                .url(url+'/'+endpoint)
                .post(requestBody)
                .build();
        return client.newCall(request);

    }

    /**
     *  Logging out from server
     * @param endpoint  logout endpoint
     * @return a call for the request
     */
    Call logout(String endpoint){
        FormBody.Builder builder = new FormBody.Builder();

        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url+"/" + endpoint)
                .post(formBody)
                .build();
        return client.newCall(request);
    }
}
