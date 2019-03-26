package net.anvisys.NestIn.Register;

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
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NewRegisterActivity extends AppCompatActivity {

    EditText txtMobile,txtEmail,txtFirstName,txtLastName,txtParentName,txtAddress;
    Button btnSubmit;
    ProgressBar progressBar;
    Profile newRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_register);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" Register as new User ");
        actionBar.show();

        txtMobile= findViewById(R.id.txtMobile);
        txtEmail= findViewById(R.id.txtEmail);
        txtFirstName= findViewById(R.id.txtFirstName);
        txtLastName= findViewById(R.id.txtLastName);
        txtParentName= findViewById(R.id.txtParentName);
        txtAddress= findViewById(R.id.txtAddress);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ValidData();
            }
        });
    }

    public void ValidData(){
        newRegister = new Profile();
        newRegister.MOB_NUMBER = txtMobile.getText().toString();
        if (newRegister.MOB_NUMBER.equals("") ) {
            txtMobile.setError("Please Enter Mobile No.");
            return;
        }
        newRegister.E_MAIL = txtEmail.getText().toString();
        if (newRegister.E_MAIL.equals("") ) {
            txtEmail.setError("Please Enter Email");
            return;
        }
        newRegister.First_Name = txtFirstName.getText().toString();
        if (newRegister.First_Name.equals("") ) {
            txtFirstName.setError("Please Enter First Name");
            return;
        }
        newRegister.Last_Name = txtLastName.getText().toString();
        if (newRegister.Last_Name.equals("") ) {
            txtLastName.setError("Please Enter Last Name");
            return;
        }
        newRegister.ParentName = txtParentName.getText().toString();
        if (newRegister.ParentName.equals("") ) {
            txtMobile.setError("Please Enter Parent Name");
            return;
        }
        newRegister.Address = txtAddress.getText().toString();
        if (newRegister.Address.equals("") ) {
            txtAddress.setError("Please Enter Address");
            return;
        }
        UserValidate();
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

                            NewRegisterActivity.this.finish();
                        }

                    } catch (JSONException je) {

                        btnSubmit.setEnabled(true);
                    }
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
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
