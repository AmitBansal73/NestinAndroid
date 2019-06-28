package net.anvisys.NestIn.Poll;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Model.Polling;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OpinionActivity extends AppCompatActivity implements OpinionActivityFragment.PollUpdateListener, Summary.SummaryListener {

    String strUserID ,strResID,strFirstName, strLastName ,strFlatNumber, strUsrType , MobileNo,  strSocietyName;
   public static List<Polling> pollList;
    ViewPager viewPager;
  //  static final int NUM_ITEMS = 5;
    FragmentStatePagerAdapter fragmentAdapter;
    ProgressBar prgBar;
    public static int PageNumber =1;
    String LastPollRefreshTime="";
    Polling dummy;
    int BatchCount=5;
    int EndIndex =5;
    int StartIndex =0;
    int TotalCount =0;
    int GetCount=10;
    int Count =10;
    Snackbar snackbar;
    SocietyUser socUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Opinion Poll");
        actionBar.show();
try {



    prgBar = (ProgressBar) findViewById(R.id.PrgBar);
    viewPager = (ViewPager) findViewById(R.id.viewPager);

    prgBar.setVisibility(View.GONE);
    pollList = new ArrayList<>();
    socUser = Session.GetCurrentSocietyUser(this);
     strSocietyName = socUser.SocietyName;

    OpinionActivityFragment.RegisterPollChangeListener(OpinionActivity.this);
    Summary.RegisterSummaryListener(OpinionActivity.this);
    //  updatedPageNumber=-1;

    /*
    DataAccess da = new DataAccess(this);
    da.open();
    pollList = da.getAllPoll(socUser.ResID);
    da.close();*/

    fragmentAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(fragmentAdapter);

    if(Utility.IsConnected(getApplicationContext()) && ApplicationVariable.AUTHENTICATED == true) {

        LoadPollData(StartIndex, EndIndex);

    } else {
        Toast.makeText(this, "Not Connected, Working offline", Toast.LENGTH_LONG).show();
    }
}
    catch (Exception ex)
    {
        int a=1;
    }

    }

    @Override
    public void OnPollUpdate(int PageNumber) {

        fragmentAdapter.notifyDataSetChanged();

    }


   public  class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

       public ScreenSlidePagerAdapter(FragmentManager fm) {
           super(fm);
       }

       @Override
       public Fragment getItem(int position) {
           Fragment fragment=null;
           try {
                if (pollList.size() == 0) {
                return OpinionActivityFragment.newInstance(position, dummy, pollList.size()+1);
               }
               else if(pollList.size()>0 && pollList.size() >position) {
                    fragment = OpinionActivityFragment.newInstance(position, pollList.get(position), pollList.size());
                   return fragment;
               }

              else if(position >= pollList.size())
               {
                   if(BatchCount>= GetCount)
                   {
                       StartIndex = pollList.size();
                       StartIndex = EndIndex;
                       EndIndex = StartIndex + BatchCount;
                        if(Utility.IsConnected(getApplicationContext())) {
                            LoadPollData(StartIndex, EndIndex);
                        }
                       else {
                            Toast.makeText(getApplicationContext(),"Internet not connected",Toast.LENGTH_LONG).show();
                        }


                   }
                  fragment = OpinionActivityFragment.newInstance(position, dummy, pollList.size()+1);
               }


           }
           catch (Exception ex)
           {
               Toast.makeText(getApplicationContext(),"Unable to create Fragment", Toast.LENGTH_LONG).show();

           }
           return fragment;
      }

       @Override
       public int getItemPosition(Object object) {
           int c=10;
           return POSITION_NONE;
       }

       @Override
       public int getCount() {
           int count =0;
           try {
               if(BatchCount +1 == GetCount)
               {
                   return pollList.size()+1;
               }
               else
               {
                   return pollList.size();
               }
           }
           catch (Exception ex) {

           }
           return count;
       }
   }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent pollActivity = new Intent(OpinionActivity.this,DashboardActivity.class);
            pollActivity.putExtra("PARENT","POLL");
            startActivity(pollActivity);
            OpinionActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void LoadPollData(final int firstIndex, final int lastIndex)
    {
        try {
            prgBar.setVisibility(View.VISIBLE);
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Poll/Get/" + socUser.SocietyId +"/" + socUser.ResID + "/" + PageNumber + "/" + Count;

           /* String reqBody = "{\"StartIndex\":\""+ firstIndex+ "\",\"EndIndex\":\""+ lastIndex+ "\",\"ResId\":\""+ socUser.ResID +"\",\"SocietyID\":\"" + socUser.SocietyId + "\",\"LastRefreshTime\":\"\"}";
            JSONObject jsRequest=null;

            try {
                jsRequest = new JSONObject(reqBody);
            }
            catch (JSONException jex)
            {

            }*/

            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jArray) {

                    try {

                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();
                        GetCount = jArray.length();
                        if(firstIndex==0)
                        {
                            pollList.clear();
                        }
                        for (int i = 0; i < GetCount; i++) {
                            JSONObject jObj = jArray.getJSONObject(i);
                            Polling tempPoll = new Polling();
                            tempPoll.PollID = jObj.getInt("PollID");
                            tempPoll.Question = jObj.getString("Question");
                            tempPoll.Answer1 = jObj.getString("Answer1");
                            tempPoll.Answer1Count = jObj.getInt("Answer1Count");
                            tempPoll.Answer2 = jObj.getString("Answer2");
                            tempPoll.Answer2Count = jObj.getInt("Answer2Count");
                            tempPoll.Answer3 = jObj.getString("Answer3");
                            tempPoll.Answer3Count = jObj.getInt("Answer3Count");
                            tempPoll.Answer4 = jObj.getString("Answer4");
                            tempPoll.Answer4Count = jObj.getInt("Answer4Count");
                            tempPoll.previousSelected = jObj.getInt("previousSelected");
                            tempPoll.Start_Date = jObj.getString("StartDate");
                            tempPoll.End_Date = jObj.getString("EndDate");
                            if(firstIndex ==0) {
                                da.insertNewPoll(tempPoll, socUser.ResID);
                                pollList.add(firstIndex+i,tempPoll);
                            }
                            else
                            {
                               pollList.add(firstIndex+i,tempPoll);
                            }
                        }

                        da.close();
                        prgBar.setVisibility(View.GONE);

                        if (GetCount > 0) {
                            //Session.SetPollRefreshTime(getApplicationContext());
                            ApplicationConstants.COMPLAINT_UPDATES=0;
                            fragmentAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        prgBar.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    prgBar.setVisibility(View.GONE);
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Server Error", Toast.LENGTH_LONG).show();
        }
    }

    private void LoadNewPoll()
    {

        ShowSnackBar( "Checking New Polls","");
        String url = ApplicationConstants.APP_SERVER_URL + "/api/PollDiff" ;
        LastPollRefreshTime = Session.GetPollRefreshTime(getApplicationContext());

        String reqBody = "{\"ResId\":\""+ strResID +"\",\"LastRefreshTime\":\""+ LastPollRefreshTime +"\"}";
        JSONObject jsRequest=null;

        try {
            jsRequest = new JSONObject(reqBody);
        }
        catch (JSONException jex)
        {}

        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {

                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    GetCount = json.length();

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    for(int i = 0; i < GetCount; i++){
                        JSONObject jObj = json.getJSONObject(i);
                        Polling tempPoll = new Polling();
                        tempPoll.PollID = jObj.getInt("PollID");
                        tempPoll.Question = jObj.getString("Question");
                        tempPoll.Answer1 = jObj.getString("Answer1");
                        tempPoll.Answer1Count = jObj.getInt("Answer1Count");
                        tempPoll.Answer2 = jObj.getString("Answer2");
                        tempPoll.Answer2Count = jObj.getInt("Answer2Count");
                        tempPoll.Answer3 = jObj.getString("Answer3");
                        tempPoll.Answer3Count = jObj.getInt("Answer3Count");
                        tempPoll.Answer4 = jObj.getString("Answer4");
                        tempPoll.Answer4Count = jObj.getInt("Answer4Count");
                        tempPoll.previousSelected = jObj.getInt("previousSelected");
                        da.insertNewPoll(tempPoll, socUser.ResID);
                        pollList.add(i, tempPoll);
                    }
                    if (GetCount>0)
                    {
                        Session.SetPollRefreshTime(getApplicationContext());
                        da.LimitPollData();
                        fragmentAdapter.notifyDataSetChanged();
                    }


                    da.close();
                    HideSnackBar();
                }
                catch (JSONException e)
                {
                    HideSnackBar();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowSnackBar( "Error While Synchronizing","");
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);

        queue.add(jsArrayRequest);
        //*******************************************************************************************************

    }

    private void ShowSnackBar(String htmlString, String ButtonText)
    {

        snackbar = Snackbar.make(viewPager, htmlString, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {

            }
        });

        snackbar.setAction(ButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void HideSnackBar()
    {
        if(snackbar.isShown())
        {
            snackbar.dismiss();
        }
    }


    @Override
    public void OnSummaryReceived() {
        if(ApplicationConstants.POLL_UPDATES!=0)
        {
            StartIndex=0;

            if(ApplicationConstants.POLL_UPDATES>BatchCount)
            {
                EndIndex = StartIndex + BatchCount;
            }
            else
            {
                EndIndex = StartIndex + ApplicationConstants.POLL_UPDATES;
            }
            LoadPollData(StartIndex, EndIndex);

            StartIndex=EndIndex;
        }
    }



}
