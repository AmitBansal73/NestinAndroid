package net.anvisys.NestIn.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.anvisys.NestIn.Object.Bill;
import net.anvisys.NestIn.Object.Complaint;
import net.anvisys.NestIn.Object.Domain;
import net.anvisys.NestIn.Object.Forum;
import net.anvisys.NestIn.Object.Messages;
import net.anvisys.NestIn.Object.Polling;
import net.anvisys.NestIn.Object.Vendor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Amit Bansal on 23-10-2016.
 */
public class DataAccess {

    //MemberData memberData=new MemberData();
	/*String message="";
    String ShowData="";*/
    private static final String DATABASE_NAME = "societyManagement.db";
    private static final String TABLE_NAME = "member_master";

    private static final String COMP_TYPE = "complaint_type";
    private static final String COMP_STATUS = "complaint_status";
    private static final String VENDOR_CATEGORY = "vendor_category";
    private static final String TABLE_SOCIETY_USER = "table_society_user";
    private static final String BILL_TYPE = "bill_type";


    private static final int DATABASE_VERSION=2;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase myDatabase;
    private final Context mCtx;
    private static final String TABLE_POLL = "Table_Poll";
    private static final String TABLE_COMPLAINT = "Table_Complaint";
    private static final String TABLE_CREATE_USER="CREATE TABLE IF NOT EXISTS "
            + "member_master"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "firstname VARCHAR(20),middlename VARCHAR(20),lastname VARCHAR(20), mobile_no VARCHAR(10),"
            + "password varchar(10),email VARCHAR(20),userLogin VARCHAR(20),societyName VARCHAR(20),"
            + "society_id int, status varchar(2));";

    private static final String TABLE_CREATE_NOTICE="CREATE TABLE IF NOT EXISTS "
            + "Table_Notice"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "notice_id int,"
            + "notice TEXT VARCHAR(20),"
            + "is_file int,"
            + "file_name TEXT VARCHAR(20),"
            + "sent_by int,"
            + "created_at DATETIME,society_id int, valid_till DATETIME);";

    private static final String TABLE_CREATE_VENDOR="CREATE TABLE IF NOT EXISTS "
            + "Table_Vendor"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, society_id int,"
            + "DB_ID INTEGER, Shop_Category VARCHAR(20),Vendor_Name VARCHAR(20),"
            + " Address VARCHAR(20), ContactNum VARCHAR(20),imgBMP BLOB,created_at DATETIME);";

    private static final String TABLE_CREATE_FORUM="CREATE TABLE IF NOT EXISTS "
            + "Table_Forum"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "THREAD_ID INTEGER, Topic VARCHAR(20),FirstUser VARCHAR(20),FirstFlat VARCHAR(20), FirstThread VARCHAR(20), InitiatedAt datetime(20),"
            + "LatestUser VARCHAR(20), LatestFlat VARCHAR(20),LatestThread VARCHAR(20), LatestAt datetime(20),FirstImage BLOB,LastImage BLOB,"
            + "First_User_Id INTEGER, Last_User_id INTEGER, Comments_Count INTEGER, First_Id INTEGER, society_id int);";

    private static final String TABLE_CREATE_COMPLAINT="CREATE TABLE IF NOT EXISTS "
            + "Table_Complaint"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "COMP_ID INTEGER,First_Id INTEGER, ModifiedAt datetime(20),Description VARCHAR(20), resident_id int,"
            + " CompStatus VARCHAR(20), CompType VARCHAR(20),Severity VARCHAR(20),LatestComment VARCHAR(20), AssignedTo VARCHAR(20));";

    private static final String TABLE_CREATE_POLL="CREATE TABLE IF NOT EXISTS "
            + "Table_Poll"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "StartDate datetime(20), EndDate datetime(20),"
            + "POLL_ID INTEGER, Question VARCHAR(20),Answer1 VARCHAR(20),Answer1Count INTEGER,"
            + " Answer2 VARCHAR(20),Answer2Count INTEGER , Answer3 VARCHAR(20),Answer3Count INTEGER,"
            +"Answer4 VARCHAR(20),Answer4Count INTEGER,previousSelected INTEGER, resident_id int);";


