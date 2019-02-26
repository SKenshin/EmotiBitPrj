package com.lab.uqac.emotibit.application.launcher.Datas;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MessageGenerator {


    public static String DATE_FORMAT = "y-MM-dd_HH-mm-ss-SSS";

    static int mPacketNumber = 0;


    public static String generateMessageWithLocalTime(TypesDatas typesDatas, int dataLength,
                                                      int protocolVersion, int reliability){

        String message = "";

        message = "" + System.currentTimeMillis();
        message += "," + mPacketNumber;
        message += "," + dataLength;
        message += "," + typesDatas.getmTag();
        message += "," + protocolVersion;
        message += "," + reliability;
        message += "," + getLocalTime();

        return message;
    }


    public static String generateMessageWithUTCTime(TypesDatas typesDatas, int dataLength,
                                                    int protocolVersion, int reliability){

        String message = "";

        message = "" + System.currentTimeMillis();
        message += "," + mPacketNumber;
        message += "," + dataLength;
        message += "," + typesDatas.getmTag();
        message += "," + protocolVersion;
        message += "," + reliability;
        message += "," + getUTCTime();

        return message;
    }


    public static String getUTCTime(){

        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));

        return dateFormat.format(date);
    }

    public static String getLocalTime(){

        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        return dateFormat.format(date);
    }

}
