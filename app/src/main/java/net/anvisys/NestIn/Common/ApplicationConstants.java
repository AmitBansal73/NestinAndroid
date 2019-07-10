package net.anvisys.NestIn.Common;

import net.anvisys.NestIn.Model.Polling;

import java.util.List;

/**
 * Created by Amit Bansal on 14-01-2016.
 */
public class ApplicationConstants {

   public static final String user_API_URL = "http://www.kevintech.in/Nestin-UserService/api/User";  //api/User
    public static final String APP_SERVER_URL = "http://www.kevintech.in/Nestin-WebApi";

        public static final String APPlICATION_URL = "http://www.nestin.online/";
        public static List<Polling> pollList;

   // static final String APP_SERVER_DELETE_URL = "http://www.myinteriorchoice.in/asp/Index.aspx?shareRegId=DELETE";

    // Google Project Number
    public static final String GOOGLE_PROJ_ID = "288809313667";
    // Message Key
    public static final String MSG_KEY = "msg";
   public static int REQUEST_ADD_HOUSE=100;
   public static int REQUEST_ADD_OWNER=200;
    public static int REQUEST_EDIT_COMPLAINT=300;
    public static int REQUEST_ADD_COMPLAINT=300;
    public static int REQUEST_ADD_Visitor=400;
    public static int PICK_CONTACT = 500;
    public static int REQUEST_ADD_RENT = 600;
    public static int REQUEST_ADD_CARPOOL = 700;
    public static int REQUEST_FORUM_COMMENT = 800;

    public static int FORUM_UPDATES=0;
    public static int TOTAL_FORUM_COUNT=0;
    public static int COMPLAINT_UPDATES=0;
    public static int TOTAL_COMPLAINT_COUNT=0;
    public static int VENDOR_UPDATES=0;
    public static int TOTAL_VENDOR_COUNT=0;
    public static int POLL_UPDATES=0;
    public static int TOTAL_POLL_COUNT=0;
    public static int BILL_UPDATES=0;
    public static int TOTAL_BILL_COUNT=0;
    public static int NOTICE_UPDATES=0;
    public static int TOTAL_NOTICE_COUNT=0;

}
