package com.example.reem1;

import android.annotation.SuppressLint;
import android.telephony.PhoneNumberUtils;

public class PhoneNumberResolver {

    @SuppressLint("NewApi")
    public static String resolve (String number, String defaultPrefix){
        return PhoneNumberUtils.formatNumberToE164(number,defaultPrefix);

    }
}
