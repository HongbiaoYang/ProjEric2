package com.ttsproj.projectericlite;

import android.graphics.Color;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Bill on 9/8/14.
 */
public class ItemStruct {
    private HashMap<LANG, String> title;
    private HashMap<LANG, String> text;
    private String SpecialTag;

    private Integer imageID;
    private List<ItemStruct> child;
    private int VImageID;
    private int color;


    private String imageString;
    private String vImageString;
    private String colorString;

    public ItemStruct(String vImageString, String content) {
        setvImageString(vImageString);
        this.title = new HashMap<LANG, String>();
        this.setTitle(LANG.ENGLISH, content);
        this.text = new HashMap<LANG, String>();
        this.setText(LANG.ENGLISH, content);
    }

    public int getFreq(String subMenu) {

        if (subMenu.equalsIgnoreCase("hearing"))
            return freq_hearing;
        else if (subMenu.equalsIgnoreCase("cognitive")) {
            Log.d("ItemStruct", "submenu="+subMenu + " freq="+freq_cognitive);

            return freq_cognitive;
        }
        else if (subMenu.equalsIgnoreCase("nonenglish"))
            return freq_nonenglish;
        else if (subMenu.equalsIgnoreCase("vision"))
            return freq_vision;
        else
            return 0;
    }


    public void setFreq(String subMenu, int freq) {
        if (subMenu.equalsIgnoreCase("hearing"))
            freq_hearing = freq;
        else if (subMenu.equalsIgnoreCase("cognitive"))
            freq_cognitive = freq;
        else if (subMenu.equalsIgnoreCase("nonenglish"))
            freq_nonenglish = freq;
        else if (subMenu.equalsIgnoreCase("vision"))
            freq_vision = freq;
    }

    private int freq;
    private int freq_hearing;
    private int freq_cognitive;
    private int freq_nonenglish;
    private int freq_vision;

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setvImageString(String vImageString) {
        this.vImageString = vImageString;
    }

    public String getImageString() {
        return imageString;
    }

    public String getvImageString() {
        return vImageString;
    }



    public String getSpecialTag() {
        return SpecialTag;
    }

    public void setSpecialTag(String specialTag) {
        SpecialTag = specialTag;

        // for input item, add initial freq to 1
        if (specialTag != null && specialTag.equalsIgnoreCase("input")) {
            setFreq("hearing", 1);
        }
    }

    public ItemStruct () {
        title = new HashMap<LANG, String>();
        text = new HashMap<LANG, String>();
        SpecialTag = null;
        color = Color.parseColor("#f174ac");

        child = null;

        this.setFreq("hearing", 0);
        this.setFreq("nonenglish", 0);
        this.setFreq("cognitive", 0);
        this.setFreq("vision", 0);
    }

    public ItemStruct(Integer image, String content) {
        this();
        this.imageID = image;
        this.title = new HashMap<LANG, String>();
        this.setTitle(LANG.ENGLISH, content);
        this.text = new HashMap<LANG, String>();
        this.setText(LANG.ENGLISH, content);
    }

    public String getText(LANG lan) {
        return text.get(lan);
    }

    public String getTitle(LANG lan) {
        String tmp;
        if (lan == LANG.SPANISH) {
            tmp = title.get(LANG.SPANISH);
        } else if (lan == LANG.EN_SP) {
            tmp = title.get(LANG.SPANISH) + "/"+title.get(LANG.ENGLISH);
        } else {
            tmp =  title.get(lan);
        }

        return tmp;
    }

    public void setText(LANG lan, String content) {
        text.put(lan, content);
    }

    public void setTitle(LANG lan, String content) {
        title.put(lan, content);
    }

    public void setImageID (Integer img) {
        imageID = img;
    }

    public Integer getImageID() {
        return imageID;
    }

    public void setChild(List<ItemStruct> child) {
        this.child = child;
    }

    public List<ItemStruct> getChild() {
        return child;
    }

    // default text
    public String getText() {
        return getText(LANG.ENGLISH);
    }

    public void setVImageID(int VImageID) {
        this.VImageID = VImageID;
    }

    public Integer getVImageID() {
        return VImageID;
    }

    public String getTitle() {
        return getTitle(LANG.ENGLISH);
    }

    public boolean hasChildren() {
        if (child == null) {
            return false;
        } else if (child.size() == 0) {
            return false;
        }

        return true;
    }

    public void setText(String text) {
        setText(LANG.ENGLISH, text);
    }

    public void setTitle(String title) {
        setTitle(LANG.ENGLISH, title);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorCode() {
        return color;
    }

    public void setColorCode() {

    }

    public void setColorString(String colorStr) {
        this.colorString = colorStr;
    }

    public String getColorString() {
        return colorString;
    }
}
