package com.james.memba.services;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class MembaClient {
    //private final String mBaseUrl = "https://membame.herokuapp.com/api/";
    private final String mBaseUrl = "http://10.0.2.2:8080/api/";

    private OkHttpClient mHttpClient;

    public MembaClient(final String token) {
        mHttpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Nullable
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
            }
        }).build();
    }

    public void getAccount(String id, Callback cb) {
        Request request = new Request.Builder()
                .url(mBaseUrl + "users/" + id)
                .get()
                .build();

        mHttpClient.newCall(request).enqueue(cb);
    }

    public void createAccount(Account account) {
        Gson gson = new Gson();
        String postBody = gson.toJson(account);

        try {
            Request request = new Request.Builder()
                    .url(mBaseUrl + "users/")
                    .post(RequestBody.create(MediaType.parse("application/json"), postBody))
                    .build();

            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAccountUsername(Account account) {
        String postBody = "{\"userName\":" + account.getUsername() + "}";

        try {
            Request request = new Request.Builder()
                    .url(mBaseUrl + "users/")
                    .put(RequestBody.create(MediaType.parse("application/json"), postBody))
                    .build();

            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAccountBerries(Account account, Callback cb) {
        Request request = new Request.Builder()
                .url(mBaseUrl + "users/" + account.getUserId() + "/berries")
                .get()
                .build();

        mHttpClient.newCall(request).enqueue(cb);
    }

    /*
     * Get all berries for map
     * Only returns coordinates of berries, no description to increase speed
     */
    public void getBerries(Callback cb) {
        Request request = new Request.Builder()
                .url(mBaseUrl + "berries/")
                .get()
                .build();

        mHttpClient.newCall(request).enqueue(cb);
    }

    public void getBerry(String berryId, Callback cb) {
        Request request = new Request.Builder()
                .url(mBaseUrl + "berries/" + berryId)
                .get()
                .build();

        mHttpClient.newCall(request).enqueue(cb);
    }

    public void createBerry(Berry berry, Callback cb) {
        Gson gson = new Gson();
        String postBody = gson.toJson(berry);

        Request request = new Request.Builder()
                .url(mBaseUrl + "berries/")
                .post(RequestBody.create(MediaType.parse("application/json"), postBody))
                .build();

        mHttpClient.newCall(request).enqueue(cb);
    }
}
