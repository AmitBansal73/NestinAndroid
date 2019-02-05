package net.anvisys.NestIn;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amit Bansal on 29-11-2016.
 */
public class Summary {

    String LastRefreshTime;

    static SummaryListener summaryListener;

    public  void LoadUpdateCount(final Context context)
    {

        LastRefreshTime = Session.GetComplaintRefreshTime(context);

        SocietyUser socUser = Session.GetCurrentSocietyUser(context);

        // prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL + "/api/LastUpdate" ;

        String reqBody = "{\"ForumRefreshTime\":\""+ Session.GetForumRefreshTime(context)+ "\",\"ComplaintRefreshTime\":\""+ Session.GetComplaintRefreshTime(context)+ "\",\"BillRefreshTime\":\""+ Session.GetBillRefreshTime(context)+ "\",\"VendorRefreshTime\":\""+ Session.GetVendorRefreshTime(context)
                + "\",\"PollRefreshTime\":\""+ Session.GetPollRefreshTime(context)+ "\",\"NoticeRefreshTime\":\""+ Session.GetPollRefreshTime(context)+ "\",\"flatNo\":\""+ socUser.FlatNumber + "\",\"resID\":\""+ socUser.ResID +"\"}";
        JSONObject jsRequest=null;

        try {
            jsRequest = new JSONObject(reqBody);
        }
        catch (JSONException jex)
        {}
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    //JSONArray json = jArray.getJSONArray("$values");
                    ApplicationConstants.COMPLAINT_UPDATES = jsonObject.getInt("ComplaintCount");
                    ApplicationConstants.TOTAL_COMPLAINT_COUNT = jsonObject.getInt("TotalComplaintCount");
                    ApplicationConstants.FORUM_UPDATES = jsonObject.getInt("ForumCount");
                    ApplicationConstants.TOTAL_FORUM_COUNT = jsonObject.getInt("TotalForumCount");
                    ApplicationConstants.VENDOR_UPDATES = jsonObject.getInt("VendorCount");
                    ApplicationConstants.TOTAL_VENDOR_COUNT = jsonObject.getInt("TotalVendorCount");
                    ApplicationConstants.NOTICE_UPDATES = jsonObject.getInt("NoticeCount");
                    ApplicationConstants.TOTAL_NOTICE_COUNT = jsonObject.getInt("TotalNoticeCount");
                    ApplicationConstants.BILL_UPDATES = jsonObject.getInt("BillCount");
                    ApplicationConstants.TOTAL_BILL_COUNT = jsonObject.getInt("TotalBillCount");
                    ApplicationConstants.POLL_UPDATES = jsonObject.getInt("PollCount");
                    ApplicationConstants.TOTAL_POLL_COUNT = jsonObject.getInt("TotalPollCount");

                  //  setCounter();

                    //     Session.SetComplaintRefreshTime(getApplicationContext());
                    if(summaryListener!=null)
                    {
                        summaryListener.OnSummaryReceived();
                    }

                }
                catch (JSONException e)
                {
                    Toast.makeText(context, "Data Type has changed, Contact Admin", Toast.LENGTH_LONG).show();
                }
                catch(Exception ex)
                {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.toString();
              /*  if(message.equals("com.android.volley.TimeoutError")&&summaryCounter<5)
                {
                    summaryCounter++;
                    LoadSummary();
                }
                */
            }
        });

        RetryPolicy policy = new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };

        jsArrayRequest.setRetryPolicy(policy);
        queue.add(jsArrayRequest);
        //*******************************************************************************************************
    }

  public  interface SummaryListener
    {
        void OnSummaryReceived();
    }

    public static void RegisterSummaryListener(SummaryListener listener)
    {
        summaryListener = listener;
    }

    public static void RemoveSummaryListener()
    {
        summaryListener = null;
    }

}
