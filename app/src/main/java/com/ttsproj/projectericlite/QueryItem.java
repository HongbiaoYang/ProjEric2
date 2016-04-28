package com.ttsproj.projectericlite;

import android.graphics.drawable.Drawable;

/**
 * Created by Bill on 8/28/14.
 */
public class QueryItem {
    private int qID;
    private String qTitle;
    private String qText;

    private Drawable qImg;


    public QueryItem(int id, String title, String text, Drawable img) {
        this.qID = id;
        this.qTitle = title;
        this.qText = text;
        this.qImg = img;
    }

    public String getqText() {
        return this.qText;
    }

    public Drawable getqImg() {
        return this.qImg;
    }

    public String getqTitle() {
        return this.qTitle;
    }


}
