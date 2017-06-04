package com.james.memba.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Entry implements Serializable{
    @SerializedName("title") @Expose private String mTitle;
    @SerializedName("date") @Expose private String mDate;
    @SerializedName("image") @Expose private String mImage;
    @SerializedName("text") @Expose private String mText;
    private final static long serialVersionUID = 5481659470060384881L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Entry() {
    }

    /**
     *
     * @param text
     * @param date
     * @param title
     * @param image
     */
    public Entry(String title, String date, String image, String text) {
        super();
        this.mTitle = title;
        this.mDate = date;
        this.mImage = image;
        this.mText = text;
    }

    public Entry(String title, String image, String text) {
        super();
        this.mTitle = title;
        this.mImage = image;
        this.mText = text;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Entry withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public Entry withImage(String image) {
        this.mImage = image;
        return this;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public Entry withText(String text) {
        this.mText = text;
        return this;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public Entry withDate(String date) {
        this.mDate = date;
        return this;
    }
}
