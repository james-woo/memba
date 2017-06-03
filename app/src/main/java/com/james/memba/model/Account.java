package com.james.memba.model;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account implements Serializable {

    @SerializedName("_id") @Expose private String mId;
    @SerializedName("userId") @Expose private String mUserId;
    @SerializedName("createDate") @Expose private int mCeateDate;
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

    public int getCreateDate() {
        return mCeateDate;
    }

    public void setCreateDate(int createDate) {
        this.mCeateDate = createDate;
    }

    public Account withCreateDate(int createDate) {
        this.mCeateDate = createDate;
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
