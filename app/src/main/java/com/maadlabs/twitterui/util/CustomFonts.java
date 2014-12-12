package com.maadlabs.twitterui.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by brainfreak on 10/29/14.
 */

public class CustomFonts {

    private static ViewGroup viewGroup;
    private static Typeface customTypeface;
    public static String PATH_TYPEFACE_CUSTOM = "fonts/Roboto-Light.ttf";

    public static Typeface getCustomTypeface(Context context) {
        if(customTypeface == null){
            //Only do this once for each typeface used
            //or we will leak unnecessary memory.
            customTypeface = Typeface.createFromAsset(context.getAssets(), PATH_TYPEFACE_CUSTOM);
        }
        return customTypeface;
    }

    public static void setTypeFace(Typeface typeFace, ViewGroup parent, Context context){
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                setTypeFace(typeFace, (ViewGroup) v, context);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(typeFace);
                tv.setPaintFlags(tv.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
            else if (v instanceof Button) {
                Button b = (Button) v;
                b.setTypeface(typeFace);
                b.setPaintFlags(b.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        }
    }

    public static void init(Context context, View view)
    {
        viewGroup = (ViewGroup) view;
        setTypeFace(getCustomTypeface(context), viewGroup, context);
    }

}
