package com.ttsproj.projectericlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Bill on 9/11/14.
 */
public class activity_response extends Activity {
    private static final String TAG = "activity_response";
    private String[] web;
    private TextView title;
    private ImageView goback;
    private Integer[] imageId;
    private ListView list;
    private int count = CONSTANT.START;
    private List<ItemStruct> thisLevel;
    private Stack<List<ItemStruct>> levelStack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_response);

        goback = (ImageView)findViewById(R.id.header1);
        title = (TextView)findViewById(R.id.header2);

        title.setText(MyProperties.getInstance().titleStack.lastElement());
        MyProperties.getInstance().playAnimation();

        MyProperties.getInstance().updateTransitType();

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProperties.getInstance().popStacks();
                finish();
            }
        });

//        thisLevel = MyProperties.getInstance().response.getInformation("response", true);

        String type = MyProperties.getInstance().Language == LANG.ENGLISH ? "hearing" : "nonenglish";
        thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType,
                "order by " + type + " desc", "menu", "response");

        // add customized item only if the response page is opened in English setting
        if (MyProperties.getInstance().Language == LANG.ENGLISH) {
            addInputItemInFront();

        }

        updateList(thisLevel);
    }

    private void addInputItemInFront() {
        ItemStruct item = new ItemStruct(R.drawable.customize, "Input your text...");
        item.setImageString("customize");
        item.setSpecialTag("input");
        thisLevel.add(0, item);
    }


    private void updateList(List<ItemStruct> level) {
        ListFactory lf = new ListFactory(level);
        CustomList adapter;
        int offset = 0;

        web =  lf.produceTitleArray(offset);
        imageId = lf.produceImageArray(this.getApplicationContext());
        adapter = new
                CustomList(activity_response.this, web, imageId);

        list = (ListView)findViewById(R.id.rlist);
        list.setAdapter(adapter);

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

                    // if tagged special tag
                    if (item.getSpecialTag() == null || item.getSpecialTag().equalsIgnoreCase("added") || item.getSpecialTag().equalsIgnoreCase("normal")) {
                        MyProperties.getInstance().speakBoth(item);

                        updateClickedItem(item);

                    } else if (item.getSpecialTag().equalsIgnoreCase("input")) {
                        createNewItem();
                    }

                }

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemStruct item = thisLevel.get(position);

                if (item.getSpecialTag() == null || (item.getSpecialTag().equalsIgnoreCase("normal"))) {
                    cannotDeleteWarning();
                    return false;
                } else {
                    removeCustomItem(position);
                    return false;
                }
            }

        });
    }

    private void updateClickedItem(ItemStruct item) {

        item.setFreq("hearing", item.getFreq("hearing") + 10);
        MyProperties.getInstance().database.updateItem(MyProperties.getInstance().transitType, "hearing", item);

        // update item of 'input' to be biggest value of freq
        int max = MyProperties.getInstance().database.getMaxFreq("response", MyProperties.getInstance().transitType);
        ItemStruct inputItem = thisLevel.get(0);
        inputItem.setFreq("hearing", max);
        MyProperties.getInstance().database.updateItem(MyProperties.getInstance().transitType, "hearing", inputItem);

    }

    // warn user that system item is not for delete
    private void cannotDeleteWarning() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Can not delete system item")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, null).show();
    }

    // delete user added item
    private void removeCustomItem(final int position) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Do you really want to delete this item?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String deletedItem = thisLevel.get(position).getText();
                        thisLevel.remove(position);
                        MyProperties.getInstance().response.decreaseCustomCount();
                        updateList(thisLevel);

                        saveDeletedItems(deletedItem);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }




    // add new customzied item
    private void createNewItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Your Text");

        final EditText input =  new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        builder.setView(input);


        builder.setPositiveButton("Speak", null);
        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // get the text and speak out the new item
                String addedItem = input.getText().toString();
                MyProperties.getInstance().speakout(addedItem);

                // add the new item into database
                ItemStruct item = new ItemStruct(R.drawable.customize, addedItem);
                item.setSpecialTag("added");
                item.setImageString("customize");
                item.setColorString("#fdbd35");
                int max = MyProperties.getInstance().database.getMaxFreq("response", MyProperties.getInstance().transitType);
                item.setFreq("hearing", max+1);
                MyProperties.getInstance().database.addItem(item, "response", MyProperties.getInstance().transitType);

                // update the value in memory as well as in 'thisLevel' variable
                String type = MyProperties.getInstance().Language == LANG.ENGLISH ? "hearing" : "nonenglish";
                thisLevel = MyProperties.getInstance().database.getAllItems(MyProperties.getInstance().transitType, "order by " + type + " desc", "menu", "response");

                // add the first item, aleays
                addInputItemInFront();


                updateList(thisLevel);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        Button speak = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        speak.setOnClickListener(new NonCloseListener(dialog, input));

        speak.setBackgroundResource(R.drawable.btn_yellow);
        speak.setTextAppearance(this, R.style.ButtonText_Black_20);

        Button save, cancel;
        save = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // set appearance and background
        save.setBackgroundResource(R.drawable.btn_yellow);
        save.setTextAppearance(this, R.style.ButtonText_Black_20);

        cancel.setBackgroundResource(R.drawable.btn_yellow);
        cancel.setTextAppearance(this, R.style.ButtonText_Black_20);

        LinearLayout layout = (LinearLayout) cancel.getParent();
        layout.setBackgroundResource(R.color.Black);



        // appearance of the title text
        int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) dialog.findViewById(textViewId);
        tv.setTextAppearance(this, R.style.ButtonText_yellow_16);

        // appearance of the input text
        input.setTextAppearance(this, R.style.ButtonText_Blue_20);

    }

    private void fillMissingInformation(List<ItemStruct> itemsFromDatabase) {
        for (ItemStruct is: itemsFromDatabase) {
            Log.d(TAG, "is name="+is.getText());
            if (is.getImageString() != null) {
                is.setImageID(getResources().getIdentifier(is.getImageString(), "drawable", getPackageName()));
            }

        }
    }

    private void saveAddedItems(String item) {
        SharedPreferences responsePref = this.getSharedPreferences("com.ttsproj.projectericlite", Context.MODE_PRIVATE);
        Set<String> response = responsePref.getStringSet("Added", new HashSet<String>());

        Set<String> copy = new HashSet<String>();
        cloneSet(response, copy);
        copy.add(item);
        response.clear();

        responsePref.edit().putStringSet("Added", copy).apply();


    }

    private void saveDeletedItems(String item) {


        MyProperties.getInstance().database.deleteItem(MyProperties.getInstance().transitType, item);

    }

    private void cloneSet(Set<String> set1, Set<String> set2) {
        for (String item : set1) {
            set2.add(item);
        }
    }

    // debug - reset tutorial to false
    private boolean debugResetTutorial(String type) {
        SharedPreferences pref = this.getSharedPreferences("com.ttsproj.projectericlite", Context.MODE_PRIVATE);

        // switch on or off
        boolean currentValue = pref.getBoolean(type, false);
        currentValue = !currentValue;
        pref.edit().putBoolean(type, currentValue).apply();

        // show the text. False means on, True means off
        String debugText = "[Debug] tutorial:"+ (currentValue ? "On --> Off" : " Off --> On");
        Toast.makeText(getApplicationContext(), debugText, Toast.LENGTH_SHORT).show();

        return currentValue;
    }

    private class NonCloseListener implements View.OnClickListener {
        private final Dialog dialog;
        private final EditText input;

        public NonCloseListener(Dialog dialog, EditText input) {
            this.dialog = dialog;
            this.input = input;
        }

        @Override
        public void onClick(View v) {

            // debug option
            String text = input.getText().toString();
            if (text.equalsIgnoreCase("debugVision")) {
                boolean cValue = debugResetTutorial("tutorial_vision");
                MyProperties.getInstance().speakout("Tutorial for vision page is switched " +(cValue ? "Off" :"On"));
            } else if (text.equalsIgnoreCase("debugCognitive")) {
                boolean cValue = debugResetTutorial("tutorial_cognitive");
                MyProperties.getInstance().speakout("Tutorial for cognitive is switched " + (cValue?"Off":"On"));
            } else {
                MyProperties.getInstance().speakout(text);
            }

            // clear the previous content
            input.setText("");

        }


    }

    @Override
    public void onBackPressed() {
        MyProperties.getInstance().popStacks();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("activity_response", "save customized items, if any");

    }
}