package com.codingwithmitch.debttracker.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LayoutBuilder {


    public static LinearLayout buildDialogLayout(Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutForInner = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutForInner);

        int [] attributes = new int []{
                android.R.attr.dialogPreferredPadding,
                android.R.attr.dialogPreferredPadding,
                android.R.attr.dialogPreferredPadding,
                android.R.attr.dialogPreferredPadding,
        };
        TypedArray arr = context.obtainStyledAttributes(attributes);
        //and get values you need by indexes from your array attributes defined above
        int leftPadding = arr.getDimensionPixelOffset(0, -1);
        linearLayout.setPadding(
                leftPadding,
                30,
                leftPadding,
                30);
        arr.recycle();

        return linearLayout;
    }

}
















