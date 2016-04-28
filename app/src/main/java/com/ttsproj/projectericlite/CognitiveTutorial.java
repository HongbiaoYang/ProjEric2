package com.ttsproj.projectericlite;

import android.graphics.drawable.AnimationDrawable;

import java.util.HashMap;

/**
 * Created by Bill on 10/23/14.
 */
public class CognitiveTutorial extends SuperTutorial {

    private AnimationDrawable previousAnim;

    public CognitiveTutorial(boolean active) {
        super(active);
    }

    @Override
    public void startTutorial() {

        curTask = 0;
        lastTask = 8;


        gestureBoard = new HashMap<Integer, String>();
        checkBoard = new HashMap<Integer, Integer>();


        gestureBoard.put(LOCAL_DIRECTION.LEFT.ordinal(), "please hold the left button to access previous item");
        gestureBoard.put(LOCAL_DIRECTION.RIGHT.ordinal(),  "please hold the right button to access next item");
        gestureBoard.put(LOCAL_DIRECTION.ENTER.ordinal(),  "please hold the item to enter next level");
        gestureBoard.put(LOCAL_DIRECTION.SPEAK.ordinal(),  "please hold the item to speak it out");
        gestureBoard.put(LOCAL_DIRECTION.BACK.ordinal(), "please hold the back button to go back to previous level");
        gestureBoard.put(LOCAL_DIRECTION.HOME.ordinal(),  "please hold the home button to go back to home page");
        gestureBoard.put(LOCAL_DIRECTION.YES.ordinal(), "please hold the yes button to say yes");
        gestureBoard.put(LOCAL_DIRECTION.NO.ordinal(),  "please hold the no button to say no");
        gestureBoard.put(LOCAL_DIRECTION.MORE.ordinal(),  "please hold the more to enter response page");


        // initialize the hashmap to all 0s
        for (int i = 0; i <= lastTask;i++) {
            checkBoard.put(i,0);
        }

        MyProperties.getInstance().speakAdd("Tutorial start. Please follow the instruction and perform the action step by step.");
        MyProperties.getInstance().speakAdd(gestureBoard.get(curTask));

    }

    @Override
    protected void performUserFunction(int now) {
      /*  if (previousAnim != null) {
            previousAnim.stop();
        }
        curAnim.start();
        previousAnim = curAnim;*/

    }

    @Override
    public int Dire2Int(DIRECTION dir) {
        return 0;
    }

    public enum LOCAL_DIRECTION {
        RIGHT,
        LEFT,
        ENTER,
        SPEAK,
        BACK,
        YES,
        NO,
        MORE,
        HOME,
        EMPTY,
    }
}
