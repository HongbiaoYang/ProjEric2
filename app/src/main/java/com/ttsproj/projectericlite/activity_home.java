package com.ttsproj.projectericlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by Bill on 11/5/2015.
 */
public class activity_home extends Activity implements TextToSpeech.OnInitListener{

    private Button para, fixed, tut;
    private int count = CONSTANT.START;
    private String TAG = "activity_home";
    private int backTimer = 0;
    private boolean accept = false;
    private LinearLayout emergency, setting;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "home resume called");
        setButtonLook(MyProperties.getInstance().isLogged());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        // facebook stuff
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        MyProperties.getInstance().gtts = new TextToSpeech(getApplicationContext(), this);

        // first time, no db exist, create one and load data from xml
        if (MyProperties.getInstance().database == null) {
            MyProperties.getInstance().database = new DatabaseHandler(this);
        }

        // first animation in main
        ImageView image = (ImageView) findViewById(R.id.frame_home);
        image.setBackgroundResource(R.drawable.frame);
//        MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();

        para = (Button)findViewById(R.id.para);
        fixed = (Button)findViewById(R.id.fixed);
        tut = (Button)findViewById(R.id.tut);

        // set button transparent according to login status
        setButtonLook(MyProperties.getInstance().isLogged());

        emergency = (LinearLayout)findViewById(R.id.head_home3);
        setting = (LinearLayout)findViewById(R.id.head_home4);
        setting.setVisibility(View.VISIBLE);

        // go to setting page
        Log.d(TAG, "get setting button 34");
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "go to setting");

                Intent intent = new Intent();
                intent.setClass(activity_home.this, activity_setting.class);
                startActivity(intent);
            }
        });

        para.setOnClickListener(new View.OnClickListener() {
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

                    if (! MyProperties.getInstance().isLogged()) {
                        showLoginDialog();

                    } else {

                        MyProperties.getInstance().transitType = CONSTANT.PARA;

                        Intent intent = new Intent();
                        intent.setClass(activity_home.this, activity_main.class);
                        startActivity(intent);
                    }
                }
            }
        });

        fixed.setOnClickListener(new View.OnClickListener() {
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

                    if (! MyProperties.getInstance().isLogged()) {
                        showLoginDialog();
                    } else {

                        MyProperties.getInstance().transitType = CONSTANT.FIXED;

                        Intent intent = new Intent();
                        intent.setClass(activity_home.this, activity_main.class);
                        startActivity(intent);
                    }
                }
            }
        });


        tut.setOnClickListener(new View.OnClickListener() {
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
                    intent.setClass(activity_home.this, activity_tutorials.class);
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
                    intent.setClass(activity_home.this, activity_emergency.class);
                    startActivity(intent);
                }
            }
        });


