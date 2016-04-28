package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Bill on 4/27/2016.
 */
public class activity_setting extends Activity {

    private static final String TAG = "activity_setting";
    private TextView SpeedText;
    private SeekBar SpeedSlide;
    private float speed;
    private LinearLayout HomeHeader;
    private ImageView GoBack;
    private LinearLayout Emergency;
    private LoginButton fbLogin;
    private CallbackManager callbackManager;


    @Override
    public void onBackPressed() {
        writeSpeedToDatabase(speed);
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);

        SpeedText = (TextView) findViewById(R.id.speedText);
        SpeedSlide = (SeekBar) findViewById(R.id.speedSlide);
        HomeHeader = (LinearLayout)findViewById(R.id.home_header);
        GoBack = (ImageView)findViewById(R.id.head_home1);
        Emergency = (LinearLayout)findViewById(R.id.head_home3);


        // facebook stuff
        fbLogin = (LoginButton)findViewById(R.id.fbLogin);
        fbLogin.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        Log.d(TAG, "fb created");


        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "fb success");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "fb cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "fb error");

            }
        });

        // override and set listen for go back button
        GoBack.setBackgroundResource(R.drawable.gobacklong);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeSpeedToDatabase(speed);
                finish();
            }
        });

        // set emergency button to hide
        Emergency.setVisibility(View.INVISIBLE);

        // default read from the database
        speed = Float.parseFloat(MyProperties.getInstance().database.getProp("speakSpeed"));
        SpeedText.setText("Talking Speed (" + speed +")");
        SpeedSlide.setProgress((int)(speed*100));

        // set the slider
        SpeedSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "progress="+progress);
                speed = (float) progress / 100.0f;

                SpeedText.setText("Talking Speed ("+speed+")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // change setting and speak out welcome phase after release
                Log.d(TAG, "slide end="+speed);
                MyProperties.getInstance().gtts.setSpeechRate(speed*2.0f);
                MyProperties.getInstance().speakout(getResources().getString(R.string.welcomeSpeed));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void writeSpeedToDatabase(float speed) {
        // update the value in database after exit the page
        MyProperties.getInstance().database.updateProp("speakSpeed", Float.toString(speed));
    }
}