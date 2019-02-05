package net.anvisys.NestIn;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.User;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Register.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartActivity extends Activity {

    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_STORAGE_REQUEST_CODE = 2;
    private static final int READ_STORAGE_REQUEST_CODE = 3;
    String MobileNo;
    String SocietyName;
    String RegID;
    Profile myProfile;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        checkNetworkPermission();

    }

    private void checkNetworkPermission()
    {
        if ((ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, INTERNET_PERMISSION_REQUEST_CODE);
        }

        else
        {
            checkStoragePermission();
        }

    }

    private void checkStoragePermission()
    {
        if ((ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                )
        {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
            //  requestLocationPermission();
        }
        else
        {
            checkReadStoragePermission();
        }
    }

    private void checkReadStoragePermission()
    {
        if ((ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                )
        {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
            //  requestLocationPermission();
        }
        else
        {
            MoveToDashBoard();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case INTERNET_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkStoragePermission();

                } else {

                    StartActivity.this.finish();

                }
                break;

            case WRITE_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkReadStoragePermission();

                } else {

                    StartActivity.this.finish();

                }
                break;
            case READ_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    MoveToDashBoard();

                } else {

                    StartActivity.this.finish();

                }
                break;
        }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



    private void MoveToDashBoard()
    {
        try {
            if (Utility.IsConnected(this)) {

                myProfile = Session.GetUser(this);
                if (myProfile == null || myProfile.MOB_NUMBER.matches("")) {
                    Intent LoginIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(LoginIntent);
                    StartActivity.this.finish();
                } else {
                    MobileNo = myProfile.MOB_NUMBER;

                    RegID = Session.GetRegID(this);
                    // verify user in parallel
                    /*DataAccess da = new DataAccess(this);
                    da.open();
                    String Password = da.getCredentials(MobileNo);
                    da.close();*/


                    // GetImages(myProfile.UserID, myProfile.MOB_NUMBER);


                    // User user = new User(getApplicationContext());
                    //     ValidateUser(MobileNo, Password); commented for debugging
                   /* Intent dashboardIntent = new Intent(StartActivity.this, DashboardActivity.class);
                    startActivity(dashboardIntent);
                    StartActivity.this.finish();*/

                    NewValidation(MobileNo,myProfile.password);
                }
            } else {
                Intent dashboardIntent = new Intent(StartActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                StartActivity.this.finish();
                Toast.makeText(this, "Not Connected, Working offline", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Unable to start Application, Contact Admin", Toast.LENGTH_LONG).show();
        }


    }




    public class AuthenticatedRequest extends JsonObjectRequest {

        private String mAuthToken;

        public AuthenticatedRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public void setAuthToken(String token) {
            mAuthToken = token;
        }

        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + mAuthToken);

            return headers;
        }
    }

    public void ValidateUser( final String MobileNo, final String Password){
        String url = ApplicationConstants.user_API_URL;
        String reqBody = "{\"MobileNumber\":\""+ MobileNo+"\",\"Password\":\""+ Password+ "\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject resObject) {

                    try {

                        String name = resObject.getString("FirstName");
                        if (name.matches("")) {
                            LoginFailedDialog();

                        } else {
                            ApplicationVariable.AUTHENTICATED = true;
                            User user = new User(getApplicationContext());
                            user.checkRegID(RegID,SocietyName,MobileNo);
                            Intent dashboardIntent = new Intent(StartActivity.this,DashboardActivity.class);
                            startActivity(dashboardIntent);
                            StartActivity.this.finish();
                          //  Toast.makeText(getApplicationContext(), "Credentials verified", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException jex)
                    {
                        //Toast.makeText(getApplicationContext(), "Login Error, Try Again", Toast.LENGTH_LONG).show();
                        LoginFailedDialog();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LoginFailedDialog();
                    //Toast.makeText(getApplicationContext(), "Login Error, Try Again", Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (JSONException jex)
        {
            Toast.makeText(getApplicationContext(),"Server Error, Try Again",Toast.LENGTH_LONG).show();
        }
    }


    private void NewValidation(final String MobileNo,final String Password)
    {
        String url = ApplicationConstants.APP_SERVER_URL + "/api/User/Validate";
        String reqBody = "{\"Mobile\":\""+ MobileNo+"\",\"Email\":\"\",\"Password\":\""+ Password + "\",\"RegistrationID\":\"\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject userData = response.getJSONObject("UserData");

                       String strFirstName = userData.get("FirstName").toString();
                        if(!strFirstName.matches("")) {
                            Profile user= new Profile();
                            user.NAME = userData.getString("FirstName")+ " "+ userData.getString("LastName");
                            user.MOB_NUMBER = MobileNo;
                            user.UserID = userData.getString("UserID");
                            user.E_MAIL = userData.getString("EmailId");
                            user.Gender = userData.getString("Gender");
                            user.ParentName = userData.getString("Parentname");
                            user.password = Password;
                            user.LOCATION =userData.getString("Address");


                           /* _databaseAccess = new DataAccess(getApplicationContext());
                            _databaseAccess.open();
                            _databaseAccess.insertNewLogin(strFirstName, "", strLastName, strEmailId, strMobile, "",0, strLogin, strUser_Password);
                            _databaseAccess.close();*/

                            ApplicationVariable.AUTHENTICATED = true;

                            JSONObject societyUserData = response.getJSONObject("SocietyUser");

                            JSONArray flatArray = societyUserData.getJSONArray("$values");

                            int x = flatArray.length();

                            if(x==0)
                            {
                                Toast.makeText(getApplicationContext(), "No Flat Associated, Try Again", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                DataAccess da = new DataAccess(getApplicationContext());
                                da.open();
                                    ArrayList<SocietyUser> socUserList = new ArrayList<>();
                                    SocietyUser socUser;
                                   da.deleteAllSocietyUser();
                                    for(int i = 0; i < x; i++) {
                                        JSONObject flatObject = flatArray.getJSONObject(i);
                                        socUser = new SocietyUser();
                                        socUser.ResID = flatObject.getInt("ResID");
                                        socUser.FlatID = flatObject.getInt("FlatID");
                                        socUser.FlatNumber = flatObject.getString("FlatNumber");
                                        socUser.RoleType = flatObject.getString("Type");
                                        socUser.SocietyName =flatObject.getString("SocietyName");
                                        socUser.SocietyId =flatObject.getInt("SocietyID");
                                        socUser.intercomNumber = flatObject.getString("intercomNumber");
                                        socUserList.add(socUser);
                                        da.insertSocietyUser(socUser);
                                    }

                                  SocietyUser currentUser =  Session.GetCurrentSocietyUser(getApplicationContext());
                                    if(!da.checkSocietyUserExist(currentUser.ResID))
                                    {
                                        ArrayList<SocietyUser> newList = new ArrayList<>();
                                        for (SocietyUser su: newList
                                             ) {
                                            if(su.RoleType.matches("Owner")|| su.RoleType.matches("Tenant"))
                                            {
                                                Session.AddCurrentSocietyUser(getApplicationContext(), su);
                                                Intent MenuActivity = new Intent(StartActivity.this, DashboardActivity.class);
                                                startActivity(MenuActivity);
                                                StartActivity.this.finish();
                                                break;
                                            }
                                        }

                                        Toast.makeText(getApplicationContext(),"No User found", Toast.LENGTH_LONG);
                                    }
                                    else
                                    {

                                        Intent MenuActivity = new Intent(StartActivity.this, DashboardActivity.class);
                                        startActivity(MenuActivity);
                                        StartActivity.this.finish();
                                    }

                            }
                            Session.AddUser(getApplicationContext(),user);



                        }
                        else
                        {

                            Toast.makeText(getApplicationContext(),"Login Failed..",Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (JSONException jex)
                    {

                        Toast.makeText(getApplicationContext(),"Contact Admin..",Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    LoginFailedDialog();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (JSONException jex)
        {
            LoginFailedDialog();
            //Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
        }

    }



    private void LoginFailedDialog()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(StartActivity.this);
        builder.setTitle("Login Failed: Try Again ");
        builder.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                        StartActivity.this.finish();

                    }

                });
        builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               /* DataAccess mDataAccess = new DataAccess(getApplicationContext());
                mDataAccess.open();
                mDataAccess.ClearAll();
                mDataAccess.close();
                Session.LogOff(getApplicationContext());
                DeleteRegIdFromServer();*/
                Intent LoginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
                StartActivity.this.finish();
            }
        });
        AlertDialog Alert = builder.create();

        Alert.show();
    }

    private void DeleteRegIdFromServer() {
        String url = ApplicationConstants.APP_SERVER_URL + "/api/gcmregister/5";
        String reqBody = "\""+ MobileNo + "\"}";

        try{
            //  JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,reqBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // System.exit(0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (Exception jex)
        {
            Toast.makeText(getApplicationContext(),jex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    private void GetImages(final String UserID,final String MobileNumber)
    {
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Image/User/" + UserID ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{
                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int id = jObject.getInt("ID");
                    String  strImg = jObject.getString("ImageString");
                    da.insertImage(Integer.parseInt(UserID),strImg);
                    ImageServer.SaveStringAsBitmap(strImg, MobileNumber, getApplicationContext());
                    da.close();
                }
                catch (JSONException e)
                {

                }

                catch (Exception ex)
                {
                    int b =8;
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

    }
}
