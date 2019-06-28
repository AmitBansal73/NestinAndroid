package net.anvisys.NestIn.Register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.R;

import org.json.JSONObject;

public class RoleHomeActivity extends AppCompatActivity {

    EditText txtHouseNo, txtSector, txtLocality, txtCity, txtState, txtPincode;
    Button btnHouse;
    ProgressBar progressBar;
    Profile myProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_home);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("House Details");
        actionBar.show();

        myProfile = Session.GetUser(getApplicationContext());


        txtHouseNo = findViewById(R.id.txtHouseNo);
        txtSector = findViewById(R.id.txtSector);
        txtLocality = findViewById(R.id.txtLocality);
        txtCity = findViewById(R.id.txtCity);
        txtState = findViewById(R.id.txtState);
        txtPincode = findViewById(R.id.txtPincode);

        btnHouse = findViewById(R.id.btnHouse);
        progressBar = findViewById(R.id.progressBar);

        btnHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHouse();
            }
        });


    }

    public void AddHouse(){
        progressBar.setVisibility(View.VISIBLE);


        String strCity = txtCity.getText().toString();
        String strPincode = txtPincode.getText().toString();
        String strLocality = txtLocality.getText().toString();
        String strSector = txtSector.getText().toString();
        String strState = txtState.getText().toString();
        String strHouse = txtHouseNo.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/House/"+ myProfile.UserID;

        final SocietyUser soc = new SocietyUser();
        soc.RoleType = "Individual";
        soc.Status = "Approved";
        soc.SocietyName = "Individual";
        soc.FlatNumber = strHouse;
        soc.SocietyId = 0;
        soc.ResID = 0;
        soc.FlatID = 0;
        soc.intercomNumber = "";

        try {
            String reqBody =  "{\"City\":\""  +strCity+"\",\"State\":\"" +strState+"\",\"Pincode\":\""
                    +strPincode+"\",\"HouseNumber\":\""+strHouse+"\",\"Sector\":\"" +strSector+"\"}";



            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if(response.getString("Response").matches("Ok"))
                        {
                            Toast.makeText(getApplicationContext(), "House Added Successfully.", Toast.LENGTH_SHORT).show();
                            DataAccess da = new DataAccess(getApplicationContext());
                            da.open();
                            da.insertSocietyUser(soc);
                            da.close();
                            RoleHomeActivity.this.finish();


                        }else if (response.getString("Response").matches("Fail")){
                            Toast.makeText(getApplicationContext(), " Failed ", Toast.LENGTH_SHORT).show();

                        }

                    }catch (Exception ex){

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    Toast.makeText(getApplicationContext(), " Server Erro, Try again ", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);

                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);


        }catch (Exception e){

        }
    }


}
