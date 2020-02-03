package com.reem.anonymouschat;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;


class PhoneRecordAdapterView extends LinearLayout {
        public PhoneRecordAdapterView(Context context,
                                      PhoneRecord phoneRecord) {
            super( context );
            this.setOrientation(HORIZONTAL);

            LinearLayout.LayoutParams cityParams = 
                new LinearLayout.LayoutParams(400, LayoutParams.WRAP_CONTENT);
            cityParams.setMargins(10, 10, 10, 10);
 
            TextView numberControl = new TextView( context );
            numberControl.setText( phoneRecord.getPhone() );
            numberControl.setTextSize(16f);
            numberControl.setTextColor(Color.BLACK);
            addView( numberControl, cityParams);


            TextView nameControl = new TextView( context );
            nameControl.setText( phoneRecord.getName() );
            nameControl.setTextSize(16f);
            nameControl.setTextColor(Color.BLUE);
            addView( nameControl, cityParams);

        }
}