package com.codingwithmitch.debttracker.util;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DateConverterUtil {

    public static String getStringFormattedDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DateConverterUtil.getMonthFromNumber(calendar.get(Calendar.MONTH)) + " "
                + calendar.get(Calendar.DAY_OF_MONTH) + ", "
                + calendar.get(Calendar.YEAR);
    }

    public static String getMonthFromNumber(int monthNumber){
        switch(monthNumber){
            case 0:{
                return "Jan";
            }
            case 1:{
                return "Feb";
            }
            case 2:{
                return "Mar";
            }
            case 3:{
                return "Apr";
            }
            case 4:{
                return "May";
            }
            case 5:{
                return "Jun";
            }
            case 6:{
                return "Jul";
            }
            case 7:{
                return "Aug";
            }
            case 8:{
                return "Sep";
            }
            case 9:{
                return "Oct";
            }
            case 10:{
                return "Nov";
            }
            case 11:{
                return "Dec";
            }

            default:{
                return "Error";
            }
        }
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

}












