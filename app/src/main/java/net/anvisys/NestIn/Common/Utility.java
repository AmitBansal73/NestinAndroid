package net.anvisys.NestIn.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Amit Bansal on 22-10-2016.
 */
public class Utility {

    static final String DATEFORMAT = "dd/MM/yyyy HH:mm:ss";
    static final String INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    static final String DATE_TIME_Display_FORMAT = "dd,MMM' @ 'hh:mm a";
    static final String TIME_Display_FORMAT = "hh:mm a";

    public static Date StringToDate(String date)
    {
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);
            Date dateTime = idf.parse(date);
            return dateTime;
        }
        catch (Exception ex)
        {
            return new Date();
        }
    }

    public static Date DBStringToLocalDate(String date)
    {
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);
            Date dateTime = idf.parse(date);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            return localDate;
        }
        catch (Exception ex)
        {
            return new Date();
        }

    }

    public static String ChangeFormat(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);

            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);

            String time = c.get(Calendar.HOUR_OF_DAY) +":" + c.get(Calendar.MINUTE);

            if (year == CurrentYear())
            {
                return Integer.toString(day) + ", " + Month + " at " + time;
            }
            else
            {
                return Integer.toString(day) + " " + Month +"," + Integer.toString(year) + " at " + time;
            }
       }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String GetDate(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);

            String time = c.get(Calendar.HOUR_OF_DAY) +":" + c.get(Calendar.MINUTE);

            if (year == CurrentYear())
            {
                return Integer.toString(day) + "\n" + Month;
            }
            else
            {
                return Integer.toString(day) + "\n" + Month;// +"," + Integer.toString(year) + " at " + time;
            }
        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }


    public static String GetDateOnly(String inDate)
    {
        String OutDate = "";
        try {

            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);
            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);
            int Month = c.get(Calendar.MONTH)+1;
            int year = c.get(Calendar.YEAR);
            return Integer.toString(day) + "/" + Integer.toString(Month)  +"/" + Integer.toString(year);

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static int GetYearOnly(String inDate)
    {
        int year = 0;
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);


            year = c.get(Calendar.YEAR);

        }
        catch (Exception ex)
        {
            int a =5;

        }
        return year;

    }


    public static String GetDayOnly(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            int Month = c.get(Calendar.MONTH)+1;

            int year = c.get(Calendar.YEAR);
            return Integer.toString(day);

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String GetMonthOnly(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            int Month = c.get(Calendar.MONTH)+1;

            int year = c.get(Calendar.YEAR);
            return   c.getDisplayName(Calendar.MONTH,Calendar.SHORT ,Locale.getDefault());

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }
    public static String GetTimeOnly(String inDate)
    {
        String time = "00:00";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(dateTime);

            int currHrs = c.get(c.HOUR_OF_DAY);
            int currMin = c.get(c.MINUTE);

            time = Integer.toString(currHrs) + ":" + Integer.toString(currMin);

        }
        catch (Exception ex)
        {
            int a = 1;
        }
        return time;
    }


    public static String GetUTCDateTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(new Date());
        return utcTime;
    }

    public static int CurrentYear()
    {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int currYear =   today.year;
        return currYear;
    }

    public static int CurrentMonth()
    {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int currMonth =   today.month;
        return currMonth+1;
    }

    public static int CurrentMonthDay()
    {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int currDay =   today.monthDay;
        return currDay;

    }

    public static String CurrentDate()
    {
        DateFormat formatter = new SimpleDateFormat(INPUT_DATE_FORMAT);
        //DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = formatter.format(new Date());
        return currentDate;
    }

    public static String CurrentTime()
    {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int currHrs =   today.hour;
        int currMin =   today.minute;
        int second =   today.second;
        String dateTime = Integer.toString(currHrs) + ":" + Integer.toString(currMin);
        return dateTime;
    }

    public static boolean isPrevious(String Date)
    {
        try
        {
            String [] sDate = Date.split("/");
            int dYear  =  Integer.parseInt(sDate[2]);
            int dMonth = Integer.parseInt(sDate[1]);
            int dDate  =  Integer.parseInt(sDate[0]);

            if (dYear<CurrentYear()) {
                return true;
            }
            else if (dYear>CurrentYear())
            {return false;}

            if ((dMonth<CurrentMonth())) {
                return true;
            }
            else if (dMonth>CurrentMonth())
            {return false;}

            if (dDate<CurrentMonthDay()){
                return true;
            }
            else if (dDate>CurrentMonthDay()){
                return false;
            }
            else { return false;}

        }
        catch (Exception ex)
        {
            return false;
        }

    }



    public static boolean IsConnected(Context context)
    {
        boolean isConnected = false;
        try
        {
            NetworkInfo networkInfo=null;

                ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connManager.getActiveNetworkInfo();
           if(networkInfo!= null)
           {
               isConnected = true;
           }
            else
           {
               isConnected = false;
           }

            return  isConnected;
        }
        catch ( Exception ex)

        {
            return isConnected;
        }
    }

    public static void HandleException(Context context, String Method, String ex)
    {
        Toast.makeText(context, Method + ":" + ex, Toast.LENGTH_LONG).show();

    }

    public static String ChangeToMonthDisplayFormat(String inDate)
    {
        String OutDate = "";
        try {
            Date dateTime;
            try {
                SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);
                dateTime = idf.parse(inDate);
            }
            catch (Exception ex)
            {
                SimpleDateFormat idf = new SimpleDateFormat(INPUT_DATE_FORMAT);
                dateTime = idf.parse(inDate);
            }

            Calendar c = Calendar.getInstance(Locale.getDefault());
            int CurrentYear = c.get(Calendar.YEAR);
            int CurrentDay = c.get(Calendar.DAY_OF_YEAR);

            c.setTime(dateTime);
            int day =  c.get(Calendar.DAY_OF_MONTH);
            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);

            return Month + "," + year;
        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String DateToDataBaseString(Date date)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(INPUT_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String LocalTime = sdf.format(date);
            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }


    public static String DateToDisplayDateTime(Date date)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_Display_FORMAT);

            String LocalTime = sdf.format(date);

            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }


    public static String DateToDisplayTimeOnly(Date date)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_Display_FORMAT);

            String LocalTime = sdf.format(date);

            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }


    public static String GetCurrentDateTimeLocal()
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(INPUT_DATE_FORMAT);

            String LocalTime = sdf.format(new Date());
            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }


}