//        DatabaseHandler database = MyProperties.getInstance().database;
//        addTimeStamp();

        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************
        // Premium - Trial version code. Comment this section out to get premium version
        /*
        
        Long start = Long.parseLong(MyProperties.getInstance().database.getProp("startTime"));
        Long now = System.currentTimeMillis();
        int elapsed = (int) (now - start)/1000; // time in seconds
        int remain = CONSTANT.HOURS_24 - elapsed;

        if (remain > 0) {

            if (MyProperties.getInstance().firstTimeOpenApp) {
                int hour = remain / 3600;
                int minute = (remain - hour * 3600) / 60;
                int second = remain % 60;
                String timeLeft = String.format("%d hours, %d minutes, %d seconds", hour, minute, second);

                new AlertDialog.Builder(this)
                        .setTitle("Trial Version")
                        .setCancelable(false)
                        .setMessage("Your trial has " + timeLeft + " remaining")
                        .setIcon(R.drawable.time_left)
                        .setPositiveButton(android.R.string.yes, null).show();
            }

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Time over!")
                    .setMessage("Your trial has expired! Please purchase a premium version!")
                    .setIcon(R.drawable.app_expire)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://projectericutk.blogspot.com/p/welcome-to-project-eric.html"));
                            startActivity(browserIntent);
                            finish();
                        }


                    }).show();
        }
        */
        // *****************************************************************************************
        // *****************************************************************************************
        // *****************************************************************************************

        if (MyProperties.getInstance().gettingonoff_para == null) {
            MyProperties.getInstance().gettingonoff_para = new DisableType("Getting on and off the bus", R.drawable.gettingonandoffthebus,
                    R.drawable.gettingonandoffthebus_v,
                    R.drawable.gettingonandoffthebus_s);

//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.PARA, "menu", "gettingonoff");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().gettingonoff_para.setInformation("general", completeItems);
        }

        if (MyProperties.getInstance().ridingbus_para == null) {
            MyProperties.getInstance().ridingbus_para = new DisableType("Riding the bus", R.drawable.ridingthebus,
                    R.drawable.ridingthebus_v,
                    R.drawable.ridingthebus_s);
            // loadXMLResourceParser(MyProperties.getInstance().ridingbus, R.xml.ridingbus);
//            List<ItemStruct> completeItems = database.getAllItems( CONSTANT.PARA, "menu", "ridingbus");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().ridingbus_para.setInformation("general", completeItems);

        }

        if (MyProperties.getInstance().safety_para == null) {
            MyProperties.getInstance().safety_para = new DisableType("Safety", R.drawable.safety,
                    R.drawable.safety_v,
                    R.drawable.safety_s);
            // loadXMLResourceParser(MyProperties.getInstance().safety, R.xml.safety);
//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.PARA,"menu", "safety");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().safety_para.setInformation("general", completeItems);
        }

        if (MyProperties.getInstance().emergency_para == null) {
            MyProperties.getInstance().emergency_para = new DisableType("Emergency", R.drawable.emergency,
                    R.drawable.emergency_v,
                    R.drawable.emergency_s);
//            loadXMLResourceParser(MyProperties.getInstance().emergency, R.xml.emergency);
//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.PARA, "menu", "emergency");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().emergency_para.setInformation("emergency", completeItems);
        }


        if (MyProperties.getInstance().response_para == null) {
            MyProperties.getInstance().response_para = new DisableType("Response", 0, 0,0);
            // loadXMLResourceParser(MyProperties.getInstance().response, R.xml.response);

//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.PARA,"Menu", "response");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().response_para.setInformation("response", completeItems);
        }

        if (MyProperties.getInstance().gettingonoff_fixed == null) {
            MyProperties.getInstance().gettingonoff_fixed = new DisableType("Getting on and off the bus", R.drawable.gettingonandoffthebus,
                    R.drawable.gettingonandoffthebus_v,
                    R.drawable.gettingonandoffthebus_s);

//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.FIXED, "Menu", "gettingonoff");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().gettingonoff_fixed.setInformation("general", completeItems);
        }

        if (MyProperties.getInstance().ridingbus_fixed == null) {
            MyProperties.getInstance().ridingbus_fixed = new DisableType("Riding the bus", R.drawable.ridingthebus,
                    R.drawable.ridingthebus_v,
                    R.drawable.ridingthebus_s);
            // loadXMLResourceParser(MyProperties.getInstance().ridingbus, R.xml.ridingbus);
//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.FIXED, "Menu", "ridingbus");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().ridingbus_fixed.setInformation("general", completeItems);

        }

        if (MyProperties.getInstance().safety_fixed == null) {
            MyProperties.getInstance().safety_fixed = new DisableType("Safety", R.drawable.safety,
                    R.drawable.safety_v,
                    R.drawable.safety_s);
            // loadXMLResourceParser(MyProperties.getInstance().safety, R.xml.safety);
//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.FIXED, "Menu", "safety");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().safety_fixed.setInformation("general", completeItems);
        }

        if (MyProperties.getInstance().emergency_fixed == null) {
            MyProperties.getInstance().emergency_fixed = new DisableType("Emergency", R.drawable.emergency,
                    R.drawable.emergency_v,
                    R.drawable.emergency_s);
//            loadXMLResourceParser(MyProperties.getInstance().emergency, R.xml.emergency);
//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.FIXED, "Menu", "emergency");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().emergency_fixed.setInformation("emergency", completeItems);
        }


        if (MyProperties.getInstance().response_fixed == null) {
            MyProperties.getInstance().response_fixed = new DisableType("Response", 0, 0,0);
            // loadXMLResourceParser(MyProperties.getInstance().response, R.xml.response);

//            List<ItemStruct> completeItems = database.getAllItems(CONSTANT.FIXED, "Menu", "response");
//            fillMissingInformation(completeItems);
//            MyProperties.getInstance().response_fixed.setInformation("response", completeItems);
        }

    }

    private void setButtonLook(boolean logged) {
        if (logged) {
            para.setAlpha(1.0f);
            fixed.setAlpha(1.0f);

        } else {
            para.setAlpha(0.5f);
            fixed.setAlpha(0.5f);
        }
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log in?")
                .setMessage("You need to login to unlock this function!")
                .setIcon(R.drawable.lock)
//                .setCancelable(false)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setClass(activity_home.this, activity_setting.class);
                        startActivity(intent);
                    }


                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    @Override
    public void onBackPressed() {
        int curTime = Calendar.getInstance().get(Calendar.SECOND);

        Log.d(TAG,"back is pressed in main");

        if (curTime - backTimer < 3) {
            TextToSpeech mTts = MyProperties.getInstance().gtts;
            mTts.stop();
            mTts.shutdown();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Click again to quit this app", Toast.LENGTH_SHORT).show();
            backTimer = curTime;
        }

    }

/*    private void addTimeStamp() {
        Long timeSeconds = System.currentTimeMillis();

        String start = MyProperties.getInstance().database.getProp("startTime");

        if (start == null) {
            MyProperties.getInstance().database.addProp("startTime", timeSeconds.toString());
        } else if (Long.parseLong(start) == 0) {
            MyProperties.getInstance().database.updateProp("startTime", timeSeconds.toString());
        }
    }*/


    private void fillMissingInformation(List<ItemStruct> itemsFromDatabase) {
        for (ItemStruct is: itemsFromDatabase) {

            if (is.getImageString() != null) {
                is.setImageID(getResources().getIdentifier(is.getImageString(), "drawable", getPackageName()));
            }

        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            LANG lan = MyProperties.getInstance().Language;
            MyProperties.getInstance().doInit(lan);


            Log.i(TAG, "onInit TTS, changed firstOpen="+MyProperties.getInstance().firstTimeOpenApp);
            if (MyProperties.getInstance().firstTimeOpenApp) {
                MyProperties.getInstance().speakout("Welcome to project Eric, double tap to make a selection");
                Log.i(TAG, "onInit inside long, firstOpen="+MyProperties.getInstance().firstTimeOpenApp);
                MyProperties.getInstance().firstTimeOpenApp = false;

            } else {
                MyProperties.getInstance().speakout("Project Eric");
            }

            Log.i("activity_main", "onInit of TTS");
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}