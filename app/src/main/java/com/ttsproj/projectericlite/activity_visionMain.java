package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Bill on 10/10/14.
 */
public class activity_visionMain extends Activity {
    private ImageView tutorial;
    private Button start, skip;
    private Boolean loop;

    @Override
    public void onBackPressed() {

//        MyProperties.getInstance().popStacks();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loop = true;
        speakRepeated(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loop = false;

        // shut up
        MyProperties.getInstance().shutup();
    }

    private String tutorialText;
    private SharedPreferences pref;
    private LinearLayout screen;
    private DIRECTION direction;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_visionmain);

        // animation in vision_main
        ImageView image = (ImageView) findViewById(R.id.frame_home);
        image.setBackgroundResource(R.drawable.frame);
//        MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();

        start = (Button)findViewById(R.id.startTutorial);
        skip = (Button)findViewById(R.id.skipTutorial);
        tutorial = (ImageView)findViewById(R.id.tutorial);
        screen = (LinearLayout)findViewById(R.id.visionTutorial);

        start.setText(R.string.startTutorial);
        skip.setText(R.string.skipTutorial);
        tutorial.setImageResource(R.drawable.tutorialv);

        start.setBackgroundResource(R.drawable.starttutorial);
        skip.setBackgroundResource(R.drawable.skiptutorial);

        tutorialText = getResources().getString(R.string.welcomeTutorial);
        tutorialText += getResources().getString(R.string.startTutorial);
        tutorialText += getResources().getString(R.string.skipTutorial);



        pref = this.getSharedPreferences("com.ttsproj.projectericlite", Context.MODE_PRIVATE);

        screen.setOnTouchListener(new visionTouchListener());
        direction = DIRECTION.EMPTY;

        // loop to control the repeated speaking. If app on pause, loop will become false, and the speak will not continue
        loop = true;
        // for the first time, always speak
        speakRepeated(true);
        Log.i("visionMain", "oncreat called");

    }

    private void speakRepeated(boolean delay) {

        // if is speaking, do not disturb and let the current one talk
        if (MyProperties.getInstance().gtts == null ||
                (MyProperties.getInstance().gtts.isSpeaking() == true || loop == false) && (delay == false)){

            return;
        }

        MyProperties.getInstance().speakAdd(tutorialText);


        Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                speakRepeated(false);
            }
        };

        if (loop) {
            handler.postDelayed(run, 15000);
        }
    }

    private class visionTouchListener implements View.OnTouchListener {

        private int mIsDown;
        private float mPrevY;
        private float mPrevX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsDown = 1;
                    break;
                case MotionEvent.ACTION_MOVE:
                        float dx = x - mPrevX;
                        float dy = y - mPrevY;
                        captureSwipe(dx, dy);
                    break;
                case MotionEvent.ACTION_UP:
                    if (direction != DIRECTION.EMPTY) {
                        // MyProperties.getInstance().speakout(direction);
                        detectSwipe(direction);
                        mIsDown = 0;
                        direction = DIRECTION.EMPTY;
                    }
                    break;
            }

            mPrevX = x;
            mPrevY = y;

            return true;
        }

        private void detectSwipe(DIRECTION dir) {
            Log.i("activity_vision","in:detectSwipe="+dir);

            switch (dir) {
                case LEFT:
                    detectLeft();
                    break;
                case RIGHT:
                    detectRight();
                    break;
                case UP:
                    detectUP();
                    break;
                case DOWN:
                    detectDown();
                    break;
                case EMPTY:
                    break;
                default:
                    Log.i("activity_vision","in:detectSwipe:default case"+dir);
                    break;
            }

        }

        private void detectUP() {
            // swipe up to start the tutorial
            pref.edit().putBoolean("tutorial_vision", false).apply();
            gotoVisionPage();

        }

        private void detectRight() {

        }

        private void detectDown() {
            // swipe down to skip the tutorial
            pref.edit().putBoolean("tutorial_vision", true).apply();
            finish();

        }

        private void gotoVisionPage() {
            MyProperties.getInstance().speakBoth(TITLE.VISION);
            String vision_str = MyProperties.getInstance().getTitleName(TITLE.VISION);
            MyProperties.getInstance().titleStack.push(vision_str);

            Intent intent = new Intent();
            intent.setClass(activity_visionMain.this, activity_visiontutorial.class);
            startActivity(intent);
        }


        private void captureSwipe(float dx, float dy) {

            Log.i("gesture", "dx="+dx + " dy="+dy);
            if (Math.abs(dx)+Math.abs(dy) < 1) {
                direction = DIRECTION.EMPTY;
                return;
            }

            if (Math.abs(dy) > Math.abs(dx)) {
                if (dy > 0) {
                    direction = DIRECTION.DOWN;
                }  else {
                    direction = DIRECTION.UP;
                }
            }  else {
                if (dx > 0) {
                    direction = DIRECTION.RIGHT;
                }  else {
                    direction = DIRECTION.LEFT;
                }

            }


        }

        private void detectLeft() {
            
        }


    }
}