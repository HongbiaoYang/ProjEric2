package com.ttsproj.projectericlite;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Bill on 10/22/14.
 */
public abstract class SuperTutorial {
    private final boolean active;
    HashMap<Integer, Integer> checkBoard;
    HashMap<Integer, String> gestureBoard;
    int curTask, lastTask;
    private boolean complete;
    protected AnimationDrawable curAnim;

    public abstract  void startTutorial();

    public SuperTutorial(boolean active) {
        this.active = active;
    }

    public boolean checkNext(int now, AnimationDrawable anim) {
        this.curAnim = anim;
        return checkNext(now);
    }

    public boolean checkNext(int now) {
        if (active == false) {
            return false;
        }

        // do nothing if already completed
        if (complete == true) {
            return true;
        }

        if (curTask != now) {
            speakPerform(curTask);
            return false;
        } else if (checkBoard.get(now) == 0) {
            checkBoard.put(now, 1);
            if (now == lastTask) {
                MyProperties.getInstance().speakAdd("Congratulations! You have finished all training!");
                complete = true;
                return complete;
            } else {
                curTask++;
                speakSuccess(curTask);
            }

        } else {
            Log.i("Turtorial", "Error: now=" + now + " curTask=" + curTask);
        }

        return false;
    }

    private void speakPerform(int now) {
        MyProperties.getInstance().speakAdd(gestureBoard.get(now));
    }

    public void speakAgainNow() {
        speakPerform(curTask);
    }

    protected abstract void performUserFunction(int now);

    private void speakSuccess(Integer now) {
        String success = "Good job, you made it. Now " + gestureBoard.get(now);
        MyProperties.getInstance().speakAdd(success);
        performUserFunction(now);
    }


    public abstract int Dire2Int(DIRECTION dir);
}
