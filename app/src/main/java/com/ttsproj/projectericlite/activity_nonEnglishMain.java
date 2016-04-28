package com.ttsproj.projectericlite;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bill on 10/24/14.
 */
public class activity_nonEnglishMain extends Activity {

    private LinearLayout nonenglishTutorial;
    private Button next, skip, prev;
    private ImageView screen, progress;
    private TextView desc;
    private int curPageIndex;
    private List<TutorialItem> itemList;
    private DIRECTION direction;
    private String TAG = "activity_nonEnglishMain";
    private ImageView backImage;
    private AnimationDrawable swipe;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nonenglishmain);

        nonenglishTutorial = (LinearLayout)findViewById(R.id.nonenglishTutorial);
        desc = (TextView)findViewById(R.id.description);
        prev = (Button)findViewById(R.id.prev);
        next = (Button)findViewById(R.id.next);
        skip = (Button)findViewById(R.id.skip);
        screen = (ImageView)findViewById(R.id.screenshot);
        progress = (ImageView)findViewById(R.id.progress);

        nonenglishTutorial.setOnTouchListener(new onSwipeListener());
        prev.setOnClickListener(new prevTutorialListener());
        next.setOnClickListener(new nextTutorialListener());
        skip.setOnClickListener(new skipTutorialListener());

        if (MyProperties.getInstance().tutorialLists == null) {
            MyProperties.getInstance().LoadTutorialXml(this); // load the xml file only when it's null
        }

        backImage = (ImageView) findViewById(R.id.swipe);
        backImage.setBackgroundResource(R.drawable.swipe_anim);
        swipe = (AnimationDrawable) backImage.getBackground();
        swipe.setExitFadeDuration(1500);
        swipe.setEnterFadeDuration(1500);

        itemList = MyProperties.getInstance().getTutorial("NonEnglish");

        curPageIndex = 0;
        setTutorialPage(itemList.get(curPageIndex));
        backImage.setVisibility(View.VISIBLE);
        swipe.start();

        Log.d(TAG, "create of nonEnglish main");
    }


    private void setTutorialPage(TutorialItem tutorialItem) {
        screen.setImageResource(tutorialItem.image);
        progress.setImageResource(tutorialItem.progress);
        desc.setText(tutorialItem.desc.replace("\\n", "\n"));
        // MyProperties.getInstance().speakout(tutorialItem.voice); ////---- mute the tutorial for now 04.06.2015
    }


    private class nextTutorialListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            detectLeft();
        }
    }

    private class skipTutorialListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            goToNewPage();
            finish();
        }
    }



    private class prevTutorialListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            detectRight();

        }
    }

    private class onSwipeListener implements View.OnTouchListener {
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
                    if (direction != null && direction != DIRECTION.EMPTY) {
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
}

    private void detectSwipe(DIRECTION dir) {
        switch (dir) {
            case LEFT:
                detectLeft();
                break;
            case RIGHT:
                detectRight();
                break;
            default:
                Log.i(TAG, "in:detectSwipe:default case" + dir);
                break;
        }
    }

    private void detectRight() {
        curPageIndex--;
        if (curPageIndex >= 0) {
            setTutorialPage(itemList.get(curPageIndex));
            next.setText("SIGUIENTE");
        } else {
            curPageIndex = 0;
        }
    }

    private void detectLeft() {
        curPageIndex++;
        if (curPageIndex < itemList.size()) {
            setTutorialPage(itemList.get(curPageIndex));
            swipe.stop();
            backImage.setVisibility(View.INVISIBLE);

            if (curPageIndex == itemList.size() - 1) {
                next.setText("HECHO");
            }
        } else {
            finish();
        }
    }

    private void captureSwipe(float dx, float dy) {

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
}