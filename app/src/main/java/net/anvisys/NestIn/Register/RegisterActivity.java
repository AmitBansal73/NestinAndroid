package net.anvisys.NestIn.Register;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText txtMobile, txtEmail, txtFirstName, txtLastName, txtParentName, txtAddress;
    Button btnRegister, btnRegisterDemo;
    Profile newRegister;
    ImageView logo;
    ProgressBar progressBar;
    AppCompatCheckBox chkFreeTrial;
    SocietyUser socUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" Register for New User ");
        actionBar.show();

        txtMobile = findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtEmail);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtParentName = findViewById(R.id.txtParentName);
        txtAddress = findViewById(R.id.txtAddress);
        chkFreeTrial = findViewById(R.id.chkFreeTrial);
        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //  Intent i = new Intent(RegisterActivity.this, RoleActivity.class);
              //   startActivity(i);


                if(chkFreeTrial.isChecked() && ValidData() == true)
                {
                    RegisterDemoUser();
                }else if(!chkFreeTrial.isChecked() && ValidData() == true)
                {
                    RegisterNewUser();
                }
            }
        });

    }

    public boolean ValidData() {
        newRegister = new Profile();
        newRegister.First_Name = txtFirstName.getText().toString();
        newRegister.Last_Name = txtLastName.getText().toString();
        newRegister.MOB_NUMBER = txtMobile.getText().toString();
        newRegister.E_MAIL = txtEmail.getText().toString();
        newRegister.ParentName = txtParentName.getText().toString();
        newRegister.Address = txtAddress.getText().toString();

        if (newRegister.First_Name.equals("")) {
             txtFirstName.setError("Please Enter First Name");
             return false;
        }else if (newRegister.Last_Name.equals("")) {
            txtLastName.setError("Please Enter Last Name");
            return false;
        }else if (newRegister.MOB_NUMBER.equals("")) {
            txtMobile.setError("Please Enter Mobile No.");
             return false;
        }else if (newRegister.E_MAIL.equals("")) {
            txtEmail.setError("Please Enter Email");
            return false;
        }else if (newRegister.ParentName.equals("")) {
            txtMobile.setError("Please Enter Parent Name");
            return false;
        }else if (newRegister.Address.equals("")) {
            txtAddress.setError("Please Enter Address");
            return false;
        }
        return true;
    }

    private void UserValidate()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Validate";
        try {
            String reqBody = "{\"Email\":\"" +newRegister.E_MAIL+"\",\"Mobile\":\""+ newRegister.MOB_NUMBER+ "\", \"Password\":\"Password@123\",  \"RegistrationID\":\"\"}";


            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        if (result.matches("Fail")) {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }else {
                            JSONObject userData = response.getJSONObject("UserData");
                                Profile user = new Profile();
                                user.NAME = userData.getString("FirstName") + " " + userData.getString("LastName");
                                user.MOB_NUMBER = userData.getString("MobileNo");
                                user.UserID = userData.getString("UserID");
                                user.E_MAIL = userData.getString("EmailId");
                                user.Gender = userData.getString("Gender");
                                user.ParentName = userData.getString("Parentname");
                                user.LOCATION = userData.getString("Address");

                                    RegisterActivity.this.finish();
                                }

                    } catch (JSONException je) {

                        btnRegister.setEnabled(true);
                    }
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        }
        catch (JSONException jex)
        {
            int a=1;
        }

        catch (Exception js) {
            int a=1;
        } finally {

        }

    }


    private void RegisterDemoUser()
    {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/demo";
        try {
            String reqBody = "{\"UserLogin\":\"" + newRegister.E_MAIL + "\", \"MiddleName\":\"K\", \"Gender\":\"Male\",\"EmailId\":\"" +newRegister.E_MAIL+"\",\"MobileNo\":\""+ newRegister.MOB_NUMBER+ "\", \"FirstName\":\"" +newRegister.First_Name+"\", \"LastName\":\"" +newRegister.Last_Name+"\", " +
                    "\"Password\":\"Password@123\", \"ParentName\":\"" +newRegister.ParentName+"\",\"Address\":\"" +newRegister.Address+"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        if (result.matches("Fail")) {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else{
                            JSONObject userData = response.getJSONObject("UserData");

                                Profile user = new Profile();
                                user.NAME = userData.getString("FirstName") + " " + userData.getString("LastName");
                                user.MOB_NUMBER = userData.getString("MobileNo");
                                user.UserID = userData.getString("UserID");
                                user.E_MAIL = userData.getString("EmailId");
                                user.Gender = userData.getString("Gender");
                                user.ParentName = userData.getString("Parentname");
                                user.password = userData.getString("Password");
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

                                }
                                Session.AddUser(getApplicationContext(), user);


                                    Intent MenuActivity = new Intent(RegisterActivity.this, DashboardActivity.class);
                                    startActivity(MenuActivity);
                                    RegisterActivity.this.finish();
                                }



                         /*   Toast.makeText(RegisterActivity.this, "You are registered Sucsessfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            RegisterActivity.this.finish();

                            Intent MenuActivity = new Intent(RegisterActivity.this, RoleActivity.class);
                            startActivity(MenuActivity);*/

                    } catch (JSONException je) {

                        int js =1;
                    }
                    RegisterActivity.this.finish();
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
            //*******************************************************************************************************
        }
        catch (JSONException jex)
        {
            int a=1;
        }

        catch (Exception js) {
            int a=1;
        } finally {

        }

    }


    private void RegisterNewUser()
    {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/Register";
        try {
            String reqBody = "{\"UserLogin\":\"" + newRegister.E_MAIL + "\", \"MiddleName\":\"K\", \"Gender\":\"Male\",\"EmailId\":\"" +newRegister.E_MAIL+"\",\"MobileNo\":\""+ newRegister.MOB_NUMBER+ "\", \"FirstName\":\"" +newRegister.First_Name+"\", \"LastName\":\"" +newRegister.Last_Name+"\", " +
                    "\"Password\":\"Password@123\", \"ParentName\":\"" +newRegister.ParentName+"\",\"Address\":\"" +newRegister.Address+"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        if (result.matches("Fail")) {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else{
                            JSONObject userData = response.getJSONObject("UserData");

                            Profile user = new Profile();
                            user.NAME = userData.getString("FirstName") + " " + userData.getString("LastName");
                            user.MOB_NUMBER = userData.getString("MobileNo");
                            user.UserID = userData.getString("UserID");
                            user.E_MAIL = userData.getString("EmailId");
                            user.Gender = userData.getString("Gender");
                            user.ParentName = userData.getString("Parentname");
                            user.LOCATION = userData.getString("Address");
                            ApplicationVariable.AUTHENTICATED = true;
                            Session.AddUser(getApplicationContext(), user);
                            Intent MenuActivity = new Intent(RegisterActivity.this, SelectRoleActivity.class);
                            startActivity(MenuActivity);
                            RegisterActivity.this.finish();
                        }



                         /*   Toast.makeText(RegisterActivity.this, "You are registered Sucsessfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            RegisterActivity.this.finish();

                            Intent MenuActivity = new Intent(RegisterActivity.this, RoleActivity.class);
                            startActivity(MenuActivity);*/

                    } catch (JSONException je) {

                        int js =1;
                    }

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
            //*******************************************************************************************************
        }
        catch (JSONException jex)
        {
            int a=1;
        }

        catch (Exception js) {
            int a=1;
        } finally {

        }

    }
}
