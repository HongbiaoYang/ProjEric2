package com.ttsproj.projectericlite;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Bill on 9/08/14.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    private final Integer[] colorList;

    private int textStyle, backgroundColor;
    private int xmlayout;
    private boolean colorful = false;
    private ArrayList<Integer> colorArray;

    public CustomList(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);

        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.xmlayout = R.layout.list_single;
        this.colorList = new Integer[0];
    }

    public CustomList(Activity context, String[] web, Integer[] imageId, int xml, Integer[] colors) {
        super(context, R.layout.list_single, web);

        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.xmlayout = xml;
        this.colorList = colors;
    }

    public void setColors(boolean colorful) {
        this.colorful = colorful;
        colorArray = new ArrayList<Integer>();

    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(xmlayout, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        imageView.setImageResource(imageId[position]);

        if (colorful == true) {
/*
            final Random rand = new Random();
            int dice = rand.nextInt(colorArray.size());
            dice = position % colorArray.size();

            txtTitle.setBackgroundColor(colorArray.get(dice));
            imageView.setBackgroundColor(colorArray.get(dice));
*/
            txtTitle.setBackgroundColor(colorList[position]);
            imageView.setBackgroundColor(colorList[position]);
        } else {
            Drawable blue = context.getResources().getDrawable(R.drawable.back_blue);
            Drawable blueTxt = context.getResources().getDrawable(R.drawable.btn_blue);

            txtTitle.setBackground(blue);
            imageView.setBackground(blue);
        }



        return rowView;
    }
}