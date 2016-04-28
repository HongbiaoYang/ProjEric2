package com.ttsproj.projectericlite;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 9/8/14.
 */
public class ListFactory {
    private final List<ItemStruct> root;

    public ListFactory(List<ItemStruct> root) {
        this.root = root;
    }

    public String[] produceTitleArray() {
        return (produceTitleArray(0));
    }

    public String[] produceTextArray() {
       return produceTextArray(0);
    }

    public Integer[] produceColorArray() {
        return produceColorArray(0);
    }

    public String[] produceTitleArray(int offset) {
        int i;

        ArrayList<String> titleList = new ArrayList<String>();
        for (i = offset; i < this.root.size(); i++) {
            // language for display. If spanish, need to display both, input LANG.EN_SP
            LANG lang = MyProperties.getInstance().Language == LANG.ENGLISH? LANG.ENGLISH : LANG.EN_SP;

            titleList.add(root.get(i).getTitle(lang));
        }
        return titleList.toArray(new String[0]);

    }

    private String[] produceTextArray(int offset) {
        int i;

        ArrayList<String> textList = new ArrayList<String>();

        for (i = offset; i < this.root.size(); i++) {
            textList.add(root.get(i).getText(MyProperties.getInstance().Language));
        }

        return textList.toArray(new String[0]);
    }

    public Integer[] produceImageArray( Context applicationContext) {
        int i;

        ArrayList<Integer> imageList = new ArrayList<Integer>();
        for (i = 0; i < this.root.size(); i++) {

            int tImageId = applicationContext.getResources().getIdentifier(root.get(i).getImageString(),
                    "drawable", applicationContext.getPackageName());

            imageList.add(tImageId);
        }

        return imageList.toArray(new Integer[0]);
    }

    private Integer[] produceColorArray(int offset) {
        int i;

        // color list
        ArrayList<Integer> colorList = new ArrayList<Integer>();
        for (i = offset; i < this.root.size(); i++) {
            colorList.add(root.get(i).getColorCode());
        }

        return colorList.toArray(new Integer[0]);
    }
}
