package com.ttsproj.projectericlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Bill on 9/11/14.
 */
public class header_view extends LinearLayout {

    private LayoutInflater inflater;
    private int count = 0;

    public header_view(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.head_banner, this, true);

        // create a new animation and push it into stack
        ImageView image = (ImageView) findViewById(R.id.frame_image);
        image.setBackgroundResource(R.drawable.frame);
//        MyProperties.getInstance().animStack.push((AnimationDrawable) image.getBackground());
        MyProperties.getInstance().currentAnim = (AnimationDrawable) image.getBackground();

        this.findViewById(R.id.header3).setOnClickListener(new homeOnClickListener());

    }

    private class homeOnClickListener implements OnClickListener {

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

                // MyProperties.getInstance().doInit(LANG.ENGLISH);
                String rootItem = MyProperties.getInstance().titleStack.firstElement();
                MyProperties.getInstance().titleStack.clear();

                // restore the root animation object
                /*AnimationDrawable rootAnim = MyProperties.getInstance().animStack.firstElement();
                MyProperties.getInstance().animStack.clear();
                MyProperties.getInstance().animStack.push(rootAnim);
                */

                // go to the sub-root level (activity_hearing). If directly come from the root level, go back there
               Intent intent;

                /*   if (rootItem.equalsIgnoreCase("Response")) {
                    intent = new Intent(getContext(), activity_main.class);
                }  else {
                    MyProperties.getInstance().titleStack.push(rootItem);
                    intent = new Intent(getContext(), activity_main.class);
                }*/

                intent = new Intent(getContext(), activity_home.class);
                MyProperties.getInstance().titleStack.clear();

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
            }

        }
    }

    // register listeners


}
