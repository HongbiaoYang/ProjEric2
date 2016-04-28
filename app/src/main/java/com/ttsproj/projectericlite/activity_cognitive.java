package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
public class activity_cognitive extends Activity implements OnInitListener {
    private TextToSpeech tts;
    private int count = CONSTANT.START;
    private ImageView lastLevelBtn;
    private LinearLayout emergency;
    private TextView title;
    private LinearLayout yes;
    private LinearLayout no;
    private LinearLayout response;



    ListView list;
    String[] web;
    Integer[] imageId;
    LANG lan;


    private List<ItemStruct> thisLevel;
    private Stack<List<ItemStruct>> levelStack;
    private LinearLayout head_banner;
    private boolean EmergencyState;
    private Integer[] colors;
    private String TAG = "activity_cognitive";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_displaycog);
        MyProperties.getInstance().gtts = new TextToSpeech(getApplicationContext(), this);
        tts = MyProperties.getInstance().gtts;
        levelStack = new Stack<List<ItemStruct>>();

        lastLevelBtn = (ImageView) findViewById(R.id.head_home1);
        title = (TextView) findViewById(R.id.head_home2);
        emergency = (LinearLayout)findViewById(R.id.head_home3);
        head_banner = (LinearLayout)findViewById(R.id.head_banner);
        yes = (LinearLayout)findViewById(R.id.footer1);
        no  = (LinearLayout)findViewById(R.id.footer2);
        response = (LinearLayout)findViewById(R.id.footer3);

        // first animation in vision
        ImageView image = (ImageView) findViewById(R.id.frame_home);
        image.setBackgroundResource(R.drawable.frame);
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();
        //MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());


        title.setText(MyProperties.getInstance().getTitleStack());
        title.setTextAppearance(this, R.style.ButtonText_Black_16);
        MyProperties.getInstance().playAnimation();

        lastLevelBtn.setBackgroundResource(R.drawable.gobacklong);
        lastLevelBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                goUpOrBack();
                return false;
            }
        });

        yes.setOnClickListener(new onHintListener("to say yes"));
        no.setOnClickListener(new onHintListener("to say no"));
        response.setOnClickListener(new onHintListener("to enter response page"));
        emergency.setOnClickListener(new onHintListener("to enter emergency page"));
        lastLevelBtn.setOnClickListener(new onHintListener("to go back"));


        yes.setOnLongClickListener(new onHoldSpeakListener("yes"));
        no.setOnLongClickListener(new onHoldSpeakListener("no"));

      /*  emergency.setBackgroundResource(0);
        emergency.setImageResource(R.drawable.emergency_icon);*/
        emergency.setOnLongClickListener(new OnEmergencyListener());
        response.setOnLongClickListener(new OnResponseListener());

        String type = getIntent().getStringExtra("Type");

//        thisLevel = MyProperties.getInstance().getCognitiveList(true, MyProperties.getInstance().transitType);  // set to always true

        thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType, "order by" +
                "   CASE menu" +
                        "   When 'emergency' THEN 0" +
                        "   When 'gettingonoff' THEN 1 " +
                        "   When 'ridingthebus' THEN 2 " +
                        "   When 'safety' THEN 3 " +
                        "   END" +
                        "   asc, cognitive desc",
                "menu !", "response");

        updateList(thisLevel);
    }

    private void goUpOrBack() {

        if (EmergencyState == true) {
            EmergencyState = false;
            emergency.setVisibility(View.VISIBLE);
            response.setVisibility(View.VISIBLE);
            thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                    "order by cognitive desc",
                    "menu !", "response");

            updateList(thisLevel);
            title.setText(MyProperties.getInstance().getTitleStack());
            return;
        }

        if (levelStack.isEmpty()) {
            goBackToMain();
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
        colors = lf.produceColorArray();

        adapter = new
                    CustomList(activity_cognitive.this, web, imageId, R.layout.fatlist, colors);
        adapter.setColors(true);

        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new onHintListener("to make the selection"));
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemStruct item = thisLevel.get(position);
                if (item.getChild() == null) {
                    MyProperties.getInstance().speakBoth(item);

                    item.setFreq("cognitive", item.getFreq("cognitive") + 1);
                    MyProperties.getInstance().database.updateItem(MyProperties.getInstance().transitType, "cognitive", item);
                    Log.d(TAG, "count=" + item.getFreq("cognitive"));

                } else {
                    MyProperties.getInstance().speakBoth(item);
                    levelStack.push(thisLevel);
                    thisLevel = item.getChild();
                    updateList(thisLevel);
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        goUpOrBack();

    }

    private void goBackToMain() {


        MyProperties.getInstance().doInit(LANG.ENGLISH);
        MyProperties.getInstance().popStacks();

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(activity_cognitive.this, activity_main.class);
        startActivity(intent);
    }


    @Override
    public void onInit(int status) {
    }


    private class OnEmergencyListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {

            thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                    "order by cognitive", "menu", "emergency");
            updateList(thisLevel);

            title.setText("Emergency!!!");
            MyProperties.getInstance().speakout("Emergency");
            emergency.setVisibility(View.INVISIBLE);
            response.setVisibility(View.INVISIBLE);

            EmergencyState = true;

            return true;
        }

    }

    private class onHintListener implements View.OnClickListener, AdapterView.OnItemClickListener{
        private final String hint;

        public onHintListener(String s) {
            this.hint = s;
        }

        @Override
        public void onClick(View v) {
            MyProperties.getInstance().speakout("Please hold this button " + hint);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MyProperties.getInstance().speakout("Please hold this button " + hint);
        }
    }

    private class onHoldSpeakListener implements View.OnLongClickListener {
        private final String text;

        public onHoldSpeakListener(String content) {
            this.text = content;
        }


        @Override
        public boolean onLongClick(View v) {
            MyProperties.getInstance().speakout(text);
            return true;
        }
    }

    private class OnResponseListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {

            thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "order by cognitive", "menu", "response", "customize", "normal");

            updateList(thisLevel);

            title.setText("Response");
            MyProperties.getInstance().speakout("Response");
            emergency.setVisibility(View.INVISIBLE);
            response.setVisibility(View.INVISIBLE);

            EmergencyState = true;

            return true;
        }
    }
}