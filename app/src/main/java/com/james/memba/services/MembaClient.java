package com.james.memba.services;

import android.support.annotation.Nullable;

import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.model.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class MembaClient {
    private final String mBaseUrl = "https://membame.herokuapp.com/api/";

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

    public ArrayList<Berry> getBerries(Account account) {
        ArrayList<Berry> berries = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    //.url(mBaseUrl + "users/" + account.getUserId() + "/berries")
                    .url(mBaseUrl + "users/123456789/berries")
                    .build();
            Response responses = null;

            try {
                responses = mHttpClient.newCall(request).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String data = responses.body().string();
            JSONArray jArray = new JSONArray(data);
            for (int i = 0; i < jArray.length(); i++) {
                berries.add(JSONToBerry(jArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return berries;
    }

    public Berry getBerry(String berryId) {
        Berry berry = null;

        try {
            Request request = new Request.Builder()
                    .url(mBaseUrl + "berry/" + berryId)
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

    private Berry JSONToBerry(JSONObject object) {
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
