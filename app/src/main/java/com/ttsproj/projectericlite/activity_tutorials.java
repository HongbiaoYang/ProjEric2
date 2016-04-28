package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Bill on 11/6/2015.
 */
public class activity_tutorials extends Activity {
    private Button vision, hearing, nonenglish;
    private ImageView goBack;
    private int count  = CONSTANT.START;
    private LinearLayout emergency;
    private String TAG = "activity_tutorials";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tutorials);

        View head = findViewById(R.id.home_header);
        TextView title = (TextView)head.findViewById(R.id.head_home2);
        title.setText(MyProperties.getInstance().getTitleName(TITLE.TUTORIAL));

        goBack = (ImageView)head.findViewById(R.id.head_home1);
        goBack.setBackgroundResource(R.drawable.gobacklong);

        vision = (Button)findViewById(R.id.vision);
        hearing = (Button)findViewById(R.id.hearing);
        nonenglish = (Button)findViewById(R.id.nonenglish);

        emergency = (LinearLayout)findViewById(R.id.head_home3);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vision.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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

                    Intent intent = new Intent();
                    intent.setClass(activity_tutorials.this, activity_visionMain.class);
                    startActivity(intent);
                }
            }
        });

        hearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    Intent intent = new Intent();
                    intent.setClass(activity_tutorials.this, activity_hearingMain.class);
                    startActivity(intent);
                }
            }
        });

        nonenglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    Intent intent = new Intent();
                    intent.setClass(activity_tutorials.this, activity_nonEnglishMain.class);
                    startActivity(intent);
                }
            }
        });

        Log.d(TAG, "oncreate of activity tutorials main");

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
                    intent.setClass(activity_tutorials.this, activity_emergency.class);
                    startActivity(intent);
                }
            }
        });



    }
}