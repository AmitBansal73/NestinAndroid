package net.anvisys.NestIn.Register;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import net.anvisys.NestIn.Complaints.AddComplaintsActivity;
import net.anvisys.NestIn.Complaints.ViewComplaintsActivity;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.R;

import org.json.JSONObject;

public class demoActivity extends AppCompatActivity {

    Spinner spPurpose,selectSociety;
    Button btnSubmit;
    String selectedPurpose="",strSociety="",strHouse="",strSector="",strLocality="",strCity="",strState="",strPincode="";
    LinearLayout societyDetail,newRegistration,indipendent,existingSociety;
    Profile myProfile;
    EditText txtSocietyNew,txtHouse,txtSector,txtFlat,txtFloor,txtBlock,txtIntercom,txtLocality,txtCity,txtState,txtPincode;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" NestIn ");
        actionBar.show();

        btnSubmit = findViewById(R.id.btnSubmit);
        spPurpose = findViewById(R.id.spPurpose);
        newRegistration = findViewById(R.id.newRegistration);
        indipendent = findViewById(R.id.indipendent);
        existingSociety = findViewById(R.id.existingSociety);
        societyDetail= findViewById(R.id.societyDetail);
        selectSociety = findViewById(R.id.selectSociety);
        txtSocietyNew = findViewById(R.id.txtSocietyNew);
        txtHouse = findViewById(R.id.txtHouse);
        txtSector = findViewById(R.id.txtSector);
        txtFlat= findViewById(R.id.txtFlat);
        txtFloor= findViewById(R.id.txtFloor);
        txtBlock= findViewById(R.id.txtBlock);
        txtIntercom= findViewById(R.id.txtIntercom);
        txtLocality= findViewById(R.id.txtLocality);
        txtCity= findViewById(R.id.txtCity);
        txtState= findViewById(R.id.txtState);
        txtPincode= findViewById(R.id.txtPincode);
        progressBar = findViewById(R.id.progressBar);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHouse();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LoginPurpose, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPurpose.setAdapter(adapter);

        spPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedPurpose = spPurpose.getSelectedItem().toString();
                 if (selectedPurpose.equalsIgnoreCase("Indipendent House"))
                {
                    societyDetail.setVisibility(View.VISIBLE);
                    indipendent.setVisibility(View.VISIBLE);
                    existingSociety.setVisibility(View.GONE);
                    newRegistration.setVisibility(View.GONE);
                }else if (selectedPurpose.equalsIgnoreCase("Enroll in Existing Society"))
                {
                    societyDetail.setVisibility(View.VISIBLE);
                    existingSociety.setVisibility(View.VISIBLE);
                    indipendent.setVisibility(View.GONE);
                    newRegistration.setVisibility(View.GONE);
                }else if (selectedPurpose.equalsIgnoreCase("Request For New Society Registration"))
                {
                    societyDetail.setVisibility(View.VISIBLE);
                    newRegistration.setVisibility(View.VISIBLE);
                    indipendent.setVisibility(View.GONE);
                    existingSociety.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                societyDetail.setVisibility(View.GONE);
            }
        });


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.societyName, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSociety.setAdapter(adapter1);
    }

    public void AddHouse(){
        progressBar.setVisibility(View.VISIBLE);
        strSociety= txtSocietyNew.getText().toString();
        strCity = txtCity.getText().toString();
        strPincode = txtPincode.getText().toString();
        strLocality = txtLocality.getText().toString();
        strSector = txtSector.getText().toString();
        strState = txtState.getText().toString();
        strHouse = txtHouse.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/House/"+ myProfile.UserID;
        try {
            String reqBody =  "{\"UserId\":\"" +myProfile.UserID+"\",\"Society\":\"" +strSociety+"\",\"City\":\"" +strCity+"\",\"State\":\"" +strState+"\",\"Pincode\":\""
                    +strPincode+"\",\"HouseID\":\""+strHouse+"\",\"Sector\":\"" +strSector+"\",\"Locality\":\"" +strLocality+"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   try {
                       if(response.getString("Response").matches("Ok"))
                       {
                           Toast.makeText(getApplicationContext(), "House Added Successfully.", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(demoActivity.this, DashboardActivity.class);
                           startActivity(intent);
                           demoActivity.this.finish();
                           progressBar.setVisibility(View.GONE);

                       }else if (response.getString("Response").matches("Fail")){
                           Toast.makeText(getApplicationContext(), " Failed ", Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                       }

                   }catch (Exception ex){

                   }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();

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
