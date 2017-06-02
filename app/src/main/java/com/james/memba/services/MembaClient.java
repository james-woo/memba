package com.james.memba.services;

import android.support.annotation.Nullable;

import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.model.Location;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

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

    public void createAccount(String id) {
        String postBody = "{\"userId\":" + id + ", \"berries\":\"[]\"}";
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

    public void getBerries(Account account, Callback cb) {
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

    public Berry getBerry(String berryId) {
        Berry berry = null;

        try {
            Request request = new Request.Builder()
                    .url(mBaseUrl + "berry/" + berryId)
                    .get()
                    .build();
            Response responses = null;

            try {
                responses = mHttpClient.newCall(request).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String data = responses.body().string();
            JSONObject jObject = new JSONObject(data);

            berry = JSONToBerry(jObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return berry;
    }

    public void createBerry(Account account, Berry berry) {
        String postBody = "{\"userId\":" + account.getUserId() + ", " +
                          " \"image\":" + berry.getImage() +
                          " \"description\":" + berry.getDescription() +
                          " \"location\":{" +
                                "\"lat\":" + berry.getLocation().lat +
                                "\"lng\":" + berry.getLocation().lng +
                          "}}";

        try {
            Request request = new Request.Builder()
                    .url(mBaseUrl + "berries/")
                    .post(RequestBody.create(MediaType.parse("application/json"), postBody))
                    .build();

            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Berry JSONToBerry(JSONObject object) {
        Berry berry = null;

        try {
            String id = object.getString("_id");
            String user = object.getString("userId");
            String image = object.getString("image");
            String description = object.getString("description");
            long date = object.getLong("createDate");
            JSONObject lObject = object.getJSONObject("location");
            Location location = new Location(lObject.getDouble("lat"), lObject.getDouble("lng"));

            berry = new Berry(id, user, image, description, new Date(date), location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return berry;
    }
}
