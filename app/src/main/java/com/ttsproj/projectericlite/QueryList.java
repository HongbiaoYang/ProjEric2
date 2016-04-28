package com.ttsproj.projectericlite;

import android.app.DownloadManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bill on 8/28/14.
 */
public class QueryList {
    private ArrayList<QueryItem> qList;
    private int curPos;

    public QueryList() {
        this.qList = new ArrayList<QueryItem>();
        this.curPos = 0;
    }
    public void AddItem(QueryItem qi) {
        qList.add(qi);
    }

    public QueryItem getQuery(int index) {
        return qList.get(index);
    }

    public int getSize() {
        return qList.size();
    }

    public QueryItem getNext() {
        if (curPos >= qList.size() - 1) {
            return qList.get(curPos);
        } else {
            return qList.get(++curPos);
        }
    }

    public QueryItem getPrevious() {
        if (curPos <= 0) {
            return qList.get(curPos);
        } else {
            return qList.get(--curPos);
        }
    }

    public QueryItem getCurrent() {
        return qList.get(curPos);
    }

    public int currentPos() {
        return curPos;
    }


}
