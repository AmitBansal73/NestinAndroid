package net.anvisys.NestIn.Common;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Amit Bansal on 18-10-2016.
 */
public class Session {


    public static boolean AddUser(Context context, Profile myProfile)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserID", myProfile.UserID);
            editor.putString("FirstName",myProfile.NAME);
            editor.putString("Email", myProfile.E_MAIL);
            editor.putString("MobileNumber", myProfile.MOB_NUMBER);
            editor.putString("Password",myProfile.password);
            editor.putString("Loation",myProfile.LOCATION);
            editor.putString("RegistrationID",myProfile.REG_ID);
            editor.putString("Address",myProfile.Address);
            editor.putString("Gender",myProfile.Gender);
            editor.putString("ParentName",myProfile.ParentName);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static Profile GetUser(Context context)
    {
        Profile mProfile = new Profile();
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            mProfile.UserID =  prefs.getString("UserID","");
            mProfile.NAME = prefs.getString("FirstName","");
            mProfile.E_MAIL =  prefs.getString("Email","");
            mProfile.MOB_NUMBER =  prefs.getString("MobileNumber","");
            mProfile.password = prefs.getString("Password","");
            mProfile.LOCATION = prefs.getString("Loation","");
            mProfile.REG_ID = prefs.getString("RegistrationID","");
            mProfile.Address= prefs.getString("Address","");
            mProfile.Gender = prefs.getString("Gender","");
            mProfile.ParentName= prefs.getString("ParentName","");
            mProfile.strImage= prefs.getString("strImage","");
            return mProfile;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static boolean AddFlatListNO(Context context, ArrayList<SocietyUser> FlatList)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("FlatList", ObjectSerializer.serialize(FlatList));

            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static ArrayList<SocietyUser> GetFlatListNO(Context context)
    {
        ArrayList<SocietyUser> flats;

        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);

            flats = (ArrayList<SocietyUser>) ObjectSerializer.deserialize(prefs.getString("FlatList",  ObjectSerializer.serialize(new ArrayList<SocietyUser>())));

            return flats;
        }
        catch (Exception ex)
        {
            return null;
        }
    }



    public static boolean AddCurrentSocietyUser(Context context, SocietyUser myProfile)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            //editor.putInt("UserID", myProfile.UserID);
            editor.putInt("ResID", myProfile.ResID);
            editor.putString("SocietyName", myProfile.SocietyName);
            editor.putInt("FlatID",myProfile.FlatID);
            editor.putString("FlatNumber",myProfile.FlatNumber);
            editor.putInt("SocietyId",myProfile.SocietyId);
            editor.putString("RoleType",myProfile.RoleType);
            editor.putString("intercomNumber",myProfile.intercomNumber);

            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static SocietyUser GetCurrentSocietyUser(Context context)
    {
        SocietyUser mProfile = new SocietyUser();
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            mProfile.ResID =  prefs.getInt("ResID",0);
            mProfile.SocietyName =  prefs.getString("SocietyName","");
            mProfile.FlatID =  prefs.getInt("FlatID",0);
            mProfile.FlatNumber =  prefs.getString("FlatNumber","");
            mProfile.SocietyId = prefs.getInt("SocietyId",1);
            mProfile.RoleType = prefs.getString("RoleType","");
            mProfile.intercomNumber = prefs.getString("intercomNumber","");
            return mProfile;
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    public static String GetSocietyName(Context context)
    {
        String society = "";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            society =  prefs.getString("SocietyName","");
            return society;
        }
        catch (Exception ex)
        {
            return society;
        }
    }

    public static void SetNoticeCount(Context context, int value)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
           // int count = prefs.getInt("NoticeCount", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("NoticeCount", value);
            editor.commit();

        }
        catch (Exception ex)
        {

        }
    }

    public static int GetNoticeCount(Context context)
    {
        int count = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            count =  prefs.getInt("NoticeCount", 0);
            return count;
        }
        catch (Exception ex)
        {
            return count;
        }
    }

    public static void LogOff(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

    }


    public static boolean SetVendorRefreshTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String time = Utility.GetUTCDateTime();
            editor.putString("Vendor_Refresh_Time", time);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetVendorRefreshTime(Context context)
    {
        String vendorRefreshTime ="01/01/2000 12:00:00";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            vendorRefreshTime =  prefs.getString("Vendor_Refresh_Time", "01/01/2000 12:00:00");
            return vendorRefreshTime;
        }
        catch (Exception ex)
        {
            return "01/01/2000 12:00:00";
        }
    }

    public static boolean SetForumRefreshTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String time = Utility.GetUTCDateTime();
            editor.putString("Forum_Refresh_Time", time);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetForumRefreshTime(Context context)
    {
        String vendorRefreshTime ="01/01/2000 12:00:00";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            vendorRefreshTime =  prefs.getString("Forum_Refresh_Time", "01/01/2000 12:00:00");
            return vendorRefreshTime;
        }
        catch (Exception ex)
        {
            return "01/01/2000 12:00:00";
        }
    }

    public static boolean SetComplaintRefreshTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String time = Utility.GetUTCDateTime();
            editor.putString("Complaint_Refresh_Time", time);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetComplaintRefreshTime(Context context)
    {
        String vendorRefreshTime ="01/01/2000 12:00:00";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            vendorRefreshTime =  prefs.getString("Complaint_Refresh_Time", "01/01/2000 12:00:00");
            return vendorRefreshTime;
        }
        catch (Exception ex)
        {
            return "01/01/2000 12:00:00";
        }
    }

    public static boolean SetPollRefreshTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String time = Utility.GetUTCDateTime();
            editor.putString("Poll_Refresh_Time", time);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetPollRefreshTime(Context context)
    {
        String vendorRefreshTime ="01/01/2000 12:00:00";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            vendorRefreshTime =  prefs.getString("Poll_Refresh_Time", "01/01/2000 12:00:00");
            return vendorRefreshTime;
        }
        catch (Exception ex)
        {
            return "01/01/2000 12:00:00";
        }
    }


    public static boolean SetBillRefreshTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String time = Utility.GetUTCDateTime();
            editor.putString("Bill_Refresh_Time", time);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetBillRefreshTime(Context context)
    {
        String vendorRefreshTime ="01/01/2000 12:00:00";
        try {
            SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
            vendorRefreshTime =  prefs.getString("Bill_Refresh_Time", "01/01/2000 12:00:00");
            return vendorRefreshTime;
        }
        catch (Exception ex)
        {
            return "01/01/2000 12:00:00";
        }
    }

    public static String GetRegID(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("SCMSession", Context.MODE_PRIVATE);
        if (prefs != null) {
            return prefs.getString("regID", "");
        }
        else
            return "";
    }

    public static void StoreRegIdInSharedPref(Context context, String regId,String Mobile) {
        SharedPreferences prefs = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("regID", regId);
        editor.putString("MobileNo", Mobile);
        editor.commit();
        //storeRegIdinServer();
    }
}
