package com.ttsproj.projectericlite;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import java.util.List;

/**
 * Created by Bill on 9/3/14.
 */
public class doubleTapListener implements View.OnClickListener {

    private String word, word2;
    private int count;


    public doubleTapListener(String word) {
        this.word = word;
        this.word2 = null;
        this.count = CONSTANT.START;
    }

    public doubleTapListener(List<String> words) {
        this.word = words.get(LANG.ENGLISH.ordinal());
        this.word2 = null;

        LANG lan = MyProperties.getInstance().Language;
        if (lan != LANG.ENGLISH) {
            this.word2 = words.get(lan.ordinal());
        }
        this.count = CONSTANT.START;
    }

    @Override
    public void onClick(View v) {
        count++;
        Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                count = CONSTANT.START;
            }
        };

        if (count == CONSTANT.MIDDLE) {
            handler.postDelayed(run, 250);
        } else if (count == CONSTANT.END) {
            count = CONSTANT.START;
            MyProperties.getInstance().speakout(word);

            if (word2 != null) {
                MyProperties.getInstance().speakSilent(500);
                MyProperties.getInstance().speakout(word2);
            }
        }

    }
}
