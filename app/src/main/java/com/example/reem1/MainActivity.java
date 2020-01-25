package com.example.reem1;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.CallLog;
import android.provider.ContactsContract;
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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class MainActivity extends AppCompatActivity {

    CountryCodePicker ccp = null;
    Map<String,String> numberToPersonNameCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ListView lv = (ListView) findViewById(R.id.listView);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberText);

        ccp.registerCarrierNumberEditText(phoneNumber);
        ccp.detectSIMCountry(true);
        ccp.setCountryPreference("US");


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
                    clearListView();
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
                    clearListView();
                }
            }
        });




        final Button button = (Button) findViewById(R.id.SendButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone="+ccp.getFormattedFullNumber()));
                startActivity(intent);
            }
        });
    }

    private void clearListView() {
        ListView lv = (ListView) findViewById(R.id.listView);
        List<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    private void normalizeContryCodeAccordingToGivenNumber(String phone, CountryCodePicker ccp) {
        if(phone.startsWith("+")){
            String currentDefaultCode = ccp.getSelectedCountryCode();
            if(!phone.substring(1).startsWith(currentDefaultCode)){
                try {
                    Phonenumber.PhoneNumber parse = PhoneNumberUtil.getInstance().parse(phone, "");
                    ccp.setCountryForPhoneCode(parse.getCountryCode());
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }

            }

        }else{
            ccp.detectSIMCountry(true);
        }
    }

    private void loadCallsToListView() {

        ListView lv = (ListView) findViewById(R.id.listView);
        final LinkedHashSet<PhoneRecord> allSms = getAllIncomingCalls();
        Toast.makeText(getApplicationContext(), "Number of calls loaded:  "+allSms.size(), Toast.LENGTH_SHORT).show();
        List<PhoneRecord> list = new ArrayList<>(allSms);
        PhoneRecordAdapter itemsAdapter =
                new PhoneRecordAdapter(this, list);
        lv.setAdapter(itemsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                setPhoneInTextboxFromListView(parent, position);
            }
        });
    }

    private void loadSMSToListView() {

        ListView lv = (ListView) findViewById(R.id.listView);
        final LinkedHashSet<PhoneRecord> allSms = getAllSmsFromProvider();
        Toast.makeText(getApplicationContext(), "Number of sms loaded:  "+allSms.size(), Toast.LENGTH_SHORT).show();
        List<PhoneRecord> list = new ArrayList<>(allSms);
           PhoneRecordAdapter itemsAdapter =
                new PhoneRecordAdapter(this, list);
        lv.setAdapter(itemsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                setPhoneInTextboxFromListView(parent, position);
            }
        });
    }

    private void setPhoneInTextboxFromListView(AdapterView<?> parent, int position) {
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberText);
        phoneNumber.setText(parent.getItemAtPosition(position).toString());
        normalizeContryCodeAccordingToGivenNumber(phoneNumber.getText().toString(),ccp);
    }

    public LinkedHashSet<PhoneRecord> getAllSmsFromProvider() {
        LinkedHashSet<PhoneRecord> lstSms = new LinkedHashSet<PhoneRecord>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.ADDRESS}, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                String senderPhone = c.getString(0);
                String senderName = getContactName(senderPhone);
                PhoneRecord phoneRecord = new PhoneRecord(senderPhone,senderName);
                if(!filter(senderPhone)) {
                    lstSms.add(phoneRecord);
                }
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        return lstSms;
    }

    public LinkedHashSet<PhoneRecord> getAllIncomingCalls() throws SecurityException {
        LinkedHashSet<PhoneRecord> lstSms = new LinkedHashSet<PhoneRecord>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(CallLog.Calls.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { CallLog.Calls.NUMBER}, // Select body text
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER); // Default sort order

        int totalCalls = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalCalls; i++) {
                String senderPhone = c.getString(0);
                String senderName = getContactName(senderPhone);
                PhoneRecord phoneRecord = new PhoneRecord(senderPhone,senderName);
                if(!filter(senderPhone)) {
                    lstSms.add(new PhoneRecord(senderPhone,senderName));
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

    public String getContactName(String phoneNumber) {
        if(numberToPersonNameCache.isEmpty()) {

            ContentResolver cr = getBaseContext().getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = cr.query(uri,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            if (cursor == null) {
                return "";
            }
            Toast.makeText(getApplicationContext(), "total contacts  :"+cursor.getCount(), Toast.LENGTH_SHORT).show();


            while (cursor.moveToNext()) {
                String contactName = "";
                String contactPhone = "";
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(contactPhone!=null && contactName !=null) {
                    numberToPersonNameCache.put(contactPhone, contactName);
                }

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
           Toast.makeText(getApplicationContext(), "contacts loaded to cache :"+numberToPersonNameCache.size(), Toast.LENGTH_SHORT).show();



        }
        return numberToPersonNameCache.get(phoneNumber);
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
