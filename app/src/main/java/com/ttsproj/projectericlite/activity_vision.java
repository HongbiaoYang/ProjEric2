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
import android.widget.*;

import java.util.*;

public class activity_vision extends Activity {
    private ImageView  ok;
    private TextView  vText;
    private int mIsDown;
    private float mPrevX, mPrevY;
    private DIRECTION direction;
    private LinearLayout screen;
    private float oldDist;
    private int onHold;
    private int mTouchCount = 0;
    private List<ItemStruct> categories, itemsEmergency, itemsGetting, itemsRiding, itemsSafety, itemsResponse;
    private int index1, index2, level;
    private Runnable run;
    private Handler handler;
    private final String TAG = "activity_vision";


    @Override
    public void onBackPressed() {
        if (level == 0) {
            finish();
        } else {
            detectUP();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vision);

        screen = (LinearLayout)findViewById(R.id.visionScreen);
        ok    = (ImageView)findViewById(R.id.Ok);
        vText = (TextView)findViewById(R.id.visionText);

        MyProperties.getInstance().updateTransitType();

        categories = new ArrayList<ItemStruct>();
        ItemStruct emergency = new ItemStruct("emergency_v", "Emergency");
        ItemStruct gettingonoff = new ItemStruct("gettingonandoffthebus_v","getting on and off the bus");
        ItemStruct ridingthebus = new ItemStruct("ridingthebus_v", "riding the bus");
        ItemStruct safety = new ItemStruct("safety_v", "safety");

        // add first level items
        categories.add(emergency);
        categories.add(gettingonoff);
        categories.add(ridingthebus);
        categories.add(safety);

        itemsEmergency = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "", "menu", "emergency");
        itemsGetting = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "", "menu", "gettingonoff");
        itemsRiding = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "", "menu", "ridingthebus");
        itemsSafety = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "", "menu", "safety");
        itemsResponse = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "", "menu", "response", "customize","normal");

        // first animation in vision
        ImageView image = (ImageView) findViewById(R.id.frame_home);
        image.setBackgroundResource(R.drawable.frame);
//        MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();

        // first display
        index1 = 0;
        index2 = 0;
        level = 0;

        displayCurrent(level, index1, index2);

        screen.setOnTouchListener(new visionTouchListener());
        direction = DIRECTION.EMPTY;

    }

    @Override
    protected void onStop() {
        super.onStop();
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


    private class visionTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Handler handler = new Handler();
            Runnable runHold = new Runnable() {
                @Override
                public void run() {
                    Log.i("activity_vision", "Run.onhold="+onHold);
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

                    } else if (mTouchCount == 4) {

                        detectFourClick();

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

    private void detectLongPress3() {
        MyProperties.getInstance().speakout("more");
        displayResponsePage();
    }

    // perform action based on directions
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
                Log.i("activity_vision","in:detectSwipe:Up case");
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

    private void displayCurrent(int level, int index1, int index2) {

        ItemStruct item;
        List<ItemStruct> currentArray;

        if (level == 0) {
            currentArray = categories;
            item = currentArray.get(index1);
        } else {
            currentArray = getArrayOfIndex1(index1);
            item = currentArray.get(index2);
        }


        int imageV = getResources().getIdentifier(item.getvImageString(), "drawable", getPackageName());
        ok.setBackgroundResource(imageV);

        vText.setText(item.getTitle());
        MyProperties.getInstance().speakout(item.getText());

    }

    private List<ItemStruct> getArrayOfIndex1(int index) {
        if (index == 0) {
            return itemsEmergency;
        } else if (index == 1) {
            return itemsGetting;
        } else  if (index == 2) {
            return itemsRiding;
        } else if (index == 3) {
            return itemsSafety;
        } else if (index == 4) {
            return itemsResponse;
        }

        return null;
    }

    // swipe down, enter next level
    private void detectDown() {

        if (level == 0) {

            level += 1;
            index2 = 0;
            displayCurrent(level, index1, index2);
        } else {
            MyProperties.getInstance().speakout("There are no more questions beyond this point, " +
                    "please swipe up to access questions");
        }

    }

    // swipe up, go to higher level
    private void detectUP() {
        if (level == 1) {

            level -= 1;
            index1 = 0;
            index2 = 0;
            displayCurrent(level, index1, index2);
        } else {
            MyProperties.getInstance().speakout("There are no more categories beyond this point, " +
                    "please swipe down to access categories");
        }
    }

    // swipe right, next item
    private void detectRight() {

      if (level == 0) {
          if (index1 > 0) {
              index1 -= 1;
          } else {
              MyProperties.getInstance().speakout("There are no more categories beyond this point, " +
                      "please swipe left to access the next categories");
              return;
          }
      } else if (level == 1) {
          if (index2 > 0) {
              index2 -= 1;
          } else {
              MyProperties.getInstance().speakout("There are no more questions beyond this point, " +
                      "please swipe left to access the next question");
              return;
          }
      }

        displayCurrent(level, index1, index2);
    }

    // swipe left, last item
    private void detectLeft() {

        if (level == 0) {
            if (index1 < 3) {
                index1 += 1;
            } else {
                MyProperties.getInstance().speakout("There are no more categories beyond this point, " +
                        "please swipe right to access the previous categories");
                return;
            }
        } else if (level == 1) {
            if (index2 < getArraySizeofIndex1(index1) - 1) {
                index2 += 1;
            } else {
                MyProperties.getInstance().speakout("There are no more questions beyond this point, " +
                        "please swipe right to access the previous question");
                return;
            }
        }

        displayCurrent(level, index1, index2);

    }

    private int getArraySizeofIndex1(int index) {
        List array = getArrayOfIndex1(index);

        return array.size();
    }

    // long press the screen
    private void detectLongPress2() {

        finish();
    }

    // double click, say yes
    private void detectDoubleClick() {
        MyProperties.getInstance().speakout("Yes");
    }

    // triple click, say no
    private void detectTripleClick() {
        MyProperties.getInstance().speakout("No");
    }

    // four click, go to  more
    private void detectFourClick() {
        MyProperties.getInstance().speakout("four taps");

    }

    private void displayResponsePage() {

        level = 1;
        index1 = 4;
        index2 = 0;

        displayCurrent(level, index1, index2);
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
