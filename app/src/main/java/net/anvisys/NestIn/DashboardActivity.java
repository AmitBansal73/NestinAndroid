package net.anvisys.NestIn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import net.anvisys.NestIn.Billing.BillingActivity;
import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Complaints.ViewComplaintsActivity;
import net.anvisys.NestIn.Forum.ForumActivity;
import net.anvisys.NestIn.Guest.GuestActivity;
import net.anvisys.NestIn.Notice.NoticeActivity;
import net.anvisys.NestIn.Poll.OpinionActivity;
import net.anvisys.NestIn.Register.LoginActivity;
import net.anvisys.NestIn.Register.RentActivity;
import net.anvisys.NestIn.Vendor.ShopActivity;
import org.json.JSONObject;

import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity implements
        MyProfile.OnFragmentInteractionListener,
        net.anvisys.NestIn.Summary.SummaryListener,
        MyGCMListnerService.GCMListener,
        ProfileActivity.ProfileEditListener
{

    String previousActivity, strUsrType,  regID,strResponse;
    int intUserID;
    private ImageView iconComplaint, iconNotice, iconShoping,iconForum, iconPoll, iconBill;
    ImageView imageProfile;
    private TextView txtUserInfo,txtUserType,txtUserAddress;
    int deleteRegIDCounter=1;
    private Integer ClickCount=0;
    private long prevTime = 0;
    private AdView mAdView;
    Profile myProfile;
    SocietyUser socUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Dashboard");
        actionBar.show();

   /*   mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/
        txtUserInfo = findViewById(R.id.txtUserInfo);
        txtUserType = findViewById(R.id.txtUserType);
        txtUserAddress = findViewById(R.id.txtUserAddress);

        SetFlippers();
        //InitializeVariableFromIntent();
        Intent mIntent = getIntent();
        String parent = mIntent.getStringExtra("PARENT");


        myProfile = Session.GetUser(this);

        intUserID = Integer.parseInt(myProfile.UserID);



        imageProfile = findViewById(R.id.imageProfile);

        String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(imageProfile);

       /* Bitmap bmp = ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, this);
        if (bmp == null) {
            ImageServer.SaveStringAsBitmap(myProfile.strImage, myProfile.MOB_NUMBER, this);
            bmp = ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, this);
        }
        imageProfile.setImageBitmap(bmp);  */

        socUser = Session.GetCurrentSocietyUser(this);
        txtUserInfo.setText(myProfile.NAME+", "+ socUser.RoleType);
        txtUserType.setText(socUser.FlatNumber+", "+socUser.SocietyName);

        //txtUserAddress.setText();

        if (( socUser.ResID < 1) ||(socUser.SocietyName== null || socUser.SocietyName.matches("")))
        {
           Intent loginIntent = new Intent(this, LoginActivity.class);
           startActivity(loginIntent);
           DashboardActivity.this.finish();
        }

        else if(parent!= null && (parent.matches("FORUM")||parent.matches("COMPLAINTS")||parent.matches("NOTICE")||parent.matches("VENDOR")||parent.matches("POLL")||parent.matches("BILLING")))
        {
            MyGCMListnerService.setGCMNotificationListener(this);
            setCounter();
        }
        else {
        ApplicationVariable.APP_RUNNING = true;


           if(Utility.IsConnected(this))
           {
/*
               Summary.RegisterSummaryListener(this);
               net.anvisys.SCM_WebAPI.Summary getSummary = new net.anvisys.SCM_WebAPI.Summary();
               getSummary.LoadUpdateCount(this);*/
               MyGCMListnerService.setGCMNotificationListener(this);
           }
            else
           {
               Toast.makeText(this, "Not Connected, Working offline", Toast.LENGTH_LONG).show();
           }

       }
 }


    private void FailedMessage()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Login Failed");
        builder.setMessage("Try Again or Go To Login");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog Alert = builder.create();
        Alert.show();
    }

    @Override
    public void OnChangeCurrentUser() {
        socUser = Session.GetCurrentSocietyUser(this);
        txtUserInfo.setText(myProfile.NAME+", "+ socUser.RoleType);
        txtUserType.setText(socUser.FlatNumber+", "+socUser.SocietyName);


    }

    @Override
    public void OnSummaryReceived() {
        setCounter();
    }

    private void setCounter()
        {
           /* if (ApplicationConstants.FORUM_UPDATES !=0)
            {
                forumCount.setVisibility(View.VISIBLE);
                forumCount.setText(Integer.toString(ApplicationConstants.FORUM_UPDATES) );
            }
            else
            {
                forumCount.setVisibility(View.INVISIBLE);
            }


            if (ApplicationConstants.COMPLAINT_UPDATES!=0)
            {
                complaintCount.setVisibility(View.VISIBLE);
                complaintCount.setText(Integer.toString(ApplicationConstants.COMPLAINT_UPDATES));
            }
            else
            {
                complaintCount.setVisibility(View.INVISIBLE);
            }


            if (ApplicationConstants.VENDOR_UPDATES!=0)
            {
                vendorCount.setVisibility(View.VISIBLE);
                vendorCount.setText(Integer.toString(ApplicationConstants.VENDOR_UPDATES));
            }
            else
            {
                vendorCount.setVisibility(View.INVISIBLE);
            }

            if (ApplicationConstants.BILL_UPDATES!=0)
            {
                BillCount.setVisibility(View.VISIBLE);
                BillCount.setText(Integer.toString(ApplicationConstants.BILL_UPDATES));
            }
            else
            {
                BillCount.setVisibility(View.INVISIBLE);
            }

            if (ApplicationConstants.POLL_UPDATES!=0)
            {
                PollCount.setVisibility(View.VISIBLE);
                PollCount.setText(Integer.toString(ApplicationConstants.POLL_UPDATES));
            }
            else
            {
                PollCount.setVisibility(View.INVISIBLE);
            }

            if (Session.GetNoticeCount(getApplicationContext())!=0)
            {
                noticeCount.setVisibility(View.VISIBLE);
                noticeCount.setText(Integer.toString(Session.GetNoticeCount(getApplicationContext())));
            }
            else
            {
                noticeCount.setVisibility(View.INVISIBLE);
            }
            */
        }


    class  clicker implements View.OnClickListener
    {
        public void onClick(View v) {
            switch ( v.getId()) {

                case R.id.iconComplaint :
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                        Intent viewComplaintsActivity = new Intent(DashboardActivity.this,ViewComplaintsActivity.class);
                        startActivity(viewComplaintsActivity);
                        }
                    break;
                }
                case R.id.iconForum:
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                         Intent viewForumActivity = new Intent(DashboardActivity.this,ForumActivity.class);
                         startActivity(viewForumActivity);

                    }
                    break;
                }
                case R.id.iconShoping:
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                        //   Log.i("Shop Activity User", strFirstName);
                        Intent viewForumActivity = new Intent(DashboardActivity.this,ShopActivity.class);
                        startActivity(viewForumActivity);

                    }
                    break;
                }
                case R.id.iconNotice:
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                        //   Log.i("Forum Activity User", strFirstName);
                        Intent viewNoticeActivity = new Intent(DashboardActivity.this,NoticeActivity.class);
                        startActivity(viewNoticeActivity);
                        //DashboardActivity.this.finish();
                    }
                    break;
                }
                case R.id.iconPoll:
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                        //   Log.i("Voting Activity User", strFirstName);
                        Intent pollingActivity = new Intent(DashboardActivity.this,OpinionActivity.class);
                        startActivity(pollingActivity);
                    }
                    break;
                }

                case R.id.iconBill:
                {
                    if (socUser.ResID <1) {
                        FailedMessage();
                    } else {
                        //  Log.i("BillingActivity User", strFirstName);
                        Intent billingActivity = new Intent(DashboardActivity.this,BillingActivity.class);
                        startActivity(billingActivity);

                    }
                    break;
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent profileIntent = new Intent(DashboardActivity.this, SettingActivity.class);
            startActivity(profileIntent);
        }

        if (id == R.id.action_profile) {

            Intent profileIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
            ProfileActivity.RegisterChatListener(this);
        }

        if (id == R.id.action_Guest) {
            Intent loginActivity = new Intent(DashboardActivity.this,GuestActivity.class);
            startActivity(loginActivity);
            return true;
        }
        if (id == R.id.action_Rent) {
            Intent rentActivity = new Intent(DashboardActivity.this, RentActivity.class);
            startActivity(rentActivity);
            return true;
        }
        if (id == R.id.action_LogOff) {

            AlertDialog.Builder builder= new AlertDialog.Builder(
                    DashboardActivity.this);
            builder.setTitle("Log Off");
            builder.setMessage("Are you sure");
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.cancel();
                            Log.e("info", "NO");
                        }

                    });

            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            DataAccess mDataAccess = new DataAccess(getApplicationContext());
                            mDataAccess.open();
                            mDataAccess.ClearAll();
                            mDataAccess.close();
                            Session.LogOff(getApplicationContext());
                            DeleteRegIdFromServer();
                        }
                    });


            AlertDialog Alert = builder.create();

            Alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {
        Toast.makeText(getApplicationContext(),"Back from Profile",Toast.LENGTH_LONG).show();
    }


    private boolean DeleteRegIdFromServer() {
        String url = ApplicationConstants.APP_SERVER_URL +"/api/gcmregister/5";
        String reqBody = "\""+ myProfile.MOB_NUMBER + "\"}";

        try{
          //  JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,reqBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    strResponse = response.toString();
                    DashboardActivity.this.finish();
                   // System.exit(0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    if(message.equals("com.android.volley.TimeoutError")&&deleteRegIDCounter<3)
                    {
                        deleteRegIDCounter++;
                        DeleteRegIdFromServer();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Could not un-register , may still get notifications",Toast.LENGTH_LONG).show();
                        DashboardActivity.this.finish();
                        System.exit(0);
                    }

                }
            });
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (Exception jex)
        {
            Toast.makeText(getApplicationContext(),jex.getMessage(),Toast.LENGTH_LONG).show();
        }

        if (strResponse == null || strResponse.isEmpty())
        {
            return false;
        }
        return true;
    }

    private void SetFlippers(){
        iconComplaint =  findViewById(R.id.iconComplaint);
        iconComplaint.setOnClickListener(new clicker());

        iconNotice =  findViewById(R.id.iconNotice);
        iconNotice.setOnClickListener(new clicker());

        iconShoping =  findViewById(R.id.iconShoping);
        iconShoping.setOnClickListener(new clicker());

        iconForum =  findViewById(R.id.iconForum);
        iconForum.setOnClickListener(new clicker());

        iconPoll =  findViewById(R.id.iconPoll);
        iconPoll.setOnClickListener(new clicker());

        iconBill =  findViewById(R.id.iconBill);
        iconBill.setOnClickListener(new clicker());


    }


    @Override
    protected void onDestroy() {
        ApplicationVariable.APP_RUNNING = false;
        MyGCMListnerService.removeGCMNotificationListener();
        super.onDestroy();
    }



    @Override
    public void OnMessageReceived(String Message) {
        try {

            ApplicationConstants.TOTAL_NOTICE_COUNT = Session.GetNoticeCount(getApplicationContext()) + 1;
           // noticeCount.setText(Integer.toString(ApplicationConstants.TOTAL_NOTICE_COUNT));
          //  Session.SetNoticeCount(getApplicationContext(), ApplicationConstants.TOTAL_NOTICE_COUNT);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onBackPressed() {
        try {
            long time = SystemClock.currentThreadTimeMillis();

            if (prevTime == 0) {
                prevTime = SystemClock.currentThreadTimeMillis();
            }

            if (time - prevTime > 1000) {
                prevTime = time;
                ClickCount=0;
            }
            if (time - prevTime < 1000 && time > prevTime) {
                ClickCount++;
                if (ClickCount == 2) {

                    DashboardActivity.this.finish();
                } else {
                    prevTime = time;
                    String msg = "double click to close";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



}
