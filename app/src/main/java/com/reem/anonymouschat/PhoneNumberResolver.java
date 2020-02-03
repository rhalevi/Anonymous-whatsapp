package com.reem.anonymouschat;

import android.annotation.SuppressLint;
import android.telephony.PhoneNumberUtils;

public class PhoneNumberResolver {

    @SuppressLint("NewApi")
    public static String resolve (String number, String defaultPrefix){
        return PhoneNumberUtils.formatNumberToE164(number,defaultPrefix);

    }
}
