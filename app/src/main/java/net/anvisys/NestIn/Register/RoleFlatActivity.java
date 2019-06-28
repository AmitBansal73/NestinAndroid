package net.anvisys.NestIn.Register;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.Flat;
import net.anvisys.NestIn.Model.Society;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RoleFlatActivity extends AppCompatActivity {

    ArrayAdapter<String> adapterSocieties;
    ListView societyList;
    List<String> societyData= new ArrayList<>();
    ArrayList<Flat> arraylistflat=new ArrayList<>();
    HashMap<String, Society> societyHashMap = new HashMap<>();
    ProgressBar progressBar;

    EditText selectSociety, selectFlat;

    Society selectedSociety;
    TextView txtHeader;
    TextView txtLocalityCity, txtExistingState,txtExistingPincode;
    TextView txtExistingArea,txtExistingFloor, txtExistingBlock,txtExistingIntercom;
    TextView txtSearchFlat;
    Button btnSubmitRole;
    Profile myProfile;
    Flat selectedFlat;
    String Role = "Owner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_flat);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Select Flat");
        actionBar.show();

        myProfile = Session.GetUser(getApplicationContext());

        Intent intent = getIntent();

        Role = intent.getStringExtra("Role");

        txtHeader = findViewById(R.id.txtHeader);
        txtHeader.setText("I am " + Role + " in ... ");

        progressBar = findViewById(R.id.progressBar);
        societyList = findViewById(R.id.societyList);
        selectSociety = findViewById(R.id.selectSociety);

        txtLocalityCity = findViewById(R.id.txtLocalityCity);
        txtExistingState = findViewById(R.id.txtExistingState);
        txtExistingPincode = findViewById(R.id.txtExistingPincode);

        selectFlat = findViewById(R.id.selectFlat);
        txtExistingArea = findViewById(R.id.txtExistingArea);
        txtExistingFloor = findViewById(R.id.txtExistingFloor);
        txtExistingBlock = findViewById(R.id.txtExistingBlock);
        txtExistingIntercom  = findViewById(R.id.txtExistingIntercom);
        txtSearchFlat = findViewById(R.id.txtSearchFlat);
        btnSubmitRole = findViewById(R.id.btnSubmitRole);

        selectSociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                societyList.setVisibility(View.VISIBLE);
            }
        });

        selectSociety.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterSocieties.getFilter().filter(s);
                societyList.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adapterSocieties = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, societyData);
        adapterSocieties.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        societyList.setAdapter(adapterSocieties);

        societyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Society = (String) societyList.getItemAtPosition(position);
                selectedSociety = societyHashMap.get(Society);
                selectSociety.setText(Society);
                societyList.setVisibility(View.GONE);
                txtLocalityCity.setText(selectedSociety.City);
                txtExistingState.setText(selectedSociety.Sector);
                txtExistingPincode.setText("");
            }
        });

        txtSearchFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFlat();
            }
        });

        btnSubmitRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddResidentRequest();
            }
        });

        GetSocietyData();
    }


    private void GetSocietyData() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Society/All";


            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,  new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jArray = response.getJSONArray("$values");

                        if(jArray.length()>0)
                        {
                            for(int i =0; i<jArray.length(); i++)
                            {
                               JSONObject jObj = jArray.getJSONObject(i);
                                Society tempSociety = new Society();
                                tempSociety.SocietyID= jObj.getInt("SocietyID");
                                tempSociety.SocietyName = jObj.getString("SocietyName");
                                tempSociety.Sector = jObj.getString("Address");
                                tempSociety.City = jObj.getString("City");
                                societyData.add(tempSociety.SocietyName);
                                societyHashMap.put(tempSociety.SocietyName, tempSociety);
                            }
                            adapterSocieties.notifyDataSetChanged();

                        }
                       else if(response.getString("Response").matches("Fail")) {

                            Toast.makeText(getApplicationContext(), "Failed... ", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        int a = 1;
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();

                    progressBar.setVisibility(View.GONE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);

            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        } catch (Exception js) {
            Toast.makeText(getApplicationContext(), "Could not Update Academic profile,Contact Admin", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void GetFlat()
    {
       String strFlat = selectFlat.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Flat/"+ selectedSociety.SocietyID+"/"+strFlat ;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {
                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();
                    if(x>0) {
                        for (int i = 0; i < x; i++) {
                            JSONObject jObj = json.getJSONObject(i);
                            selectedFlat = new Flat();
                            selectedFlat.FlatId = jObj.getString("ID");
                            selectedFlat.FlatNumber = jObj.getString("FlatNumber");
                            selectedFlat.Block = jObj.getString("Block");
                            selectedFlat.FlatArea = jObj.getInt("FlatArea");
                            selectedFlat.Floor = jObj.getInt("Floor");
                            selectedFlat.IntercomNumber = jObj.getInt("IntercomNumber");
                            arraylistflat.add(selectedFlat);
                            txtExistingArea.setText("Area:  " + selectedFlat.FlatArea);
                            txtExistingFloor.setText("Floor:   " + Integer.toString(selectedFlat.Floor));
                            txtExistingBlock.setText("Block:   " + selectedFlat.Block);
                            txtExistingIntercom.setText("Intercom:  " + Integer.toString(selectedFlat.IntercomNumber));

                        }
                        btnSubmitRole.setVisibility(View.VISIBLE);
                    }
                    else {
                        txtExistingArea.setText("No Flat Found");
                    }
                   // adapterFlats = new RoleActivity.MyAdapterFlats(RoleFlatActivity.this, 0, 0, arraylistflat);
                   // flatList.setAdapter(adapterFlats);
                   // adapterFlats.notifyDataSetChanged();

                     /*   flatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Flats currentFlat = arraylistflat.get(position);
                                txtExistingArea.setText("Area: "+ currentFlat.FlatArea);
                                txtExistingFloor.setText("Floor: "+ Integer.toString( currentFlat.Floor));
                                txtExistingBlock.setText("Block: "+ currentFlat.Block);
                                txtExistingIntercom.setText("Intercom: "+ Integer.toString( currentFlat.IntercomNumber));
                            }
                        });   */


                    progressBar.setVisibility(View.GONE);




                }catch (JSONException js){
                    int e= 1;
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
    }

    private  void AddResidentRequest()
    {
       final SocietyUser soc = new SocietyUser();
        soc.RoleType = Role;
        soc.Status = "Applied";
        soc.SocietyName = selectedSociety.SocietyName;
        soc.FlatNumber = selectedFlat.FlatNumber;
        soc.SocietyId = selectedSociety.SocietyID;
        soc.ResID = 0;
        soc.FlatID = Integer.getInteger(selectedFlat.FlatId);
        soc.intercomNumber = "";


        String date = Utility.CurrentDate();
        Date newDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        Date deactiveDate = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH));

        String strDeactiveDate = Utility.DateToDataBaseString(deactiveDate);

        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/SocietyUser";
        try{
            String reqBody =  "{\"UserId\":\"" +myProfile.UserID+"\",\"FlatID\":\"" +selectedFlat.FlatId+"\",\"Type\":\"" +Role+"\",\"ServiceType\":\"" +0+"\",\"CompanyName\":\""
                    +"NA"+"\",\"ActiveDate\":\""+date + "\",\"DeActiveDate\":\""+ strDeactiveDate +" \",\"ModifiedDate\":\""+ date+"\",\"SocietyID\":\"" +selectedSociety.SocietyID +"\",\"Status\":\"1\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String result = response.getString("result");

                        if (result.matches("Fail")) {
                            Toast.makeText(getApplicationContext(), "Could Not submit Request", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            DataAccess da = new DataAccess(getApplicationContext());
                            da.open();
                            da.insertSocietyUser(soc);
                            da.close();

                            Toast.makeText(getApplicationContext(), "Request has been submitted", Toast.LENGTH_SHORT).show();
                            RoleFlatActivity.this.finish();
                        }
                    }
                    catch (Exception ex)
                    {

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
