package com.ttsproj.projectericlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Bill on 8/28/14.
 */
public class activity_main extends Activity implements OnInitListener {
    private TextToSpeech tts;
    private Button vision, hearing, cognitive, nonenglish;
    private LinearLayout emergency;
    private int count = CONSTANT.START;
    private int backTimer = 0;
    private String TAG = "activity_main";
    private ImageView goBack;
    private TextView title;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        // first animation in main
        ImageView image = (ImageView) findViewById(R.id.frame_home);
        image.setBackgroundResource(R.drawable.frame);
//        MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();

        View head = findViewById(R.id.home_header);

        title = (TextView)head.findViewById(R.id.head_home2);
        String titleText = MyProperties.getInstance().transitType == CONSTANT.PARA ?
                MyProperties.getInstance().getTitleName(TITLE.PARA) : MyProperties.getInstance().getTitleName(TITLE.FIXED);
        title.setText(titleText);

        goBack = (ImageView)head.findViewById(R.id.head_home1);
        goBack.setBackgroundResource(R.drawable.gobacklong);

        vision = (Button)findViewById(R.id.vision);
        hearing = (Button)findViewById(R.id.hearing);
        cognitive = (Button)findViewById(R.id.cognitive);
        nonenglish = (Button)findViewById(R.id.nonenglish);

        emergency = (LinearLayout)findViewById(R.id.head_home3);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cognitive.setOnClickListener(new View.OnClickListener() {
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

                    /*MyProperties.getInstance().speakBoth(TITLE.COGNITIVE);

                    Intent intent = new Intent();
                    intent.setClass(activity_main.this, activity_cognitiveMain.class);
                    startActivity(intent);*/

                    // MyProperties.getInstance().speakBoth(TITLE.COGNITIVE);
                    MyProperties.getInstance().speakout(getResources().getString(R.string.cogHint));

                    String cognitive_str = MyProperties.getInstance().getTitleName(TITLE.COGNITIVE);
                    MyProperties.getInstance().titleStack.push(cognitive_str);
                    Intent intent = new Intent();
                    intent.setClass(activity_main.this, activity_cognitive.class);
                    startActivity(intent);
                }
            }
        });

        nonenglish.setOnClickListener(new View.OnClickListener() {
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

                    MyProperties.getInstance().speakBoth(TITLE.NON_ENGLISH);
                    MyProperties.getInstance().Language = LANG.SPANISH;
                    String non_english_str = MyProperties.getInstance().getTitleEither(TITLE.NON_ENGLISH);
                    MyProperties.getInstance().titleStack.push(non_english_str);

                    Intent intent = new Intent();
                    intent.setClass(activity_main.this, activity_hearing.class);
                    startActivity(intent);
                }
            }
        });
        // click vision button
        vision.setOnClickListener(new View.OnClickListener() {
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

                    // MyProperties.getInstance().speakBoth(TITLE.VISION);

                    String vision_str = MyProperties.getInstance().getTitleName(TITLE.VISION);
                    MyProperties.getInstance().titleStack.push(vision_str);

                    Intent intent = new Intent();
                    intent.setClass(activity_main.this, activity_vision.class);
                    startActivity(intent);
                }
            }
        });

        // click layout_hearing button
        hearing.setOnClickListener(new View.OnClickListener() {
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

                    String hearing_str = MyProperties.getInstance().getTitleName(TITLE.HEARING);
                    MyProperties.getInstance().titleStack.push(hearing_str);

                    MyProperties.getInstance().speakBoth(TITLE.HEARING);

                    /*String hearing_str = MyProperties.getInstance().getTitleName(TITLE.HEARING);
                    MyProperties.getInstance().titleStack.push(hearing_str);*/
                    Intent intent = new Intent();
                    intent.setClass(activity_main.this, activity_hearing.class);
                    startActivity(intent);
                }
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
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

                    MyProperties.getInstance().speakBoth(TITLE.EMERGENCY);

                    String emergency_str = MyProperties.getInstance().getTitleName(TITLE.EMERGENCY);
                    MyProperties.getInstance().titleStack.push(emergency_str);

                    Intent intent = new Intent();
                    intent.putExtra("Type", "emergency");
                    intent.setClass(activity_main.this, activity_emergency.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onInit(int status) {

    }


    @Override
    protected void onResume() {

            MyProperties.getInstance().speakout("Project Eric");

        super.onResume();
    }

}