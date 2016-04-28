package com.ttsproj.projectericlite;

/**
 * Created by Bill on 10/30/14.
 */
public class TutorialItem {

    String desc;
    Integer image;
    Integer progress;
    public String voice;
    public String custom;

    public TutorialItem(String desc, Integer pgs, Integer img) {
        this.desc = desc;
        this.image = img;
        this.progress = pgs;
        this.voice = desc;
    }

    public TutorialItem() {

    }
}
