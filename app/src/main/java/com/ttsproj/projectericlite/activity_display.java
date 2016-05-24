package com.ttsproj.projectericlite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;
import java.util.Stack;

/**
 * Created by Bill on 8/28/14.
 */
public class activity_display extends Activity implements OnInitListener {
    private TextToSpeech tts;
    private int count = CONSTANT.START;
    private ImageView lastLevelBtn;
    private TextView title;


    ListView list;
    String[] web;
    Integer[] imageId;
    LANG lan;


    private List<ItemStruct> thisLevel;
    private Stack<List<ItemStruct>> levelStack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display);
        MyProperties.getInstance().gtts = new TextToSpeech(getApplicationContext(), this);
        tts = MyProperties.getInstance().gtts;
        levelStack = new Stack<List<ItemStruct>>();

        // the setting language
        lan = MyProperties.getInstance().Language;

        lastLevelBtn = (ImageView) findViewById(R.id.header1);
        title = (TextView) findViewById(R.id.header2);

        title.setText(MyProperties.getInstance().getTitleStack());
        MyProperties.getInstance().playAnimation();

        lastLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               goUpOrBack();
            }
        });

        String type = getIntent().getStringExtra("Type");


        boolean updated = MyProperties.getInstance().Language == LANG.ENGLISH ?
                MyProperties.getInstance().hearing_updated : MyProperties.getInstance().nonenglish_updated;
        thisLevel = MyProperties.getInstance().currentType.getInformation(type, true);

        // order standard
        String  order = MyProperties.getInstance().Language == LANG.ENGLISH ? "hearing" : "nonenglish";
        thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType, " order by " + order +" desc", "menu", type);

        updateList(thisLevel);
    }

    private void goUpOrBack() {
        if (levelStack.isEmpty()) {
            MyProperties.getInstance().popStacks();
            finish();
        } else {
            thisLevel = levelStack.pop();
            updateList(thisLevel);
        }
    }

    private void updateList(List<ItemStruct> level) {
        ListFactory lf = new ListFactory(level);
        CustomList adapter;
        web =  lf.produceTitleArray();
        imageId = lf.produceImageArray(this.getApplicationContext());

        adapter = new
                CustomList(activity_display.this, web, imageId);

        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemStruct item = thisLevel.get(position);
                if (item.getChild() == null) {
                    MyProperties.getInstance().speakBoth(item);

                    if (item.getSpecialTag() != null && item.getSpecialTag().equalsIgnoreCase("demo")) {
                        return false;
                    }

                    String subMenu;
                    if (MyProperties.getInstance().Language == LANG.ENGLISH) {
                        subMenu = "hearing";
                        MyProperties.getInstance().hearing_updated = true;
                    } else {
                        subMenu = "nonenglish";
                        MyProperties.getInstance().nonenglish_updated = true;
                    }

                    item.setFreq(subMenu, item.getFreq(subMenu) + 1);
                    MyProperties.getInstance().database.updateItem(MyProperties.getInstance().transitType, subMenu, item);

                    Log.d("activity_display", "submenu="+subMenu +" count="+item.getFreq(subMenu));
                }
//                else {
//                    MyProperties.getInstance().speakBoth(item);
//                    levelStack.push(thisLevel);
//                    thisLevel = item.getChild();
//                    updateList(thisLevel);
//                }
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                count++;
                Handler handler = new Handler();
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        count = CONSTANT.START;
                    }
                };

                if (count == CONSTANT.MIDDLE) {
                    handler.postDelayed(run, 250);
                } else if (count == CONSTANT.END) {
                    count = CONSTANT.START;

                    ItemStruct item = thisLevel.get(position);
                    if (item.getChild() == null) {
                        MyProperties.getInstance().speakBoth(item);
                        if (item.getSpecialTag() != null && item.getSpecialTag().equalsIgnoreCase("demo")) {
                            return;
                        }

                        String subMenu;
                        if (MyProperties.getInstance().Language == LANG.ENGLISH) {
                            subMenu = "hearing";
                            MyProperties.getInstance().hearing_updated = true;
                        } else {
                            subMenu = "nonenglish";
                            MyProperties.getInstance().nonenglish_updated = true;
                        }

                        item.setFreq(subMenu, item.getFreq(subMenu) + 1);
                        MyProperties.getInstance().database.updateItem( MyProperties.getInstance().transitType, subMenu, item);

                        Log.d("activity_display", "submenu="+subMenu +" count="+item.getFreq(subMenu));
                    }
//                    else {
//                        MyProperties.getInstance().speakBoth(item);
//                        levelStack.push(thisLevel);
//                        thisLevel = item.getChild();
//                        updateList(thisLevel);
//                    }
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        goUpOrBack();
        finish();
    }


    @Override
    public void onInit(int status) {
    }

}