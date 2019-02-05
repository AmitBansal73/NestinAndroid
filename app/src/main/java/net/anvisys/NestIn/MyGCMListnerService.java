package net.anvisys.NestIn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.Base64;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmListenerService;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Notice.NoticeActivity;
import net.anvisys.NestIn.Object.Messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amit Bansal on 15-01-2016.
 */
public class MyGCMListnerService extends GcmListenerService {
    final int NOTIFICATION_ID = 9000;
    private int numMessages = 0;
    Handler mHandler;
    public MyGCMListnerService() {
    }

    static GCMListener gcmListener;
    public interface GCMListener
    {
        void OnMessageReceived(String Message);

    }

    // Assign the listener implementing events interface that will receive the events
    public static void setGCMNotificationListener(GCMListener listener) {
        gcmListener = listener;
    }


    // Assign the listener implementing events interface that will receive the events
    public static void removeGCMNotificationListener() {
        gcmListener = null ;
    }


    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        String message = data.getString("msg");

        if (from.startsWith("/topics/")) {
            saveNotification(message);
            sendNotification(message);
            // message received from some topic.
        }
        else {
            // normal downstream message.
            saveNotification(message);
            if(ApplicationVariable.APP_RUNNING==false)
            {
                sendNotification(message);
            }
            else
            {
                if(gcmListener==null)
                {
                    ApplicationConstants.TOTAL_NOTICE_COUNT = Session.GetNoticeCount(getApplicationContext()) +1;
                    Session.SetNoticeCount(getApplicationContext(),ApplicationConstants.TOTAL_NOTICE_COUNT);
                }
                else
                {
                    SendMessage("New Notice");
                }
            }
        }
    }

private boolean saveNotification( String message){
    DataAccess _databaseAccess;
    try {
    Messages msg = new Messages();
    String[] msgArr = message.split("&");
    msg.notice_id = Integer.parseInt(msgArr[0]);
    msg.message = msgArr[1];
    msg.timestamp =msgArr[2];
    msg.isFile=Integer.parseInt(msgArr[3]);
    msg.filename = msgArr[4];
    msg.sent_by=Integer.parseInt(msgArr[5]);
    msg.valid_till=msgArr[6];
    int SocID = Integer.parseInt(msgArr[7]);

        _databaseAccess = new DataAccess(getApplicationContext());
        _databaseAccess.open();
        if (message != null) {
            if (!message.isEmpty()) {
                _databaseAccess.insertNewNotice(msg, SocID);
            }
        }
        _databaseAccess.close();
        if(msg.isFile==1)
        {
            getNoticeFromServer(msg.notice_id, msg.filename);
        }
        return true;
    }
    catch (Exception ex)
    {
        return false;
    }
}

    private void sendNotification(String message) {
        try {

            String[] msgArr = message.split("&");
            int notice_id = Integer.parseInt(msgArr[0]);
            String txt = "New Notification:" +  msgArr[1];


            Intent noticeIntent = new Intent(this, NoticeActivity.class);
            noticeIntent.putExtra("msg", txt);
            noticeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(NoticeActivity.class);
            stackBuilder.addNextIntent(noticeIntent);
            ++numMessages;
            PendingIntent noticePendingIntent = stackBuilder.getPendingIntent(numMessages, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder notificationBuilder = (Notification.Builder) new Notification.Builder(this)
                    .setSmallIcon(R.drawable.iconhome_n)
                    .setContentTitle("SPM")
                    .setContentText(txt)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(noticePendingIntent);
            // Set Vibrate, Sound and Light
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;
            notificationBuilder.setNumber(++numMessages);
            notificationBuilder.setDefaults(defaults);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());

            int count = Session.GetNoticeCount(getApplicationContext());
            Session.SetNoticeCount(getApplicationContext(),count+1);
        }
        catch (Exception ex)
        {}
    }



    public void getNoticeFromServer( final int notice_id, final String file_name)
    {

        String url =  ApplicationConstants.APP_SERVER_URL +  "/api/Notifications";
        String reqBody = "{\"Notice_ID\":\""+ notice_id + "\",\"FileName\":\""+ file_name +  "\"}";
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,reqBody , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObj) {

                try{
                        String ImageByte =jObj.getString("imageByte");
                        String fileName = notice_id +"__" +file_name;
                        byte[] imgByte = Base64.decode(ImageByte, Base64.DEFAULT);
                        ImageServer.SaveFileToExternal(imgByte, fileName, getApplicationContext());
                                  }
                catch (JSONException e)
                {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();

            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************
    }

   private void SendMessage(final String txt)
   {
       try {
           if(gcmListener!=null) {

               mHandler = new Handler(Looper.getMainLooper()) {
                   @Override
                   public  void handleMessage(android.os.Message message) {
                       // This is where you do your work in the UI thread.
                       // Your worker tells you in the message what to do.
                       try {
                           String str = (String) message.obj;
                           //  FilterMessage(str);
                           gcmListener.OnMessageReceived(str);
                       }
                       catch (Exception ex)
                       {
                           int a=1;
                       }
                   }
               };

               new Thread()
               {
                   @Override
                   public void run()
                   {
                       Looper.prepare();

                       //gcmListener.OnMessageReceived(message);
                       android.os.Message msg = mHandler.obtainMessage(1, txt);
                       msg.sendToTarget();

                       Looper.loop();
                   }
               }.start();
           }


       }
       catch (Exception ex)
       {
           Toast.makeText(getApplicationContext(), "Error in handling message thread", Toast.LENGTH_SHORT).show();
       }
   }

}
