package net.anvisys.NestIn.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    Spinner spPurpose;
    Button btnSubmit;
    String selectedPuprose="",strSociety="",strHouse="",strSector="",strFlat="",strFloor="",strBlock="",strIntercom="",strLocality="",strCity="",strState="",strPincode="";
    LinearLayout rowSosiety,row1,row2,row3,row4,row0;
    Profile myProfile;
    EditText txtSociety,txtHouse,txtSector,txtFlat,txtFloor,txtBlock,txtIntercom,txtLocality,txtCity,txtState,txtPincode;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        btnSubmit = findViewById(R.id.btnSubmit);
        spPurpose = findViewById(R.id.spPurpose);
        rowSosiety = findViewById(R.id.rowSosiety);
        row1= findViewById(R.id.row1);
        row2= findViewById(R.id.row2);
        row3= findViewById(R.id.row3);
        row4= findViewById(R.id.row4);
        row0= findViewById(R.id.row0);
        txtSociety = findViewById(R.id.txtSociety);
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

                selectedPuprose = spPurpose.getSelectedItem().toString();
                if (selectedPuprose.equalsIgnoreCase("Indipendent House"))
                {
                    row0.setVisibility(View.VISIBLE);
                    rowSosiety.setVisibility(View.GONE);
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.GONE);
                    row3.setVisibility(View.GONE);
                }else if (selectedPuprose.equalsIgnoreCase("Enroll in Existing Society"))
                {
                    row0.setVisibility(View.VISIBLE);
                    rowSosiety.setVisibility(View.VISIBLE);
                    row1.setVisibility(View.GONE);
                    row2.setVisibility(View.VISIBLE);
                    row3.setVisibility(View.VISIBLE);
                }else if (selectedPuprose.equalsIgnoreCase("Request For New Society Registration"))
                {
                    row0.setVisibility(View.VISIBLE);
                    rowSosiety.setVisibility(View.VISIBLE);
                    row1.setVisibility(View.GONE);
                    row2.setVisibility(View.GONE);
                    row3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                row0.setVisibility(View.GONE);
            }
        });
    }

    public void AddHouse(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/House/"+ myProfile.UserID;
        try {
            String reqBody =  "{\"UserId\":\"" +myProfile.UserID+"\",\"Society\":\"" +strSociety+"\",\"City\":\"" +strCity+"\",\"State\":\"" +strState+"\",\"Pincode\":\""
                    +strPincode+"\",\"HouseID\":\""+strHouse+"\",\"Sector\":\"" +strSector+"\",\"Locality\":\"" +strLocality+"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "House Added Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(demoActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    demoActivity.this.finish();
                    progressBar.setVisibility(View.GONE);
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
