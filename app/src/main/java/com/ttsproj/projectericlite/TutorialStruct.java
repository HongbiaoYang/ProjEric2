package com.ttsproj.projectericlite;

import java.security.PublicKey;

/**
 * Created by Bill on 12/9/2015.
 */
public class TutorialStruct {


    public String picture;
    public String voice;
    public String text;
    public String gesture;
    public TutorialStruct next;

    public TutorialStruct(String text, String picture, String gesture,  String voice) {
        this.text = text;
        this.picture = picture;
        this.gesture = gesture;
        this.voice = voice;
    }
}


