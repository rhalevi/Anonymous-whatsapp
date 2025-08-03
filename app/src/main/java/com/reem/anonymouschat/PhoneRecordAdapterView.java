package com.reem.anonymouschat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reem.anonymouschat.PhoneRecord;

public class PhoneRecordAdapterView extends LinearLayout {
    public PhoneRecordAdapterView(Context context, PhoneRecord phoneRecord) {
        super(context);
        this.setOrientation(HORIZONTAL);

        // Set the gravity of the LinearLayout to start (left alignment)
        this.setGravity(Gravity.START);

        // Define equal weight for each column
        float weight = 1.0f;

        // LayoutParams for the first column with 3/5 width
        LinearLayout.LayoutParams firstColumnParams =
                new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f);
        firstColumnParams.setMargins(10, 10, 10, 10);

        // LayoutParams for the second and third columns with 1/5 width each
        LinearLayout.LayoutParams otherColumnsParams =
                new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        otherColumnsParams.setMargins(10, 10, 10, 10);

        // Create numberControl TextView (first column)
        TextView numberControl = new TextView(context);
        numberControl.setText(phoneRecord.getPhone());
        numberControl.setTextSize(16f);
        numberControl.setTextColor(Color.BLACK);
        numberControl.setGravity(Gravity.START);  // Set gravity to start for left alignment
        addView(numberControl, firstColumnParams);

        // Create nameControl TextView (second column)
        TextView nameControl = new TextView(context);
        nameControl.setText(phoneRecord.getName());
        nameControl.setTextSize(16f);
        nameControl.setTextColor(Color.BLUE);
        nameControl.setGravity(Gravity.START);  // Set gravity to start for left alignment
        addView(nameControl, otherColumnsParams);

        // Create dateControl TextView (third column)
        TextView dateControl = new TextView(context);
        dateControl.setText(phoneRecord.getDate());
        dateControl.setTextSize(12f);
        dateControl.setTextColor(Color.BLUE);
        dateControl.setGravity(Gravity.START);  // Set gravity to start for left alignment
        addView(dateControl, otherColumnsParams);
    }
}