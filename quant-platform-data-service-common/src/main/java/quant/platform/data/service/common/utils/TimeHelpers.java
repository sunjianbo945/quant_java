package quant.platform.data.service.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeHelpers {

    //default formatter
    private String formatter = "yyyy-MM-dd";

    public String getFormatter(){
        return formatter;
    }

    public void setFormatter(String formatter){
        this.formatter=formatter;
    }

    public String convertLongToGMTDateString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date(time));
    }

    public String convertLongToLocalTimeDateString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(time));
    }


    public String convertLongToSpecificTimezoneTimeDateString(long time, String timezoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        sdf.setTimeZone(TimeZone.getTimeZone(timezoneId));
        return sdf.format(new Date(time));
    }

    public static Date nextTime(Date date, int filed,int num ) throws ParseException {
        return nextTime(date,TimeZone.getDefault(),filed,num);
    }

    public static Date nextTime(Date date, TimeZone zone, int filed, int num ) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(zone);
        c.setTime(date);
        c.add(filed,num);
        return c.getTime();
    }

    public long convertStringToLongGMT(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(date).getTime();
    }

    public static TimeHelpers getUTCTimeConverter(){
        String formatter = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        TimeHelpers converter = new TimeHelpers();
        converter.setFormatter(formatter);
        return converter;
    }

    public boolean canParseString(String date){
        boolean flag = true;
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            System.out.println(String.format("Date = %s cannot be parse by format %s",date,formatter));
            flag = false;
            e.printStackTrace();
        }finally {
            return flag;
        }
    }

    public Date parse(String date,TimeZone zone) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        sdf.setTimeZone(zone);
        return sdf.parse(date);
    }

    public static long floorDateTimeToHourLong(long input){

        return input/3600000*3600000;
    }

    public static long ceilingDateTimeToHourLong(long input){

        return (input/3600000 +1)*3600000;
    }

    public static long floorMillsecLongToMinLong(long input){

        return input/60000*60000;
    }

    public static long floorUnixLongToMinLong(long input){

        return input/60*60;
    }



}
