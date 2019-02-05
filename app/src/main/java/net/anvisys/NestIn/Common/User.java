package net.anvisys.NestIn.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Amit Bansal on 2017-09-05.
 */
public class User {

    private Context mContext;
    private String regID,strSocietyName,MobileNo,strResponse,SocietyID;

    public ArrayList<SocietyUser> socUserList = new ArrayList<>();

    public User(Context context) {
        this.mContext = context;
    }

    public void ValidateUser( String MobileNo, String Password){
        String url = ApplicationConstants.user_API_URL;
        String reqBody = "{\"MobileNumber\":\""+ MobileNo+"\",\"Password\":\""+ Password+ "\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject resObject) {

                    try {

                        String name = resObject.getString("FirstName");
                        if (resObject.getString("FirstName").matches("")) {
                            AlertDialog.Builder builder= new AlertDialog.Builder(mContext.getApplicationContext());
                            builder.setTitle("Login Failed : Try Again ");
                            builder.setNeutralButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.cancel();

                                        }

                                    });

                            AlertDialog Alert = builder.create();
                            Alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            Alert.show();
                        } else {

                            Toast.makeText(mContext, "Credentials verified", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException jex)
                    {
                        Toast.makeText(mContext, "Login Error, Try Again", Toast.LENGTH_LONG).show();


                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(mContext, "Login Error, Try Again", Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (JSONException jex)
        {
            Toast.makeText(mContext,"Server Error, Try Again",Toast.LENGTH_LONG).show();
        }
    }


    public void checkRegID( String REG_ID, String SOCIETY_NAME, String MOBILE_NO)
    {
        regID = REG_ID;
        strSocietyName = SOCIETY_NAME;
        MobileNo = MOBILE_NO;
        String url = ApplicationConstants.APP_SERVER_URL + strSocietyName + "/api/GCMRegister/" + MobileNo ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(mContext.getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    regID = jsonObject.getString("RegID");
                    new CompareRegID().execute(regID);
                }
                catch (JSONException jEx)
                {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
        //*******************************************************************************************************
    }

    private class CompareRegID extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String newRegID="";
            try {
                InstanceID instanceID = InstanceID.getInstance(mContext.getApplicationContext());
                newRegID = instanceID.getToken(ApplicationConstants.GOOGLE_PROJ_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            }
            catch (Exception ex)
            {}
            return newRegID;
        }

        @Override
        protected void onPostExecute(String newID) {
            //  regID = Session.GetRegID(getApplicationContext());
            if (!newID.matches(regID)) {
                storeRegIdinServer(newID);

            } else {

            }

            //prgDialog.cancel();
        }
    }

    private boolean storeRegIdinServer(String newRegID){

        String url = ApplicationConstants.APP_SERVER_URL + strSocietyName + "/api/gcmregister";
        String reqBody = "{\"MobileNo\":\""+ MobileNo+ "\",\"RegID\":\""+ newRegID + "\",\"Topic\":\""+ "" + "\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(mContext.getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        strResponse = response.get("Response").toString();
                        if (strResponse.matches("OK"))
                        {

                            Session.StoreRegIdInSharedPref(mContext.getApplicationContext(), regID, MobileNo);
                        }
                        else if (strResponse.matches("Fail"))
                        {
                            Toast.makeText(mContext.getApplicationContext(),"Could not register, may not get notifications",Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException ex)
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
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (JSONException jex)
        {
            Toast.makeText(mContext.getApplicationContext(),jex.getMessage(),Toast.LENGTH_LONG).show();
        }

        if (strResponse == null || strResponse.isEmpty())
        {
            return false;
        }
        return true;
    }
}
