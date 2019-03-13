package net.anvisys.NestIn.Register;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Object.Domain;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.anvisys.NestIn.Poll.OpinionActivityFragment.strResID;

public class LoginActivity extends AppCompatActivity
{
    Button btnLogin,btnDemo;
    EditText password,txtLogin;
    DataAccess _databaseAccess;
    String regID="";
    String strResponse,strUserID, strFirstName,strLastName,  strLogin, strEmailId,strMobile;
    ProgressDialog prgDialog;

    String strUser_Mobile="", strUser_Login="", strUser_Password="";

   long sleepTime = 2000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      try {
            super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_login);
            txtLogin =  findViewById(R.id.txtLogin);
            password =  findViewById(R.id.edittxtPassword);
            btnDemo = findViewById(R.id.btnDemo);
            btnDemo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent demo = new Intent(LoginActivity.this, RegisterDemoActivity.class);
                    startActivity(demo);
                }
            });
            btnLogin =  findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new clicker());
       }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will+
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    class  clicker implements OnClickListener
    {
        public void onClick(View v)
        {
            if(v == btnLogin)
            {
                try {
                    if (!Utility.IsConnected(getApplicationContext()))
                    {
                        Toast.makeText(getApplicationContext(),"Not connected to internet, Please connect", Toast.LENGTH_LONG).show();
                    }
                    else {
                        strUser_Login = txtLogin.getText().toString();

                        boolean numeric = true;

                        numeric = strUser_Login.matches("-?\\d+(\\.\\d+)?");


                        if (numeric) {
                            strUser_Mobile = strUser_Login;
                            strUser_Login = "";
                        }


                        strUser_Password = password.getText().toString();
                        if ((strUser_Mobile.length() < 5 && strUser_Login.length() < 5) || strUser_Password.length() < 3) {
                            Toast.makeText(getApplicationContext(), " Incorrect Mobile No or Password", Toast.LENGTH_LONG).show();
                        } else {
                            prgDialog = ProgressDialog.show(LoginActivity.this, "Login", "Please Wait....");
                           // ValidateUser();

                          NewValidation("");
                        }
                    }
                }
                catch (Exception ex)
                {
                    prgDialog.dismiss();
                        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Service not available temporarily : Try Again ");
                        builder.setNeutralButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                        LoginActivity.this.finish();
                                        Log.e("info", "NO");
                                    }

                                });

                        AlertDialog Alert = builder.create();
                        Alert.show();
                }
            }
        }
    }


    private void ValidateUser(){

    String url = ApplicationConstants.user_API_URL;
    String reqBody = "{\"MobileNumber\":\""+ strUser_Mobile+"\",\"LoginID\":\""+ strUser_Login+ "\",\"Password\":\""+ strUser_Password+"\"}";

    try{
        JSONObject jsRequest = new JSONObject(reqBody);
    //-------------------------------------------------------------------------------------------------
    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            strResponse = response.toString();
            try {
                strFirstName = response.get("FirstName").toString();
                if(!strFirstName.matches("")) {
                    Profile user= new Profile();
                    //strMiddleName = null;
                    strLastName = response.getString("LastName");
                    strMobile = response.getString("MobileNo");
                    strUserID = response.getString("UserId");
                    strLogin = response.getString("UserLogin");
                    strEmailId = response.getString("EmailId");
                   // strResID = response.getString("ResID");
                    String Address =  response.getString("Address");


                    /*if(strUserType.matches("Admin"))
                    {
                        Toast.makeText(getApplicationContext(),"Admin Login is not allowed",Toast.LENGTH_LONG).show();
                        return;
                    } */

                    user.NAME = strFirstName+ " "+ strLastName;
                    user.MOB_NUMBER = strMobile;
                    user.UserID = strUserID;
                    user.E_MAIL = strEmailId;
                    user.password = strUser_Password;
                    user.LOCATION =Address;
                    Session.AddUser(getApplicationContext(),user);

                  //  GetImages(Integer.parseInt(strUserID),  strMobile);



                   _databaseAccess = new DataAccess(getApplicationContext());
                    _databaseAccess.open();
                    _databaseAccess.insertNewLogin(strFirstName, "", strLastName, strEmailId, strMobile, "",0, strLogin, strUser_Password);
                    _databaseAccess.close();
                    ApplicationVariable.AUTHENTICATED = true;
                    GetResidentInfo();

                }
                else
                {
                    if (prgDialog.isShowing())
                            prgDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Login Failed..",Toast.LENGTH_LONG).show();
                }

            }
            catch (JSONException jex)
            {
                if (prgDialog.isShowing())
                         prgDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Contact Admin..",Toast.LENGTH_LONG).show();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            String message = error.toString();
            if (prgDialog!= null &&prgDialog.isShowing())
            {      prgDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Network Error - Try Again",Toast.LENGTH_LONG).show();
            }


        }
    });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsObjRequest.setRetryPolicy(rPolicy);
    queue.add(jsObjRequest);
   //-------------------------------------------------------------------------------------------------
    }
    catch (JSONException jex)
    {
        if (prgDialog.isShowing())
            prgDialog.dismiss();
        Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
    }
}

    private void GetResidentInfo(){
        String url = ApplicationConstants.APP_SERVER_URL + "/api/Resident/User/" + strUserID;

        try{
                  //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObject) {

                    try {
                    JSONArray json = jObject.getJSONArray("$values");
                    int x = json.length();
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();
                        da.deleteAllSocietyUser();
                        SocietyUser socUser =  new SocietyUser();;
                    for(int i = 0; i < x; i++) {
                        JSONObject response = json.getJSONObject(i);
                        socUser = new SocietyUser();
                        strResponse = response.toString();
                        socUser.ResID = response.getInt("ResID");
                        socUser.FlatID = response.getInt("FlatID");
                        socUser.FlatNumber = response.getString("FlatNumber");
                        socUser.RoleType = response.getString("Type");
                        socUser.SocietyName =response.getString("SocietyName");
                        socUser.SocietyId =response.getInt("SocietyID");
                        socUser.intercomNumber = response.getString("intercomNumber");
                        da.insertSocietyUser(socUser);
                    }

                        if((socUser.RoleType.matches("Owner") || socUser.RoleType.matches("Tenant"))  ) {
                            Session.AddCurrentSocietyUser(getApplicationContext(), socUser);
                            if (checkPlayServices()) {
                                new RegisterGCM().execute();
                            }
                        }

                    if (prgDialog.isShowing())
                        prgDialog.dismiss();


                    }

                    catch (JSONException jex)
                    {
                        if (prgDialog.isShowing())
                            prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Contact Admin..",Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    if (prgDialog!= null && prgDialog.isShowing())
                    {      prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Network Error - Try Again",Toast.LENGTH_LONG).show();
                    }

                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (Exception ex)
        {
            if (prgDialog.isShowing())
                prgDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText( getApplicationContext(),"This device doesn't support Play services,You may not receive Notifications",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private class RegisterGCM extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

           String newRegID="";
            try {
                    InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                newRegID = instanceID.getToken(ApplicationConstants.GOOGLE_PROJ_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            }
            catch (Exception ex)
            {}
            return newRegID;
        }

        @Override
        protected void onPostExecute(String newID) {
            regID = Session.GetRegID(getApplicationContext());
            if (!newID.matches(regID)) {
                storeRegIdinServer(newID);
              /*  if (storeRegIdinServer(newID)) {
                    storeRegIdinSharedPref(getApplicationContext(), newID, strMobile);
                }*/
            } else {

                GetDomainValue();
               /* Toast.makeText(
                        getApplicationContext(),
                        "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server" +
                                " is busy right now. Make sure you enabled Internet and try registering again after some time."
                                + regID, Toast.LENGTH_LONG).show();*/
            }

            //prgDialog.cancel();
        }
    }

    private void storeRegIdinSharedPref(Context context, String regId,
                                        String Mobile) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("regID", regId);
        editor.putString("MobileNo", Mobile);
        editor.commit();
        GetDomainValue();
        //storeRegIdinServer();
    }

    private void GetImages(final int userID,final String MobileNumber)
    {
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Image/User/" + userID ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{
                    int id = jObject.getInt("ID");
                    if(id>0) {
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();

                        String strImg = jObject.getString("ImageString");
                        da.insertImage(userID, strImg);
                        ImageServer.SaveStringAsBitmap(strImg, MobileNumber, getApplicationContext());
                        da.close();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Image Not found", Toast.LENGTH_SHORT).show();
                    }
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


    private void storeRegIdinServer(final String GCM_REG_ID){

        String url = ApplicationConstants.APP_SERVER_URL + "/api/gcmregister";
        String reqBody = "{\"MobileNo\":\""+ strMobile+ "\",\"RegID\":\""+ GCM_REG_ID + "\",\"Topic\":\""+ "" + "\"}";
        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        strResponse = response.get("Response").toString();
                        if (strResponse.matches("OK"))
                        {
                            storeRegIdinSharedPref(getApplicationContext(), GCM_REG_ID, strMobile);

                        }
                        else if (strResponse.matches("Fail"))
                        {
                            Toast.makeText(getApplicationContext(),"Could not register, may not get notifications",Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(),jex.getMessage(),Toast.LENGTH_LONG).show();
        }

        if (strResponse == null || strResponse.isEmpty())
        {
            return ;
        }
        return ;
    }


    private boolean GetDomainValue(){

        String url = ApplicationConstants.APP_SERVER_URL + "/api/domain";


        try{

            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                         DataAccess da = new DataAccess(getApplicationContext());
                         da.open();
                          JSONObject compType =  response.getJSONObject("ComplaintType");
                          JSONArray typeArray = compType.getJSONArray("$values");
                            int x = typeArray.length();
                            for(int i = 0; i < x; i++){
                                Domain domain = new Domain();
                                JSONObject obj = typeArray.getJSONObject(i);
                                domain.domain_id = obj.getInt("ID");
                                domain.domain_value = obj.getString("Value");
                                da.insertCompType(domain);
                            }


                        JSONObject compStatus =  response.getJSONObject("ComplaintStatus");
                        JSONArray statusArray = compStatus.getJSONArray("$values");
                        int y = statusArray.length();
                        for(int i = 0; i < y; i++){
                            Domain domain = new Domain();
                            JSONObject obj = statusArray.getJSONObject(i);
                            domain.domain_id = obj.getInt("ID");
                            domain.domain_value = obj.getString("Value");
                            da.insertCompStatus(domain);
                        }


                        JSONObject vendors =  response.getJSONObject("ComplaintType");
                        JSONArray vendorArray = vendors.getJSONArray("$values");
                        int z = typeArray.length();
                        for(int i = 0; i < z; i++){
                            Domain domain = new Domain();
                            JSONObject obj = vendorArray.getJSONObject(i);
                            domain.domain_id = obj.getInt("ID");
                            domain.domain_value = obj.getString("Value");
                            da.insertVendorCategory(domain);
                        }


                            da.close();

                            // storeRegIdinSharedPref(getApplicationContext(), regID, MobileNo);

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



    private class NewValidationProcess extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String newRegID="";
            try {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                newRegID = instanceID.getToken(ApplicationConstants.GOOGLE_PROJ_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            }
            catch (Exception ex)
            {
                newRegID ="";
            }
            return newRegID;
        }

        @Override
        protected void onPostExecute(String newID) {
            regID = Session.GetRegID(getApplicationContext());
            if (!newID.matches(regID)) {
                 storeRegIdinServer(newID);

            } else {

            }
        }
    }

    private void NewValidation(final String RegID)
    {
        String url = ApplicationConstants.APP_SERVER_URL + "/api/User/Validate";
        String reqBody = "{\"Mobile\":\""+ strUser_Mobile+"\",\"Email\":\""+ strUser_Login+ "\",\"Password\":\""+ strUser_Password + "\",\"RegistrationID\":\""+ RegID+"\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String result = response.getString("result");
                        if (result.matches("Fail")) {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            if (prgDialog.isShowing())
                                prgDialog.dismiss();
                        }
                        else{
                        JSONObject userData = response.getJSONObject("UserData");
                        strFirstName = userData.get("FirstName").toString();
                        if (!strFirstName.matches("")) {
                            Profile user = new Profile();
                            user.NAME = userData.getString("FirstName") + " " + userData.getString("LastName");
                            user.MOB_NUMBER = userData.getString("MobileNo");
                            user.UserID = userData.getString("UserID");
                            user.E_MAIL = userData.getString("EmailId");
                            user.Gender = userData.getString("Gender");
                            user.ParentName = userData.getString("Parentname");
                            user.password = strUser_Password;
                            user.LOCATION = userData.getString("Address");


                            ApplicationVariable.AUTHENTICATED = true;
                            JSONObject societyUserData = response.getJSONObject("SocietyUser");
                            JSONArray flatArray = societyUserData.getJSONArray("$values");
                            int x = flatArray.length();
                            DataAccess da = new DataAccess(getApplicationContext());
                            da.open();
                            da.deleteAllSocietyUser();
                            ArrayList<SocietyUser> socUserList = new ArrayList<>();
                            SocietyUser socUser;
                            for (int i = 0; i < x; i++) {
                                JSONObject flatObject = flatArray.getJSONObject(i);
                                socUser = new SocietyUser();
                                socUser.ResID = flatObject.getInt("ResID");
                                socUser.FlatID = flatObject.getInt("FlatID");
                                socUser.FlatNumber = flatObject.getString("FlatNumber");
                                socUser.RoleType = flatObject.getString("Type");
                                socUser.SocietyName = flatObject.getString("SocietyName");
                                socUser.SocietyId = flatObject.getInt("SocietyID");
                                socUser.intercomNumber = flatObject.getString("intercomNumber");
                                da.insertSocietyUser(socUser);

                                if (Session.GetCurrentSocietyUser(getApplicationContext()).ResID < 1 && (socUser.RoleType.matches("Owner") || socUser.RoleType.matches("Tenant"))) {
                                    Session.AddCurrentSocietyUser(getApplicationContext(), socUser);
                                }
                            }
                            Session.AddUser(getApplicationContext(), user);
                            GetImages(Integer.parseInt(user.UserID), user.MOB_NUMBER);
                            GetDomainValue();

                            if (Session.GetCurrentSocietyUser(getApplicationContext()).ResID > 0) {
                                if (checkPlayServices()) {
                                    new NewValidationProcess().execute();
                                }

                                Intent MenuActivity = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(MenuActivity);
                                LoginActivity.this.finish();
                            } else {

                            }

                        } else {
                            if (prgDialog.isShowing())
                                prgDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Failed..", Toast.LENGTH_LONG).show();
                        }
                    }
                    }
                    catch (JSONException jex)
                    {
                        if (prgDialog.isShowing())
                            prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Contact Admin..",Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    if (prgDialog!= null &&prgDialog.isShowing())
                    {      prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Network Error - Try Again",Toast.LENGTH_LONG).show();
                    }


                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsObjRequest.setRetryPolicy(rPolicy);
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (JSONException jex)
        {
            if (prgDialog.isShowing())
                prgDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
        }

    }

}
