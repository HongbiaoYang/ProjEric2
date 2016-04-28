package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Bill on 8/28/14.
 */
public class activity_hearing extends Activity implements OnInitListener {
    private TextToSpeech tts;
    private Button safety, gettingonoff, ridingbus, emergency;
    private ImageView goBack;
    private TextView title;
    private int count = CONSTANT.START;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_hearing);

        // MyProperties.getInstance().gtts = new TextToSpeech(getApplicationContext(), this);

        gettingonoff = (Button)findViewById(R.id.Hboarding);
        safety = (Button)findViewById(R.id.Hsafety);
        ridingbus = (Button)findViewById(R.id.Htraveling);
        emergency = (Button)findViewById(R.id.Hemergency);
        goBack = (ImageView)findViewById(R.id.header1);

        MyProperties.getInstance().updateTransitType();

        if (MyProperties.getInstance().Language == LANG.SPANISH) {
            gettingonoff.setBackgroundResource(MyProperties.getInstance().gettingonoff.getImageS());
            safety.setBackgroundResource(MyProperties.getInstance().safety.getImageS());
            ridingbus.setBackgroundResource(MyProperties.getInstance().ridingbus.getImageS());
            emergency.setBackgroundResource(MyProperties.getInstance().emergency.getImageS());
        } else {
            gettingonoff.setBackgroundResource(MyProperties.getInstance().gettingonoff.getImage());
            safety.setBackgroundResource(MyProperties.getInstance().safety.getImage());
            ridingbus.setBackgroundResource(MyProperties.getInstance().ridingbus.getImage());
            emergency.setBackgroundResource(MyProperties.getInstance().emergency.getImage());
        }


        title = (TextView)findViewById(R.id.header2);

        title.setText(MyProperties.getInstance().getTitleStack());
        MyProperties.getInstance().playAnimation();

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
                    String emergency_str = MyProperties.getInstance().getTitleEither(TITLE.EMERGENCY);
                    MyProperties.getInstance().titleStack.push(emergency_str);

                    Intent intent = new Intent();
                    intent.putExtra("Type", "emergency");
                    intent.setClass(activity_hearing.this, activity_emergency.class);
                    startActivity(intent);
                }
            }
        });

        gettingonoff.setOnClickListener(new View.OnClickListener() {
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


                    MyProperties.getInstance().speakBoth(TITLE.BOARDING);
                    String board_str = MyProperties.getInstance().getTitleEither(TITLE.BOARDING);
                    MyProperties.getInstance().titleStack.push(board_str);

                   /* Intent intent = new Intent();
                    MyProperties.getInstance().currentType = MyProperties.getInstance().gettingonoff;
                    intent.setClass(activity_hearing.this, activity_boarding.class);
                    startActivity(intent);*/

                    MyProperties.getInstance().currentType = MyProperties.getInstance().gettingonoff;
                    Intent intent = new Intent();
                    intent.putExtra("Type", "gettingonoff");
                    intent.setClass(activity_hearing.this, activity_display.class);
                    startActivity(intent);
                }

            }
        });

        ridingbus.setOnClickListener(new View.OnClickListener() {
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

                    MyProperties.getInstance().speakBoth(TITLE.TRAVELLING);
                    String travel_str = MyProperties.getInstance().getTitleName(TITLE.TRAVELLING);
                    MyProperties.getInstance().titleStack.push(travel_str);

                    MyProperties.getInstance().currentType = MyProperties.getInstance().ridingbus;
                    Intent intent = new Intent();
                    intent.putExtra("Type", "ridingthebus");
                    intent.setClass(activity_hearing.this, activity_display.class);
                    startActivity(intent);
                }

            }
        });

        safety.setOnClickListener(new View.OnClickListener() {
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


                    MyProperties.getInstance().speakBoth(TITLE.GETTING_OFF);

                    String getoff_str = MyProperties.getInstance().getTitleName(TITLE.GETTING_OFF);
                    MyProperties.getInstance().titleStack.push(getoff_str);


                    MyProperties.getInstance().currentType = MyProperties.getInstance().safety;
                    Intent intent = new Intent();
                    intent.putExtra("Type", "safety");
                    intent.setClass(activity_hearing.this, activity_display.class);
                    startActivity(intent);
                }

            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goBackToMain();
            }
        });



    }

    @Override
    public void onInit(int status) {
    }

    @Override
    public void onBackPressed() {
        goBackToMain();
    }

    private void goBackToMain() {
        MyProperties.getInstance().doInit(LANG.ENGLISH);
        MyProperties.getInstance().clearStacks();

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(activity_hearing.this, activity_main.class);
        startActivity(intent);
    }
}