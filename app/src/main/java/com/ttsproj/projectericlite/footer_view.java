package com.ttsproj.projectericlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 9/11/14.
 */
public class footer_view extends LinearLayout {

    private LayoutInflater inflater;
    private int count = CONSTANT.START;

    public footer_view(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.foot_banner, this, true);

        List<String> yes, no, more;

        yes = new ArrayList<String>();
        yes.add("Yes");
        yes.add("sí");

        no = new ArrayList<String>();
        no.add("No");
        no.add("no");

        more = new ArrayList<String>();
        more.add("More");
        more.add("más");

        TextView YesTxt, NoTxt, MoreTxt;

        YesTxt = (TextView)findViewById(R.id.footer1_text);
        NoTxt = (TextView)findViewById(R.id.footer2_text);
        MoreTxt = (TextView)findViewById(R.id.footer3_text);

        LinearLayout Yes = (LinearLayout) this.findViewById(R.id.footer1);
        LinearLayout No = (LinearLayout) this.findViewById(R.id.footer2);
        LinearLayout More = (LinearLayout) this.findViewById(R.id.footer3);

        //set text
        int lan = MyProperties.getInstance().Language.ordinal();

        YesTxt.setText(yes.get(lan));
        NoTxt.setText(no.get(lan));
        MoreTxt.setText(more.get(lan));

        // set listener
        Yes.setOnClickListener(new doubleTapListener(yes));
        No.setOnClickListener(new doubleTapListener(no));
        More.setOnClickListener(new moreOnclickListener());

    }

    private class moreOnclickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
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

                MyProperties.getInstance().speakout("More");
                MyProperties.getInstance().titleStack.push("Response");
                Intent intent = new Intent(getContext(), activity_response.class);
                getContext().startActivity(intent);
            }

        }
    }

    // register listeners


}
