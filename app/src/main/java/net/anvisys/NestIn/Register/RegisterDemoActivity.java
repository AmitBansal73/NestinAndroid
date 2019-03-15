package net.anvisys.NestIn.Register;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterDemoActivity extends AppCompatActivity {

    EditText txtMobile,txtEmail,txtFirstName,txtLastName,txtParentName,txtAddress;
    Button btnRegister;
    Profile newRegister;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_demo);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("NestIn");
        actionBar.show();

        txtMobile= findViewById(R.id.txtMobile);
        txtEmail= findViewById(R.id.txtEmail);
        txtFirstName= findViewById(R.id.txtFirstName);
        txtLastName= findViewById(R.id.txtLastName);
        txtParentName= findViewById(R.id.txtParentName);
        txtAddress= findViewById(R.id.txtAddress);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VadlidData();


                Intent purpose = new Intent(RegisterDemoActivity.this, demoActivity.class);
                startActivity(purpose);
            }
        });

    }



    public void VadlidData(){
        newRegister = new Profile();
        newRegister.MOB_NUMBER = txtMobile.getText().toString();
        newRegister.E_MAIL = txtEmail.getText().toString();
        newRegister.First_Name = txtFirstName.getText().toString();
        newRegister.Last_Name = txtLastName.getText().toString();
        newRegister.ParentName = txtParentName.getText().toString();
        newRegister.Address = txtAddress.getText().toString();
        UserRegister();
    }

    private void UserValidate()
    {

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

                                    Intent MenuActivity = new Intent(RegisterDemoActivity.this, DashboardActivity.class);
                                    startActivity(MenuActivity);
                                    RegisterDemoActivity.this.finish();
                                }

                    } catch (JSONException je) {

                        btnRegister.setEnabled(true);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
            btnRegister.setEnabled(true);
        }

        catch (Exception js) {
            int a=1;
            btnRegister.setEnabled(true);
        } finally {

        }

    }


    private void UserRegister()
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
                        if(response.getString("Response").matches("Fail"))
                        {
                            if(response.getString("IsMail").matches("false"))
                            {
                                Toast.makeText(RegisterDemoActivity.this, "EMail is already registered", Toast.LENGTH_SHORT).show();

                            }
                            if(response.getString("IsMail").matches("false"))
                            {
                                Toast.makeText(RegisterDemoActivity.this, "EMail is already registered", Toast.LENGTH_SHORT).show();
                            }

                        }else if (response.getString("Response").matches("OK")){
                            Toast.makeText(RegisterDemoActivity.this, "You are registered Sucsessfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Intent MenuActivity = new Intent(RegisterDemoActivity.this, demoActivity.class);
                            startActivity(MenuActivity);
                            RegisterDemoActivity.this.finish();
                        }

                    } catch (JSONException je) {

                        int js =1;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
            btnRegister.setEnabled(true);
        }

        catch (Exception js) {
            int a=1;
            btnRegister.setEnabled(true);
        } finally {

        }

    }
}
