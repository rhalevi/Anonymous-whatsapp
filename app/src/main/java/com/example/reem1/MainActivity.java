package com.example.reem1;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.CallLog;
import android.provider.Telephony;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ListView lv = (ListView) findViewById(R.id.listView);

        final Switch useSmsSwitch = (Switch) findViewById(R.id.sms_switch);
        final Switch useCallsSwitch = (Switch) findViewById(R.id.calls_switch);
        useSmsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    useCallsSwitch.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Sms numbers lookup activated", Toast.LENGTH_SHORT).show();
                    loadSMSToListView();
                }else{
                    Toast.makeText(getApplicationContext(), "Sms numbers lookup off", Toast.LENGTH_SHORT).show();
                    ListView lv = (ListView) findViewById(R.id.listView);
                    List<String> list = new ArrayList<String>();
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                    lv.setAdapter(adapter);
                }
            }
        });


        useCallsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    useSmsSwitch.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Call numbers lookup activated", Toast.LENGTH_SHORT).show();
                    loadCallsToListView();
                }else{
                    Toast.makeText(getApplicationContext(), "Call numbers lookup off", Toast.LENGTH_SHORT).show();
                    ListView lv = (ListView) findViewById(R.id.listView);
                    List<String> list = new ArrayList<String>();
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                    lv.setAdapter(adapter);
                }
            }
        });




        final Button button = (Button) findViewById(R.id.SendButton);
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberText);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber.getText()));
                startActivity(intent);


            }
        });
    }

    private void loadCallsToListView() {

        ListView lv = (ListView) findViewById(R.id.listView);
        final LinkedHashSet<String> allSms = getAllIncomingCalls();
        Toast.makeText(getApplicationContext(), "Number of calls loaded:  "+allSms.size(), Toast.LENGTH_SHORT).show();
        List<String> list = new ArrayList<String>(allSms);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(itemsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberText);
                String resolvedPhone = PhoneNumberResolver.resolve(parent.getItemAtPosition(position).toString(),"IL");
                phoneNumber.setText(resolvedPhone);
            }
        });
    }

    private void loadSMSToListView() {

        ListView lv = (ListView) findViewById(R.id.listView);
        final LinkedHashSet<String> allSms = getAllSmsFromProvider();
        Toast.makeText(getApplicationContext(), "Number of sms loaded:  "+allSms.size(), Toast.LENGTH_SHORT).show();
        List<String> list = new ArrayList<String>(allSms);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(itemsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberText);
                String resolvedPhone = PhoneNumberResolver.resolve(parent.getItemAtPosition(position).toString(),"IL");
                phoneNumber.setText(resolvedPhone);
            }
        });
    }

    public LinkedHashSet<String> getAllSmsFromProvider() {
        LinkedHashSet<String> lstSms = new LinkedHashSet<String>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.ADDRESS }, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                String sender = c.getString(0);
                if(!filter(sender)) {
                    lstSms.add(sender);
                }
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        return lstSms;
    }

    public LinkedHashSet<String> getAllIncomingCalls() throws SecurityException {
        LinkedHashSet<String> lstSms = new LinkedHashSet<String>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(CallLog.Calls.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { CallLog.Calls.NUMBER}, // Select body text
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER); // Default sort order

        int totalCalls = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalCalls; i++) {
                String sender = c.getString(0);
                if(!filter(sender)) {
                    lstSms.add(sender);
                }
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no incoming calls");
        }
        c.close();

        return lstSms;
    }

    boolean filter(String str){
        return str.matches("[a-z]+.*|[A-Z]+.*");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
