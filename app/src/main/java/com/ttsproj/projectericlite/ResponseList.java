package com.ttsproj.projectericlite;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Bill on 9/08/14.
 */
public class ResponseList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;

    private int textStyle, backgroundColor;
    private boolean colorChanged = false;

    public ResponseList(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.response_single, web);

        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    public void setColors(int style, int background) {
        this.textStyle = style;
        this.backgroundColor = background;
        this.colorChanged = true;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.response_single, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        imageView.setImageResource(imageId[position]);

        if (colorChanged == true) {
            txtTitle.setTextAppearance(context, textStyle);
            txtTitle.setBackgroundResource(backgroundColor);
            imageView.setBackgroundResource(backgroundColor);
        }

        return rowView;
    }
}