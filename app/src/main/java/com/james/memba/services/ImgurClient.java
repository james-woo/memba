package com.james.memba.services;

import com.james.memba.utils.KeyUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImgurClient {

    private final String mBaseUrl = "https://api.imgur.com";
    private OkHttpClient mHttpClient;

    public ImgurClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mHttpClient = builder.build();
    }

    public synchronized String postImage(File image) {
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", image.getName(), RequestBody.create(MediaType.parse("image/*"), image))
                    .build();

            Request request = new Request.Builder()
                    .url(mBaseUrl + "/3/image")
                    .addHeader("Authorization", KeyUtil.getImgurClientAuth())
                    .post(requestBody)
                    .build();

            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject res = new JSONObject(response.body().string());
            return res.getJSONObject("data").getString("link");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
