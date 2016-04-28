package com.ttsproj.projectericlite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Bill on 12/9/2015.
 */
public class activity_visiontutorial extends Activity {

    private ImageView  ok;
    private TextView  vText;
    private LinearLayout screen;
    private DIRECTION direction;
    private int onHold;
    private float mPrevX, mPrevY;
    private int mIsDown;
    private int mTouchCount = 0;
    private float oldDist;
    private TutorialStruct currentStruct;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_visiontutorial);

        screen = (LinearLayout)findViewById(R.id.visionScreen);
        ok    = (ImageView)findViewById(R.id.Ok);
        vText = (TextView)findViewById(R.id.visionText);


        TutorialStruct tutorialObject1 = new TutorialStruct("Emergency", "emergency_v", "down",
                "please swipe down to enter next level");

        TutorialStruct tutorialObject2= new TutorialStruct("I need help", "picture48_v", "left",
                "please swipe left to access next item");

        TutorialStruct tutorialObject3 = new TutorialStruct("Another passenger in the bus needs help", "picture52_v", "right",
                "please swipe right to access previous item");

        TutorialStruct tutorialObject4 = new TutorialStruct("I need help", "picture48_v", "up",
                "please swipe up to go back to previous level");

        TutorialStruct tutorialObject5 = new TutorialStruct("Emergency", "emergency_v", "double",
                "please double tap to say yes");

        TutorialStruct tutorialObject6 = new TutorialStruct("Emergency", "emergency_v", "triple",
                "please triple tap to say no");

        TutorialStruct tutorialObject7 = new TutorialStruct("Emergency", "emergency_v", "threeHold",
                "please put three fingers and hold for a while to go to response page");

        TutorialStruct tutorialObject8 = new TutorialStruct("Thank you very much", "picture109_v", "doubleHold",
                "please put two fingers and hold for a while to go back home");


        tutorialObject1.next = tutorialObject2;
        tutorialObject2.next = tutorialObject3;
        tutorialObject3.next = tutorialObject4;
        tutorialObject4.next = tutorialObject5;
        tutorialObject5.next = tutorialObject6;
        tutorialObject6.next = tutorialObject7;
        tutorialObject7.next = tutorialObject8;
        tutorialObject8.next = null;

        currentStruct = tutorialObject1;

        performStruct(currentStruct);

        screen.setOnTouchListener(new visionTouchListener());
        direction = DIRECTION.EMPTY;

    }

    private class visionTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Handler handler = new Handler();
            Runnable runHold = new Runnable() {
                @Override
                public void run() {

                    if (onHold == 2) {
                        onHold = 0;

                        detectLongPress2();

                    } else if (onHold == 3) {
                        onHold = 0;

                        detectLongPress3();
                    }

                }
            };
            Runnable runTap = new Runnable() {
                @Override
                public void run() {
                    if (mTouchCount == 2) {
                        detectDoubleClick();
                    } else if (mTouchCount == 3) {
                        detectTripleClick();
                    }

                    mTouchCount = 0;
                }
            };

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsDown = 1;
                    mTouchCount ++;

                    onHold = 0;

                    Log.i("gesture","holdtime="+onHold);

                    // detect multiple tap
                    if (mTouchCount == 1) {
                        handler.postDelayed(runTap, 1000);
                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    if (mIsDown == 1) {

                        float dx = x - mPrevX;
                        float dy = y - mPrevY;

                        if (dx > 10 || dy > 10) {
                            onHold = 0;
                        }

                        captureSwipe(dx, dy);
                    } else{
                        float newDist = spacing(event);
                        if (newDist > oldDist) {
                            Log.i("gesture","zoom in");
                            // MyProperties.getInstance().speakout("Enter");
                        }  else if (newDist < oldDist) {
                            Log.i("gesture","zoom out");
                            // MyProperties.getInstance().speakout("Leave");
                        }

                        Log.i("gesture", "dist=" + newDist);

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("gesture", "released");
                    Log.i("gesture", "direction="+direction);
                    onHold = 0;

                    if (direction != DIRECTION.EMPTY) {
                        // MyProperties.getInstance().speakout(direction);
                        detectSwipe(direction);

                        mIsDown = 0;
                        direction = DIRECTION.EMPTY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    onHold = 0;
                    mIsDown -= 1;
                    break;
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    onHold = 2;
                    handler.postDelayed(runHold, 2000);
                    Log.i("gesture","second touched detected");
                    mIsDown += 1;
                    oldDist = spacing(event);
                    break;
                case MotionEvent.ACTION_POINTER_2_UP:
                    onHold = 0;
                    mIsDown -=1;
                    break;
                case MotionEvent.ACTION_POINTER_3_DOWN:
                    onHold = 3;
                    mIsDown +=1;
                    Log.i("gesture", "third finger detected");
                    break;
                case MotionEvent.ACTION_POINTER_3_UP:
                    onHold = 0;
                    mIsDown -=1;
                    break;
            }

            mPrevX = x;
            mPrevY = y;


            return true;
        }



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

    private void detectSwipe(DIRECTION dir) {
        switch (dir) {
            case LEFT:
                detectGestureCommand("left", true);
                break;
            case RIGHT:
                detectGestureCommand("right", true);
                break;
            case UP:
                Log.i("activity_vision","in:detectSwipe:Up case");
                detectGestureCommand("up", true);
                break;
            case DOWN:
                detectGestureCommand("down", true);
                break;
            case EMPTY:
                break;
            default:
                Log.i("activity_vision","in:detectSwipe:default case"+dir);
                break;
        }
    }



    private void performStruct(TutorialStruct aStruct) {

        int aImage = getResources().getIdentifier(aStruct.picture, "drawable", getPackageName());
        ok.setBackgroundResource(aImage);
        vText.setText(aStruct.text);
        MyProperties.getInstance().speakAdd(aStruct.voice);

    }

    private void detectGestureCommand(String dir, boolean speak) {
        if (currentStruct.gesture.equalsIgnoreCase(dir)) {
            currentStruct = currentStruct.next;

            if (speak) {
                MyProperties.getInstance().speakAdd(currentStruct.text);
            }

            MyProperties.getInstance().speakAdd("good job, you made it. now ");

            performStruct(currentStruct);
        } else {
            MyProperties.getInstance().speakout(currentStruct.voice);
        }
    }

    private void detectDoubleClick() {
        MyProperties.getInstance().speakout("yes");
        detectGestureCommand("double", false);
    }

    private void detectTripleClick() {
        MyProperties.getInstance().speakout("no");
        detectGestureCommand("triple", false);
    }

    private void detectLongPress3() {
        detectGestureCommand("threeHold", true);
    }

    private void detectLongPress2() {

        if (currentStruct.gesture.equalsIgnoreCase("doubleHold")) {

            // last one, finish and get the hell out of here
            MyProperties.getInstance().speakAdd("tutorial complete!");
            finish();

        } else {
            MyProperties.getInstance().speakout(currentStruct.voice);
        }
    }




    private float spacing(MotionEvent event) {
        if (event.getPointerCount() <= 1) {
            return 0.0f;
        }

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }
}