    private static final String TABLE_CREATE_IMAGE="CREATE TABLE IF NOT EXISTS "
            + "Table_Image"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, Img_ID INTEGER, Image BLOB);";

    private static final String TABLE_CREATE_BILL="CREATE TABLE IF NOT EXISTS "
            + "Table_Bill"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, BillType VARCHAR(20), Balance VARCHAR(20),"
            + " CurrentMonthBalance INTEGER,AmountPaid INTEGER , PaidDate datetime(20),"
            + " Amount VARCHAR(20),Total_Payable VARCHAR(20),DueDate datetime(20), PayID INTEGER, resident_id int);";


    private static final String TABLE_CREATE_COMP_TYPE="CREATE TABLE IF NOT EXISTS "
            + COMP_TYPE
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, key INTEGER, value VARCHAR(20));";


    private static final String TABLE_CREATE_COMP_STATUS="CREATE TABLE IF NOT EXISTS "
            + COMP_STATUS
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, key INTEGER, value VARCHAR(20));";

    private static final String TABLE_CREATE_VENDOR_CATEGORY="CREATE TABLE IF NOT EXISTS "
            + VENDOR_CATEGORY
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, key INTEGER, value VARCHAR(20));";

    private static final String TABLE_CREATE_SOCIETY_USER="CREATE TABLE IF NOT EXISTS "
            + TABLE_SOCIETY_USER
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, res_id INTEGER,"
            + "FlatID INTEGER,FlatNumber VARCHAR(20),RoleType VARCHAR(20), SocietyName VARCHAR(10),"
            + "SocietyId INTEGER,intercomNumber INTEGER);";




    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
          //  Log.i(TABLE_NAME, "Creating DataBase: " + TABLE_CREATE_USER);
            db.execSQL(TABLE_CREATE_FORUM);
            db.execSQL(TABLE_CREATE_USER);
            db.execSQL(TABLE_CREATE_NOTICE);
            db.execSQL(TABLE_CREATE_VENDOR);
            db.execSQL(TABLE_CREATE_COMPLAINT);
            db.execSQL(TABLE_CREATE_POLL);
            db.execSQL(TABLE_CREATE_IMAGE);
            db.execSQL(TABLE_CREATE_BILL);
            db.execSQL(TABLE_CREATE_COMP_TYPE);
            db.execSQL(TABLE_CREATE_COMP_STATUS);
            db.execSQL(TABLE_CREATE_VENDOR_CATEGORY);
            db.execSQL(TABLE_CREATE_SOCIETY_USER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TABLE_NAME, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
           db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
           //  db.execSQL("DROP TABLE IF EXISTS Table_Notice");
            // db.execSQL("DROP TABLE IF EXISTS Table_Vendor");
            db.execSQL("DROP TABLE IF EXISTS Table_Forum");
           //  db.execSQL("DROP TABLE IF EXISTS Table_Complaint");
           //  db.execSQL("DROP TABLE IF EXISTS Table_Poll");
           // db.execSQL("DROP TABLE IF EXISTS Table_Image");
           // db.execSQL("DROP TABLE IF EXISTS Table_Bill");
            onCreate(db);
            //Log.i("TABLE Created", TABLE_NAME);
        }
    }

    public DataAccess(Context _context)
    {
        this.mCtx = _context;
    }
    public DataAccess open() throws SQLException {
        Log.i("DBOpen", "Opening DataBase Connection....");
        mDbHelper = new DatabaseHelper(mCtx);
        myDatabase = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean ClearAll()
    {
        try {
            myDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Notice");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Vendor");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Forum");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Complaint");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Poll");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Image");
            myDatabase.execSQL("DROP TABLE IF EXISTS Table_Bill");
            myDatabase.execSQL("DROP TABLE IF EXISTS "+COMP_TYPE);
            myDatabase.execSQL("DROP TABLE IF EXISTS "+COMP_STATUS);
            myDatabase.execSQL("DROP TABLE IF EXISTS "+VENDOR_CATEGORY);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }


    public long insertNewLogin(String _firstname,String _middlename,String _lastname,String _email,String _mobileNo,String _societyName,int SocietyId,String _userLogin,String _password) {
        Log.i(TABLE_NAME, "Inserting record...");
        myDatabase.execSQL(TABLE_CREATE_USER);
        ContentValues initialValues = new ContentValues();
        initialValues.put("firstname",_firstname);
        initialValues.put("middlename",_middlename);
        initialValues.put("lastname",_lastname);
        initialValues.put("mobile_no", _mobileNo);
        initialValues.put("password", _password);
        initialValues.put("email",_email);
        initialValues.put("userLogin", _userLogin);
        initialValues.put("societyName", _societyName);
        initialValues.put("society_id",SocietyId);
        initialValues.put("status", "Y");
        if(!checkMobileNoExist(_mobileNo)) {
             return myDatabase.insert(TABLE_NAME, null, initialValues);
        }
        else
            return 0;
    }

    public String getCredentials(String Mobile) {

        String Password="";
        try {
            myDatabase.execSQL(TABLE_CREATE_USER);
            String selectQuery = "SELECT  password FROM member_master where mobile_no = '" + Mobile +"'";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Password = ((c.getString(c.getColumnIndex("password"))));

                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return Password;

    }

    //region domain Function




    public long insertCompType(Domain _domain) {
        try {
            Log.i(COMP_TYPE, "Inserting record...");
            ContentValues initialValues = new ContentValues();
            initialValues.put("key", _domain.domain_id);
            initialValues.put("value", _domain.domain_value);
            myDatabase.execSQL(TABLE_CREATE_COMP_TYPE);
            long result = myDatabase.insert(COMP_TYPE, null, initialValues);
            return result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public List<String> getAllComplaintType() {

        List<String> TypeArray = new ArrayList<String>();
        try {
            myDatabase.execSQL(TABLE_CREATE_COMP_TYPE);
            String selectQuery = "SELECT  * FROM " + COMP_TYPE + " order by key asc";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                   String Comp_type = ((c.getString(c.getColumnIndex("value"))));

                    // adding to todo list
                    TypeArray.add(Comp_type);
                    c.moveToNext();       // omprakash
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return TypeArray;

    }


    public int getComplaintTypeKey(String value) {

       int typeKey = 0;
        try {
            myDatabase.execSQL(TABLE_CREATE_COMP_TYPE);
            String selectQuery = "SELECT  key FROM " + COMP_TYPE + " where value = '" + value + "'";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {

                    typeKey = ((c.getInt(c.getColumnIndex("key"))));

                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return typeKey;

    }


    public long insertCompStatus(Domain _domain) {
        try {
            Log.i(COMP_STATUS, "Inserting record...");
            ContentValues initialValues = new ContentValues();
            initialValues.put("key", _domain.domain_id);
            initialValues.put("value", _domain.domain_value);
            myDatabase.execSQL(TABLE_CREATE_COMP_STATUS);
            long result = myDatabase.insert(COMP_STATUS, null, initialValues);
            return result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public List<String> getAllComplaintStatus() {

        List<String> TypeArray = new ArrayList<String>();
        try {
            myDatabase.execSQL(TABLE_CREATE_COMP_STATUS);
            String selectQuery = "SELECT  * FROM " + COMP_STATUS + " order by key asc";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {

                    String Comp_Status = ((c.getString(c.getColumnIndex("value"))));

                    // adding to todo list
                    TypeArray.add(Comp_Status);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return TypeArray;

    }

    public long insertVendorCategory(Domain _domain) {
        try {
            Log.i(VENDOR_CATEGORY, "Inserting record...");
            ContentValues initialValues = new ContentValues();
            initialValues.put("key", _domain.domain_id);
            initialValues.put("value", _domain.domain_value);
            myDatabase.execSQL(TABLE_CREATE_VENDOR_CATEGORY);
            long result = myDatabase.insert(VENDOR_CATEGORY, null, initialValues);
            return result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    public List<String> getAllVendorCategory() {

        List<String> TypeArray = new ArrayList<String>();
        try {
            myDatabase.execSQL(TABLE_CREATE_VENDOR_CATEGORY);
            String selectQuery = "SELECT  * FROM " + VENDOR_CATEGORY + " order by key asc";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {

                    String vendor_category = ((c.getString(c.getColumnIndex("value"))));

                    // adding to todo list
                    TypeArray.add(vendor_category);
                } while (c.moveToNext());
            }
        }
        catch(Exception ex){
            int a=1;
        }

        return TypeArray;

    }


    //endregion


    //region Notice Function


    public long insertNewNotice(Messages _msg, int SocietyId) {
        try {
            Log.i(TABLE_NAME, "Inserting record...");
            ContentValues initialValues = new ContentValues();
            initialValues.put("notice_id", _msg.notice_id);
            initialValues.put("notice", _msg.message);
            initialValues.put("is_file", _msg.isFile);
            initialValues.put("file_name", _msg.filename);
            initialValues.put("sent_by", _msg.sent_by);
            initialValues.put("created_at", _msg.timestamp);
            initialValues.put("created_at", _msg.timestamp);
            initialValues.put("valid_till", _msg.valid_till);
            initialValues.put("society_id", SocietyId);

            myDatabase.execSQL(TABLE_CREATE_NOTICE);
            long result = myDatabase.insert("Table_Notice", null, initialValues);
            return result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public List<Messages> getAllNotice(int SocietyId) {

        List<Messages> noticelist = new ArrayList<Messages>();
        try {
            myDatabase.execSQL(TABLE_CREATE_NOTICE);
            String selectQuery = "SELECT  * FROM Table_Notice where society_id = " +SocietyId+ " order by notice_id desc" ;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Messages nt = new Messages();
                    nt.notice_id = ((c.getInt(c.getColumnIndex("notice_id"))));
                    nt.message = ((c.getString(c.getColumnIndex("notice"))));
                    nt.isFile = ((c.getInt(c.getColumnIndex("is_file"))));
                    nt.filename = ((c.getString(c.getColumnIndex("file_name"))));
                    nt.sent_by = ((c.getInt(c.getColumnIndex("sent_by"))));
                    nt.timestamp = (c.getString(c.getColumnIndex("created_at")));
                    nt.valid_till = (c.getString(c.getColumnIndex("valid_till")));
                    // adding to todo list
                    noticelist.add(nt);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return noticelist;

    }

    public boolean deleteAllNotice() {
        try {
            myDatabase.execSQL("Delete from Table_Notice");
            myDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='Table_Notice';");
        }
        catch(Exception ex){}

        return false;

    }

    public boolean deleteWeekOldNotice() {
        try {
            Calendar c = Calendar.getInstance();
            if (Calendar.DATE>7)
            {
                c.set(Calendar.DAY_OF_MONTH,Calendar.DATE-7);
            }
            else
            {
                int currentDay = Calendar.DATE;
                c.set(Calendar.MONTH,Calendar.MONTH-1);
                int previousDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                int newDay = previousDay + currentDay - 7;
                c.set(Calendar.DAY_OF_MONTH,newDay);
            }
            c.add(Calendar.DATE, -7);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = dateFormat.format(c.DATE);
            return myDatabase.delete("Table_Notice","created_at" + "<'" + date+"'", null) > 0;
        }

        catch (Exception ex)
        {
            return false;
        }

    }


    // endregion

    //region Vendor Functions
    public long insertNewVendor(Vendor vendor, int SocietyId) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("DB_ID", vendor.VendorID);
            initialValues.put("Shop_Category", vendor.Category);
            initialValues.put("Vendor_Name", vendor.vendorName);
            initialValues.put("Address", vendor.VendorAddress);
            initialValues.put("ContactNum", vendor.VendorCont1);
            initialValues.put("imgBMP", vendor.strImage);
            initialValues.put("created_at", Utility.CurrentTime());
            initialValues.put("society_id", SocietyId);
            myDatabase.execSQL(TABLE_CREATE_VENDOR);
            myDatabase.execSQL("DELETE FROM Table_Vendor WHERE DB_ID = "+ vendor.VendorID);
            long value = myDatabase.insert("Table_Vendor", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public  HashMap<Integer,Vendor> getAllVendors( int SocietyId) {

        HashMap<Integer,Vendor> vendorList = new HashMap<>();
      //  List<Vendor> vendorList = new ArrayList<Vendor>();
        try {
            myDatabase.execSQL(TABLE_CREATE_VENDOR);
            String selectQuery = "SELECT  * FROM Table_Vendor where society_id = " + SocietyId;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Vendor vendor = new Vendor();
                    vendor.VendorID = (c.getInt(c.getColumnIndex("DB_ID")));
                    vendor.Category = (c.getString(c.getColumnIndex("Shop_Category")));
                    vendor.vendorName = (c.getString(c.getColumnIndex("Vendor_Name")));
                    vendor.VendorAddress = (c.getString(c.getColumnIndex("Address")));
                    vendor.VendorCont1 = (c.getString(c.getColumnIndex("ContactNum")));
                    vendor.strImage = (c.getString(c.getColumnIndex("imgBMP")));
                    vendor.CreationDate = (c.getString(c.getColumnIndex("created_at")));
                    // adding to todo list
                    vendorList.put(vendor.VendorID, vendor);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){


        }

        return vendorList;

    }


    public boolean deleteAllVendor() {
        try {
            myDatabase.execSQL("Delete from Table_Vendor");
            myDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='Table_Vendor';");
        }
        catch(Exception ex){}

        return false;

    }


    public void  insertVendorImage(int ID, String imgString)
    {
        try {
            String selectQuery =  "UPDATE Table_Vendor SET imgBMP = \"" + imgString + "\" WHERE DB_ID = "+ ID;
            myDatabase.execSQL(selectQuery);
        }
        catch (Exception ex)
        {
int a=5;
            a++;
        }
        return ;
    }

    public long  insertImage(int ID, String imgString)
    {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("Img_ID", ID);
            initialValues.put("Image", imgString);
            myDatabase.execSQL(TABLE_CREATE_IMAGE);
            myDatabase.execSQL("DELETE FROM Table_Image WHERE Img_ID = "+ ID);
            long value = myDatabase.insert("Table_Image", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public String  GetImage(int ID)
    {
        String img="";

        try {
           // myDatabase.execSQL(TABLE_CREATE_IMAGE);
            String selectQuery = "SELECT  Image FROM Table_Image WHERE Img_ID = "+ ID;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    img = (c.getString(c.getColumnIndex("Image")));

                } while (c.moveToNext());
            }
            return img;
        }
        catch (Exception ex)
        {
            return img;
        }
    }

    // endregion

    //region Forum Functions
    public long insertNewForum(Forum forum, int SocietyId) {
        try {
            myDatabase.execSQL(TABLE_CREATE_FORUM);
            myDatabase.delete("Table_Forum", "THREAD_ID" + "=" + forum.thread_ID + "", null);

            ContentValues initialValues = new ContentValues();
            initialValues.put("THREAD_ID", forum.thread_ID);
            initialValues.put("Topic", forum.Topic);
            initialValues.put("FirstUser", forum.FirstUser);
            initialValues.put("FirstFlat", forum.FirstFlat);
            initialValues.put("FirstThread", forum.First_Post);
            initialValues.put("InitiatedAt", forum.First_Date);
            initialValues.put("FirstImage", forum.FirstUserImage);
            initialValues.put("First_User_Id", forum.First_userID);
            initialValues.put("LatestUser",forum.LatestUser);
            initialValues.put("LatestFlat", forum.LatestFlat);
            initialValues.put("LatestThread", forum.Latest_Post);
            initialValues.put("LatestAt", forum.Latest_Date);
            initialValues.put("LastImage", forum.LatestUserImage);
            initialValues.put("Last_User_id", forum.Last_UserID);
            initialValues.put("Comments_Count", forum.Comments_Count);
            initialValues.put("First_Id", forum.FirstID);
            initialValues.put("society_id", SocietyId);

            long value = myDatabase.insert("Table_Forum", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public LinkedHashMap<Integer,Forum> getAllForum(int SocietyId) {
        LinkedHashMap<Integer,Forum> forumList = new LinkedHashMap<>();
     //   List<Forum> forumList = new ArrayList<Forum>();
        try {
            myDatabase.execSQL(TABLE_CREATE_FORUM);
            String selectQuery = "SELECT  * FROM Table_Forum  where society_id = " + SocietyId+" ORDER BY LatestAt DESC";
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Forum forum = new Forum();
                    forum.thread_ID = (c.getInt(c.getColumnIndex("THREAD_ID")));
                    forum.Topic = (c.getString(c.getColumnIndex("Topic")));
                    forum.FirstUser = (c.getString(c.getColumnIndex("FirstUser")));
                    forum.FirstFlat = (c.getString(c.getColumnIndex("FirstFlat")));
                    forum.First_Post = (c.getString(c.getColumnIndex("FirstThread")));
                    forum.First_Date = (c.getString(c.getColumnIndex("InitiatedAt")));
                    forum.FirstUserImage = (c.getString(c.getColumnIndex("FirstImage")));
                    forum.First_userID = (c.getInt(c.getColumnIndex("First_User_Id")));
                    forum.LatestUser = (c.getString(c.getColumnIndex("LatestUser")));
                    forum.LatestFlat = (c.getString(c.getColumnIndex("LatestFlat")));
                    forum.Latest_Post = (c.getString(c.getColumnIndex("LatestThread")));
                    forum.Latest_Date = (c.getString(c.getColumnIndex("LatestAt")));
                    forum.LatestUserImage = (c.getString(c.getColumnIndex("LastImage")));
                    forum.Last_UserID = (c.getInt(c.getColumnIndex("Last_User_id")));
                    forum.Comments_Count = (c.getInt(c.getColumnIndex("Comments_Count")));
                    forum.FirstID = (c.getInt(c.getColumnIndex("First_Id")));
                    // adding to todo list
                    forumList.put(forum.thread_ID,forum);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            Log.i("getAllForum", "reading forum data....");

        }

        return forumList;

    }

    public void LimitForumData()
    {
        try {
            //String selectQuery = " DELETE FROM Table_Forum WHERE id in (SELECT id FROM Table_Forum ORDER BY id Desc Limit -1 OFFSET  (select count(*)-10 from Table_name) )";
            String selectQuery = "delete from Table_Forum where id not in ( select id from Table_Forum order by id desc limit 10 )";
            myDatabase.execSQL(selectQuery);
        }
        catch (Exception ex)
        {
            int a =5;
        }
    }

    // endregion

    //region Complaint Functions
    public long insertNewComplaint(Complaint comp, int ResidentID) {
        try {
            myDatabase.execSQL(TABLE_CREATE_COMPLAINT);
            myDatabase.delete("Table_Complaint", "COMP_ID" + "=" + comp.comp_id + "", null);

            ContentValues initialValues = new ContentValues();
            initialValues.put("First_Id", comp.FirstID);
            initialValues.put("COMP_ID", comp.comp_id);
            initialValues.put("CompType", comp.comp_type);
            initialValues.put("Description", comp.comp_desc);
            initialValues.put("ModifiedAt", comp.comp_date);
            initialValues.put("Severity", comp.comp_severity);
            initialValues.put("CompStatus", comp.LastStatus);
            initialValues.put("LatestComment", comp.LatestComment);
            initialValues.put("AssignedTo", comp.AssignedTo);
            initialValues.put("resident_id", ResidentID);

              long value = myDatabase.insert("Table_Complaint", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public List<Complaint> getAllComplaint(int ResidentID) {

        List<Complaint> forumList = new ArrayList<Complaint>();
        try {
            myDatabase.execSQL(TABLE_CREATE_COMPLAINT);
            String selectQuery = "SELECT  * FROM Table_Complaint ORDER BY ModifiedAt DESC where resident_id = " + ResidentID;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Complaint comp = new Complaint();
                    comp.FirstID = (c.getInt(c.getColumnIndex("First_Id")));
                    comp.comp_id = (c.getInt(c.getColumnIndex("COMP_ID")));
                    comp.comp_type = (c.getString(c.getColumnIndex("CompType")));
                    comp.comp_desc = (c.getString(c.getColumnIndex("Description")));
                    comp.comp_date = (c.getString(c.getColumnIndex("ModifiedAt")));
                    comp.comp_severity = (c.getString(c.getColumnIndex("Severity")));
                    comp.LastStatus = (c.getString(c.getColumnIndex("CompStatus")));
                    comp.LatestComment = (c.getString(c.getColumnIndex("LatestComment")));
                    comp.AssignedTo = (c.getString(c.getColumnIndex("AssignedTo")));
                    // adding to todo list
                    forumList.add(comp);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){


        }

        return forumList;

    }

    public void LimitComplaintData()
    {
        try {
            //String selectQuery = " DELETE FROM Table_Forum WHERE id in (SELECT id FROM Table_Forum ORDER BY id Desc Limit -1 OFFSET  (select count(*)-10 from Table_name) )";
            String selectQuery = "delete from Table_Complaint where id not in ( select id from Table_Complaint order by id desc limit 10 )";
            myDatabase.execSQL(selectQuery);
        }
        catch (Exception ex)
        {
            int a =5;
        }
    }

    public boolean checkComplaintExist(int _compId)
    {
        Cursor mCursor =	myDatabase.query(true, TABLE_COMPLAINT, new String[] {"COMP_ID",
                        "Description"}, "COMP_ID" + "='" + _compId+"'", null,
                null, null, null, null);
        if(mCursor.getCount()>0)
            return true;
        else
            return false;
    }

    public boolean deleteComplaint(int _compId) {
        try {
            myDatabase.execSQL("Delete from Table_Complaint where COMP_ID = " + _compId);

        }
        catch(Exception ex){}

        return false;

    }

    // endregion


    // region Poll Function

    public long insertNewPoll(Polling poll, int ResidentId) {
        try {
            myDatabase.execSQL(TABLE_CREATE_POLL);
            myDatabase.delete("Table_Poll", "POLL_ID" + "=" + poll.PollID + "", null);

            ContentValues initialValues = new ContentValues();
            initialValues.put("POLL_ID", poll.PollID);
            initialValues.put("Question", poll.Question);
            initialValues.put("StartDate", poll.Start_Date);
            initialValues.put("EndDate", poll.End_Date);
            initialValues.put("Answer1", poll.Answer1);
            initialValues.put("Answer1Count", poll.Answer1Count);
            initialValues.put("Answer2", poll.Answer2);
            initialValues.put("Answer2Count", poll.Answer2Count);
            initialValues.put("Answer3", poll.Answer3);
            initialValues.put("Answer3Count", poll.Answer3Count);
            initialValues.put("Answer4", poll.Answer4);
            initialValues.put("Answer4Count", poll.Answer4Count);
            initialValues.put("previousSelected", poll.previousSelected);
            initialValues.put("resident_id", ResidentId);

            long value = myDatabase.insert("Table_Poll", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public void updatePollData(Polling poll) {
        try {
            myDatabase.execSQL(TABLE_CREATE_POLL);

            String selectQuery =  "UPDATE Table_Poll SET Answer1Count = " + poll.Answer1Count
                                   + ", Answer2Count = " + poll.Answer2Count
                                   + ", Answer3Count = " + poll.Answer3Count
                                   + ", Answer4Count = " + poll.Answer4Count
                                   + ", previousSelected = " + poll.previousSelected
                                   + " WHERE POLL_ID = "+ poll.PollID;
            myDatabase.execSQL(selectQuery);

            return ;

        }
        catch (Exception ex)
        {
            return ;
        }
    }

    public List<Polling> getAllPoll(int ResidentId) {

        List<Polling> pollList = new ArrayList<Polling>();
        try {
            myDatabase.execSQL(TABLE_CREATE_POLL);
            String selectQuery = "SELECT  * FROM Table_Poll where resident_id = " + ResidentId;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Polling poll = new Polling();
                    poll.PollID = (c.getInt(c.getColumnIndex("POLL_ID")));
                    poll.Question = (c.getString(c.getColumnIndex("Question")));
                    poll.Start_Date = (c.getString(c.getColumnIndex("StartDate")));
                    poll.End_Date = (c.getString(c.getColumnIndex("EndDate")));
                    poll.Answer1 = (c.getString(c.getColumnIndex("Answer1")));
                    poll.Answer1Count = (c.getInt(c.getColumnIndex("Answer1Count")));
                    poll.Answer2 = (c.getString(c.getColumnIndex("Answer2")));
                    poll.Answer2Count = (c.getInt(c.getColumnIndex("Answer2Count")));
                    poll.Answer3 = (c.getString(c.getColumnIndex("Answer3")));
                    poll.Answer3Count = (c.getInt(c.getColumnIndex("Answer3Count")));
                    poll.Answer4 = (c.getString(c.getColumnIndex("Answer4")));
                    poll.Answer4Count = (c.getInt(c.getColumnIndex("Answer4Count")));
                    poll.previousSelected = (c.getInt(c.getColumnIndex("previousSelected")));
                    // adding to todo list
                    pollList.add(poll);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
        }

        return pollList;

    }

    public boolean deleteAllPolls() {
        try {
            myDatabase.execSQL("Delete from Table_Poll");
            myDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='Table_Poll';");
        }
        catch(Exception ex){}

        return false;

    }


    public void LimitPollData()
    {
        try {
            //String selectQuery = " DELETE FROM Table_Forum WHERE id in (SELECT id FROM Table_Forum ORDER BY id Desc Limit -1 OFFSET  (select count(*)-10 from Table_name) )";
            String selectQuery = "delete from Table_Poll where id not in ( select id from Table_Poll order by id desc limit 10 )";
            myDatabase.execSQL(selectQuery);
        }
        catch (Exception ex)
        {
            int a =5;
        }
    }

    public boolean checkPollExist(int _pollId)
    {
        Cursor mCursor =	myDatabase.query(true, TABLE_POLL, new String[] {"POLL_ID",
                        "Question", "Answer1","Answer2"}, "POLL_ID" + "='" + _pollId+"'", null,
                null, null, null, null);
        if(mCursor.getCount()>0)
            return true;
        else
            return false;
    }

    // endregion


    // region Bill Function

    public long insertNewBill(Bill bill, int ResidentID) {
        try {
            myDatabase.execSQL(TABLE_CREATE_BILL);
            myDatabase.delete("Table_Bill", "PayID" + "=" + bill.PayID + "", null);

            ContentValues initialValues = new ContentValues();
            initialValues.put("PayID", bill.PayID);
            initialValues.put("BillType", bill.BillType);
            initialValues.put("Amount", bill.Amount);
            initialValues.put("Balance", bill.Balance);
            initialValues.put("DueDate", bill.DueDate);
            initialValues.put("Total_Payable", bill.TotalPayable);
            initialValues.put("CurrentMonthBalance", bill.CurrentMonthBalance);
            initialValues.put("AmountPaid", bill.AmountPaid);
            initialValues.put("PaidDate", bill.PaidDate);
            initialValues.put("resident_id", ResidentID);
            long value = myDatabase.insert("Table_Bill", null, initialValues);
            return value;

        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public HashMap<String,Bill> getAllBill(int ResidentID) {

        HashMap<String,Bill> billList = new HashMap<>();
        try {
            myDatabase.execSQL(TABLE_CREATE_BILL);
            String selectQuery = "SELECT  * FROM Table_Bill where resident_id = " + ResidentID;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Bill bill = new Bill();
                    bill.PayID = (c.getInt(c.getColumnIndex("PayID")));
                    bill.BillType = (c.getString(c.getColumnIndex("BillType")));
                    bill.Amount = (c.getInt(c.getColumnIndex("Amount")));
                    bill.Balance = (c.getString(c.getColumnIndex("Balance")));
                    bill.DueDate = (c.getString(c.getColumnIndex("DueDate")));
                    bill.TotalPayable = (c.getString(c.getColumnIndex("Total_Payable")));
                    bill.CurrentMonthBalance = (c.getInt(c.getColumnIndex("CurrentMonthBalance")));
                    bill.AmountPaid = (c.getInt(c.getColumnIndex("AmountPaid")));
                    bill.PaidDate = (c.getString(c.getColumnIndex("PaidDate")));
                    // adding to todo list
                    billList.put(bill.BillType, bill);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
        }

        return billList;

    }

    public boolean deleteAllBill() {
        try {
            myDatabase.execSQL("Delete from Table_Bill");
            myDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='Table_Bill';");
        }
        catch(Exception ex){}

        return false;

    }

    // endregion


    // region Login Function

    public boolean checkMobileNoExist(String _mobileNo)
    {
        Cursor mCursor =	myDatabase.query(true, TABLE_NAME, new String[] {"id",
                        "firstname", "mobile_no","password","status"}, "mobile_no" + "='" + _mobileNo+"'", null,
                null, null, null, null);
        if(mCursor.getCount()>0)
            return true;
        else
            return false;
    }
    public boolean deleteMember(String _mobileNo) {

        return myDatabase.delete("Table_Notice","mobile_no" + "='" + _mobileNo+"'", null) > 0;
    }

    public void deleteTable() {

        try {

            myDatabase.execSQL("DELETE FROM " + TABLE_NAME);
        }
        catch (Exception ex)
        {}

    }

    public Cursor fetchMember(String _mobileNo,String _password) throws SQLException {

        Cursor mCursor =

                myDatabase.query(true, TABLE_NAME, new String[] {"id",
                                "fullname", "mobile_no","password","status"}, "mobile_no" + "='" + _mobileNo+"'", null,
                        null, null, null, null);
        int id = mCursor.getColumnIndex("id");
        int fullName = mCursor.getColumnIndex("fullname");
        int mobileName = mCursor.getColumnIndex("mobile_no");
        int password = mCursor.getColumnIndex("password");
        int status=mCursor.getColumnIndex("status");

        return mCursor;
    }
    public Cursor fetchMember() throws SQLException {

        Cursor mCursor = myDatabase.query(true, TABLE_NAME, new String[]{"id",
                        "firstname","middlename","lastname","mobile_no", "password","email","userLogin","societyName", "status"}, null, null,
                null, null, null, null);
        return mCursor;
    }

    public String getUserName(Cursor _cursor)
    {
        int fullName = _cursor.getColumnIndex("fullname");
        String FirstName="";
        String ShowData="";
        if (_cursor != null) {
            _cursor.moveToFirst();
            do {
                FirstName =_cursor.getString(fullName);
                ShowData =FirstName;
                Log.i("ShowData", ShowData);
            }while(_cursor.moveToNext());
        }
        return FirstName;
    }
    // endregion


    //region Society User Function


    public long insertSocietyUser(SocietyUser socUser) {
        try {
            Log.i(TABLE_SOCIETY_USER, "Inserting record...");
            ContentValues initialValues = new ContentValues();
            initialValues.put("res_id", socUser.ResID);
            initialValues.put("FlatID", socUser.FlatID);
            initialValues.put("FlatNumber", socUser.FlatNumber);
            initialValues.put("RoleType", socUser.RoleType);
            initialValues.put("SocietyName", socUser.SocietyName);
            initialValues.put("SocietyId", socUser.SocietyId);
            initialValues.put("intercomNumber", socUser.intercomNumber);
            myDatabase.execSQL(TABLE_CREATE_SOCIETY_USER);
            long result = myDatabase.insert(TABLE_SOCIETY_USER, null, initialValues);
            return result;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }


    public ArrayList<SocietyUser> getAllSocietyUser() {

        ArrayList<SocietyUser> noticelist = new ArrayList<SocietyUser>();
        try {
            myDatabase.execSQL(TABLE_CREATE_SOCIETY_USER);
            String selectQuery = "SELECT  * FROM "+ TABLE_SOCIETY_USER ;
            Cursor c = myDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    SocietyUser nt = new SocietyUser();
                    nt.ResID = ((c.getInt(c.getColumnIndex("res_id"))));
                    nt.FlatID = ((c.getInt(c.getColumnIndex("FlatID"))));
                    nt.FlatNumber = ((c.getString(c.getColumnIndex("FlatNumber"))));
                    nt.RoleType = ((c.getString(c.getColumnIndex("RoleType"))));
                    nt.SocietyId = ((c.getInt(c.getColumnIndex("SocietyId"))));
                    nt.SocietyName = (c.getString(c.getColumnIndex("SocietyName")));
                    nt.intercomNumber = (c.getString(c.getColumnIndex("intercomNumber")));
                    // adding to todo list
                    noticelist.add(nt);
                } while (c.moveToNext());
            }

        }
        catch(Exception ex){
            int a=1;
        }

        return noticelist;

    }

    public boolean deleteAllSocietyUser() {
        try {
            myDatabase.execSQL("Delete from TABLE_SOCIETY_USER");
            myDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='table_society_user';");
        }
        catch(Exception ex){}

        return false;

    }

    public boolean checkSocietyUserExist(int _resId)
    {

            try {
                Cursor mCursor = myDatabase.query(true, TABLE_SOCIETY_USER, new String[]{"id",
                                "res_id", "FlatID", "FlatNumber", "RoleType"}, "res_id" + "=" + _resId + "", null,
                        null, null, null, null);
                if (mCursor.getCount() > 0)
                    return true;
                else
                    return false;
            }
            catch (Exception ex)
            {
               return true;
            }
    }

    // endregion

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
