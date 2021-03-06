package com.james.memba.model;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account implements Serializable {

    /* Accounts for Memba. The userId is from Google+ api and the userName is also from Google+ api.
     * If the userName is ever changed, it will be updated. Berries stores the id's of all the users
     * berries.
     */

    @SerializedName("_id") @Expose private String mId;
    @SerializedName("userId") @Expose private String mUserId;
    @SerializedName("userName") @Expose private String mUsername;
    @SerializedName("createDate") @Expose private String mCreateDate;
    @SerializedName("berries") @Expose private List<Object> mBerries = null;

    private final static long serialVersionUID = -6126086970311859495L;

    /**
     * No args constructor for use in serialization
     */
    public Account() {
    }

    public Account(String userId) {
        super();
        this.mUserId = userId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Account withId(String id) {
        this.mId = id;
        return this;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public Account withUserId(String userId) {
        this.mUserId = userId;
        return this;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public Account withUsername(String username) {
        this.mUsername = username;
        return this;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        this.mCreateDate = createDate;
    }

    public Account withCreateDate(String createDate) {
        this.mCreateDate = createDate;
        return this;
    }

    public List<Object> getBerries() {
        return mBerries;
    }

    public void setBerries(List<Object> berries) {
        this.mBerries = berries;
    }

    public Account withBerries(List<Object> berries) {
        this.mBerries = berries;
        return this;
    }
}
