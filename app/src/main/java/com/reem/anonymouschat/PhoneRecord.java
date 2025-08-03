package com.reem.anonymouschat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class PhoneRecord {

    private String phone;
    private String name;
    private String date;

    @Override
    public String toString() {
        return getPhone();
    }

    public PhoneRecord(String phone, String name,String date) {
        this.phone = phone;
        this.name = name;
        this.date = date;
    }

    public String getPhone() {
        if(phone == null){
            return "";
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        if(name == null){
            return "";
        }
        return name;
    }
    public String getDate(){
        return convertEpochToReadable(date);
       // return date;
    }

    public static String convertEpochToReadable(String epochTime) {
        // Parse the epoch time string to a long value
        long epochMillis = Long.parseLong(epochTime);

        // Convert epoch time to LocalDateTime in the system's default time zone
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

        // Format the LocalDateTime to the specified format
        return dateTime.format(formatter);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneRecord that = (PhoneRecord) o;
        return phone.equals(that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }
}
