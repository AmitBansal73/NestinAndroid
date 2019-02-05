package net.anvisys.NestIn.Common;

import java.util.HashMap;

/**
 * Created by Amit Bansal on 2017-04-25.
 */
public class ApplicationVariable {

    public static int Res_Id = 0;
    public static Boolean APP_RUNNING = false;
    public static Boolean AUTHENTICATED = true;

    public static HashMap<String,String> complaintTypeDomain = new HashMap<>(6);
    public static  HashMap<String,String> complaintSeverityDomain;
    public static HashMap<String,String> complaintStatusDomain;

    public static enum CompliantStatus
    {
        Initiated(1),

        Assigned(2),

        Ongoing(3),

        Resolved(4),

        ReOpen(5),

        Reminder(6);
        public int value;
        private CompliantStatus(int value) {
            this.value = value;
        }
    }

    public static enum CompliantType
    {
        Plumber(1),

        Electrician(2),
        Mason(3),

        Carpenter(4),

        Parking(5),

        General(6);

        public int value;
        private CompliantType(int value) {
            this.value = value;
        }
    }
}
