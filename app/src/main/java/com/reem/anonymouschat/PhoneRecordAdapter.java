package com.reem.anonymouschat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;


public class PhoneRecordAdapter extends BaseAdapter {

    private Context context;
    private List<PhoneRecord> phoneRecordList;

    public PhoneRecordAdapter(Context context, List<PhoneRecord> phoneRecordList ) {
        this.context = context;
        this.phoneRecordList = phoneRecordList;
    }

    @Override
    public int getCount() {
        return phoneRecordList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return phoneRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return new PhoneRecordAdapterView(context,phoneRecordList.get(position));
    }
}
