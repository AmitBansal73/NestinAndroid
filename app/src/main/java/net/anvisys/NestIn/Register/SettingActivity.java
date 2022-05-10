package net.anvisys.NestIn.Register;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {
    Profile myProfile;
    UserSetting userSetting;
    Button btnUpdate;
    CheckBox chkCompNotification,chkForumNotification,chkBillNotification,chkNotification,chkCompMail,chkForumMail,chkBillMail,chkNoticeMail,
            chkCompSMS,chkForumSMS,chkBillSMS,chkNotificationSMS;
    Boolean newComplaintNotification,newforumNotification,newBillingNotification,newNoticeNotification,newComplaintMail,newforumMail,
            newBillingMail, newNoticeMail,newComplaintSMS,newforumSMS,newBillingSMS,newNoticeSMS;
    ProgressBar progressBar;

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
        progressBar= findViewById(R.id.progressBar);
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

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });

        LoadSetting();

    }

    private void LoadSetting(){
        progressBar.setVisibility(View.VISIBLE);

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
                    userSetting.BillingNotification = jObj.getBoolean("BillingNotification");
                    userSetting.BillingMail = jObj.getBoolean("BillingMail");
                    userSetting.BillingSMS = jObj.getBoolean("BillingSMS");
                    userSetting.ComplaintNotification = jObj.getBoolean("ComplaintNotification");
                    userSetting.ComplaintMail = jObj.getBoolean("ComplaintMail");
                    userSetting.ComplaintSMS = jObj.getBoolean("ComplaintSMS");
                    userSetting.forumNotification = jObj.getBoolean("forumNotification");
                    userSetting.forumMail = jObj.getBoolean("forumMail");
                    userSetting.forumSMS = jObj.getBoolean("forumSMS");
                    userSetting.NoticeNotification = jObj.getBoolean("NoticeNotification");
                    userSetting.NoticeMail = jObj.getBoolean("NoticeMail");
                    userSetting.NoticeSMS = jObj.getBoolean("NoticeSMS");

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


                } catch (JSONException js) {
                      int a=1;
                }
                progressBar.setVisibility(View.GONE);
            }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        }

        public void Update(){
        try {
            if (chkCompNotification.isChecked()) {
                newComplaintNotification = true;
            }else {newComplaintNotification = false;}

            if (chkBillNotification.isChecked()) {
                newBillingNotification = true;
            }else {newBillingNotification = false;}

            if (chkNotification.isChecked()) {
                newNoticeNotification = true;
            }else {newNoticeNotification = false;}

            if (chkForumNotification.isChecked()) {
                newforumNotification = true;
            }else {newforumNotification = false;}

            if (chkCompMail.isChecked()) {
                newComplaintMail = true;
            }else {newComplaintMail = false;}

            if (chkForumMail.isChecked()) {
                newforumMail = true;
            }else {newforumMail = false;}

            if (chkBillMail.isChecked()) {
                newBillingMail = true;
            }else {newBillingMail = false;}

            if (chkNoticeMail.isChecked()) {
                newNoticeMail = true;
            }else {newNoticeMail = false;}

            if (chkCompSMS.isChecked()) {
                newComplaintSMS = true;
            }else {newComplaintSMS = false;}

            if (chkForumSMS.isChecked()) {
                newforumSMS = true;
            }else {newforumSMS = false;}

            if (chkBillSMS.isChecked()) {
                newBillingSMS = true;
            }else {newBillingSMS = false;}

            if (chkNotificationSMS.isChecked()) {
                newNoticeSMS = true;
            }else {newNoticeSMS = false;}

            UpdateSetting();


        }catch (Exception e){
            int a=2;

        }

        }


    private void UpdateSetting(){
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +"/api/user/Setting";
        String reqBody = "{\"UserId\":" + myProfile.UserID + ",\"BillingNotification\":" + newBillingNotification + ",\"BillingMail\":" + newBillingMail + ",\"BillingSMS\":" + newBillingSMS + ",\"ComplaintNotification\":" +
                newComplaintNotification + ",\"ComplaintMail\":" + newComplaintMail + ",\"ComplaintSMS\":" + newComplaintSMS + ",\"forumNotification\":" + newforumNotification +
                ",\"forumMail\":" + newforumMail +", \"forumSMS\":" + newforumSMS +", \"NoticeNotification\":" +newNoticeNotification +", \"NoticeMail\":" + newNoticeMail +",\"NoticeSMS\":" + newNoticeSMS +"}";

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
                    userSetting.BillingNotification = newBillingNotification;
                    userSetting.BillingMail = newBillingMail;
                    userSetting.BillingSMS = newBillingSMS;
                    userSetting.ComplaintNotification = newComplaintNotification;
                    userSetting.ComplaintMail = newComplaintMail;
                    userSetting.ComplaintSMS = newComplaintSMS;
                    userSetting.forumNotification = newforumNotification;
                    userSetting.forumMail = newforumMail;
                    userSetting.forumSMS = newforumSMS;
                    userSetting.NoticeNotification = newNoticeNotification;
                    userSetting.NoticeMail = newNoticeMail;
                    userSetting.NoticeSMS = newNoticeSMS;

                    Toast.makeText(getApplicationContext(), "Updated Successfully.", Toast.LENGTH_SHORT).show();
                   // Intent mainIntent = new Intent(SettingActivity.this,DashboardActivity.class);
                   // startActivity(mainIntent);
                   // SettingActivity.this.finish();

                } catch (Exception e) {
                    int a=1;
                }
                progressBar.setVisibility(View.GONE);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

    }

    class UserSetting
    {
        Boolean BillingNotification,BillingMail,BillingSMS,ComplaintNotification,ComplaintMail,ComplaintSMS,forumNotification,forumMail,forumSMS,NoticeNotification,NoticeMail,NoticeSMS;
        Integer userID;
        String FirstName,MobileNo,EmailId;
    }
}
