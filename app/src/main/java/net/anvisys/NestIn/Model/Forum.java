package net.anvisys.NestIn.Model;

import java.io.Serializable;

/**
 * Created by Amit Bansal on 26-10-2016.
 */
public class Forum implements Serializable {

   public String Topic,FirstUser,FirstFlat,First_Post,Latest_Post,First_Date,Latest_Date, FirstUserImage,LatestUserImage, LatestUser, LatestFlat;
   public int thread_ID, First_Res_Id, Last_Res_Id, Comments_Count, FirstID,First_userID,Last_UserID;
}
