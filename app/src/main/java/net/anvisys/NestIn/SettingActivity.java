package net.anvisys.NestIn;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {
    Profile myProfile;
    UserSetting userSetting;
    Button btnUpdate;
    CheckBox chkCompNotification,chkForumNotification,chkBillNotification,chkNotification,chkCompMail,chkForumMail,chkBillMail,chkNoticeMail,
            chkCompSMS,chkForumSMS,chkBillSMS,chkNotificationSMS;
    Boolean newCompSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Setting");
        actionBar.show();

        myProfile = Session.GetUser(this);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });

        LoadSetting();

    }

    private void LoadSetting(){

        String url = ApplicationConstants.APP_SERVER_URL +"/api/user/Setting/" + myProfile.UserID;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObj) {
                try {

                    userSetting = new UserSetting();
                    userSetting.userID = jObj.getInt("UserID");

                    userSetting.FirstName = jObj.getString("FirstName");
                    userSetting.MobileNo = jObj.getString("MobileNo");
                    userSetting.EmailId=jObj.optString("EmailId");
                    userSetting.BillingNotification = jObj.optBoolean("BillingNotification");
                    userSetting.BillingMail = jObj.optBoolean("BillingMail");
                    userSetting.BillingSMS = jObj.optBoolean("BillingSMS");
                    userSetting.ComplaintNotification = jObj.optBoolean("ComplaintNotification");
                    userSetting.ComplaintMail = jObj.optBoolean("ComplaintMail");
                    userSetting.ComplaintSMS = jObj.optBoolean("ComplaintSMS");
                    userSetting.forumNotification = jObj.optBoolean("forumNotification");
                    userSetting.forumMail = jObj.optBoolean("forumMail");
                    userSetting.forumSMS = jObj.optBoolean("forumSMS");
                    userSetting.NoticeNotification = jObj.optBoolean("NoticeNotification");
                    userSetting.NoticeMail = jObj.optBoolean("NoticeMail");
                    userSetting.NoticeSMS = jObj.optBoolean("NoticeSMS");


                } catch (JSONException js) {
                      int a=1;
                }
            }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //listPrgBar.setVisibility(View.GONE);
                }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        }

        public void Update(){
        try {



        }catch (Exception e){

        }

        }



        public void EditSetting(){

            chkCompNotification= findViewById(R.id.chkCompNotification);
            chkForumNotification= findViewById(R.id.chkForumNotification);
            chkBillNotification= findViewById(R.id.chkBillNotification);
            chkNotification= findViewById(R.id.chkNotification);
            chkCompMail= findViewById(R.id.chkCompMail);
            chkForumMail= findViewById(R.id.chkForumMail);
            chkBillMail= findViewById(R.id.chkBillMail);
            chkNoticeMail= findViewById(R.id.chkNoticeMail);
            chkCompSMS= findViewById(R.id.chkCompSMS);
            chkForumSMS= findViewById(R.id.chkForumSMS);
            chkBillSMS= findViewById(R.id.chkBillSMS);
            chkNotificationSMS= findViewById(R.id.chkNotificationSMS);

            chkCompNotification.setChecked(userSetting.ComplaintNotification);
            chkForumNotification.setChecked(userSetting.forumNotification);
            chkBillNotification.setChecked(userSetting.BillingNotification);
            chkNotification.setChecked(userSetting.NoticeNotification);
            chkCompMail.setChecked(userSetting.ComplaintMail);
            chkForumMail.setChecked(userSetting.forumMail);
            chkBillMail.setChecked(userSetting.BillingMail);
            chkNoticeMail.setChecked(userSetting.NoticeMail);
            chkCompSMS.setChecked(userSetting.ComplaintSMS);
            chkForumSMS.setChecked(userSetting.forumSMS);
            chkBillSMS.setChecked(userSetting.BillingSMS);
            chkNotificationSMS.setChecked(userSetting.NoticeSMS);

        }




    private void UpdateSetting(){

        String url = ApplicationConstants.APP_SERVER_URL +"/api/user/Setting";
        String reqBody = "{\"UserId\":" + myProfile.UserID + ",\"BillingNotification\":" + userSetting.BillingNotification + ",\"BillingMail\":" + userSetting.BillingMail + ",\"BillingSMS\":" + userSetting.BillingSMS + ",\"ComplaintNotification\":" +
                userSetting.ComplaintNotification + ",\"ComplaintMail\":" + userSetting.ComplaintMail + ",\"ComplaintSMS\":" + userSetting.ComplaintSMS + ",\"forumNotification\":" + userSetting.forumNotification +
                ",\"forumMail\":" + userSetting.forumMail +", \"forumSMS\":" + userSetting.forumSMS +", \"NoticeNotification\":" + userSetting.NoticeNotification +", \"NoticeMail\":" + userSetting.NoticeMail +",\"NoticeMail\":" + userSetting.NoticeMail +"}";

        JSONObject jsRequest = null;

        try {
            jsRequest = new JSONObject(reqBody);
        } catch (JSONException jex) {

        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObj) {
                try {

                    userSetting = new UserSetting();
                    userSetting.userID = jObj.getInt("UserID");

                    userSetting.FirstName = jObj.getString("FirstName");
                    userSetting.MobileNo = jObj.getString("MobileNo");
                    userSetting.EmailId=jObj.optString("EmailId");
                    userSetting.BillingNotification = jObj.optBoolean("BillingNotification");
                    userSetting.BillingMail = jObj.optBoolean("BillingMail");
                    userSetting.BillingSMS = jObj.optBoolean("BillingSMS");
                    userSetting.ComplaintNotification = jObj.optBoolean("ComplaintNotification");
                    userSetting.ComplaintMail = jObj.optBoolean("ComplaintMail");
                    userSetting.ComplaintSMS = jObj.optBoolean("ComplaintSMS");
                    userSetting.forumNotification = jObj.optBoolean("forumNotification");
                    userSetting.forumMail = jObj.optBoolean("forumMail");
                    userSetting.forumSMS = jObj.optBoolean("forumSMS");
                    userSetting.NoticeNotification = jObj.optBoolean("NoticeNotification");
                    userSetting.NoticeMail = jObj.optBoolean("NoticeMail");
                    userSetting.NoticeSMS = jObj.optBoolean("NoticeSMS");


                } catch (JSONException js) {
                    int a=1;
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //listPrgBar.setVisibility(View.GONE);
            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

    }

    class UserSetting
    {
        Boolean BillingNotification,BillingMail,BillingSMS,ComplaintNotification,ComplaintMail,ComplaintSMS,forumNotification,forumMail,forumSMS,NoticeNotification,NoticeMail,NoticeSMS;
        Integer RegID,userID;
        String FirstName,MobileNo,EmailId;
    }

}
