package com.ttsproj.projectericlite;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by Bill on 10/2/14.
 * This class is obsolete, DO NOT USE. Inherit a subclass from SuperTutorial instead
 */
public class Tutorial {
    /*private int swipeLeft;
    private int swipteRight;
    private int swipeUp;
    private int swipeDown;
    private int holdTwo;
    private int doubleClick;
    private int tripleClick;
    private int fourClick;*/

    private HashMap<DIRECTION, Integer> checkBoard;
    private HashMap<DIRECTION, String> gestureBoard;
    private DIRECTION curTask;
    private boolean complete;

    public void startTutorial() {

        checkBoard = new HashMap<DIRECTION, Integer>();
        gestureBoard = new HashMap<DIRECTION, String>();

        initializeBoard();

        curTask = DIRECTION.DOWN;
        complete = false;
    }

    // put all gesture into hashmap except the EMPTY
    private void initializeBoard() {
        for (DIRECTION dir : DIRECTION.values()) {
            if (dir != DIRECTION.EMPTY)  {
                checkBoard.put(dir, 0);
                String gestureTip = "";

                switch (dir) {
                    case DOWN:
                        gestureTip = "please swipe down to enter next level";
                        break;
                    case UP:
                        gestureTip = "please swipe up to go back to previous level";
                        break;
                    case LEFT:
                        gestureTip = "please swipe left to access next item";
                        break;
                    case RIGHT:
                        gestureTip = "please swipe right to access previous item";
                        break;
                    case DOUBLE_CLICK:
                        gestureTip = "please double tap to say yes";
                        break;
                    case TRIPLE_CLICK:
                        gestureTip = "please triple tap to say no";
                        break;
                    case HOLD_FINGER2:
                        gestureTip = "please put two fingers and hold for a while to go back home";
                        break;
                    case HOLD_FINGER3:
                        gestureTip = "please put three fingers and hold for a while go to to response page";
                        break;
                }
                gestureBoard.put(dir, gestureTip);

            }
        }
        MyProperties.getInstance().speakAdd("Tutorial start. Please follow the instruction and perform the action step by step.");
        MyProperties.getInstance().speakAdd(gestureBoard.get(DIRECTION.DOWN));
    }

    public boolean checkNext(DIRECTION now) {

        // do nothing if already completed
        if (complete == true) {
            return true;
        }

        if (curTask != now) {
            speakPerform(curTask);
            return false;
        } else if (checkBoard.get(now) == 0) {
            checkBoard.put(now, 1);
            if (now == DIRECTION.HOLD_FINGER3) {
                MyProperties.getInstance().speakAdd("Congratulations! You have finished all training!");
                complete = true;
                return complete;
            } else {
                curTask = DIRECTION.values()[now.ordinal()+1];
                speakSuccess(curTask);
            }

        } else {
            Log.i("Turtorial", "Error: now=" + now + " curTask=" + curTask);
        }

        return false;
    }

    private void speakPerform(DIRECTION now) {
        MyProperties.getInstance().speakAdd(gestureBoard.get(now));
    }

    private void speakSuccess(DIRECTION now) {
        String success = "Good job, you made it. Now " + gestureBoard.get(now);
        MyProperties.getInstance().speakAdd(success);
    }


}
