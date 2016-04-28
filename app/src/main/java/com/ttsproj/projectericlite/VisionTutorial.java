package com.ttsproj.projectericlite;

import java.util.HashMap;

/**
 * Created by Bill on 10/22/14.
 */
public class VisionTutorial extends SuperTutorial {

    public VisionTutorial(boolean active) {
        super(active);
    }

    @Override
    public void startTutorial() {
        initializeBoard();
    }

    @Override
    protected void performUserFunction(int now) {
        // nothing need to be done here
    }

    @Override
    public int Dire2Int(DIRECTION dir) {

        switch (dir) {
            case DOWN:
                return 0;
            case UP:
                return LOCAL_DIRECTION.UP.ordinal();
            case LEFT:
                return LOCAL_DIRECTION.LEFT.ordinal();
            case RIGHT:
                return LOCAL_DIRECTION.RIGHT.ordinal();
            default:
                return -1;
        }

    }

    private void initializeBoard() {
        curTask = 0;
        lastTask = 7;


        gestureBoard = new HashMap<Integer, String>();
        checkBoard = new HashMap<Integer, Integer>();

        gestureBoard.put(LOCAL_DIRECTION.DOWN.ordinal(), "please swipe down to enter next level");
        gestureBoard.put(LOCAL_DIRECTION.LEFT.ordinal(), "please swipe left to access next item");
        gestureBoard.put(LOCAL_DIRECTION.RIGHT.ordinal(),  "please swipe right to access previous item");
        gestureBoard.put(LOCAL_DIRECTION.UP.ordinal(), "please swipe up to go back to previous level");
        gestureBoard.put(LOCAL_DIRECTION.DOUBLE_CLICK.ordinal(),  "please double tap to say yes");
        gestureBoard.put(LOCAL_DIRECTION.TRIPLE_CLICK.ordinal(),  "please triple tap to say no");
        gestureBoard.put(LOCAL_DIRECTION.HOLD_FINGER3.ordinal(), "please put three fingers and hold for a while go to to response page");
        gestureBoard.put(LOCAL_DIRECTION.HOLD_FINGER2.ordinal(),  "please put two fingers and hold for a while to go back home");

        // initialize the hashmap to all 0s
        for (int i = 0; i <= lastTask;i++) {
            checkBoard.put(i,0);
        }

        MyProperties.getInstance().speakAdd("Tutorial start. Please follow the instruction and perform the action step by step.");
        MyProperties.getInstance().speakAdd(gestureBoard.get(curTask));
    }

    public enum LOCAL_DIRECTION {
        DOWN,
        LEFT,
        RIGHT,
        UP,
        DOUBLE_CLICK,
        TRIPLE_CLICK,
        HOLD_FINGER3,
        HOLD_FINGER2,
        EMPTY,
    }



}
