package net.anvisys.NestIn;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Object.Accomodation;
import net.anvisys.NestIn.Object.Inventory;
import net.anvisys.NestIn.Object.Rent;
import net.anvisys.NestIn.Register.RentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddRentActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Spinner InventoryType,AccomodationType;
    EditText txtInterest, txtContactNumber,txtContactName,txtRent,txtDescription;
    ArrayAdapter<String> adapterInventoryType,adapterAccomodationType;
    Button btnAddRent;
    int selectedInventoryType, selectedAccomodationType;
    Accomodation accomodation;
    Inventory inventory;
    List<String> listInventoryType= new ArrayList<>();
    HashMap<String,Integer> hashInventoryType = new HashMap<>();
    List<String> listAccomodationType= new ArrayList<>();
    HashMap<String,Integer> hashAccomodationType = new HashMap<>();
    Profile myProfile;
    SocietyUser socUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rent);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" Add For Rent ");
        actionBar.show();

        progressBar = findViewById(R.id.progressBar);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        txtContactName = findViewById(R.id.txtContactName);
        txtRent = findViewById(R.id.txtRent);
        txtDescription = findViewById(R.id.txtDescription);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        InventoryType = findViewById(R.id.InventoryType);
        AccomodationType = findViewById(R.id.AccomodationType);
        btnAddRent = findViewById(R.id.btnAddRent);
        btnAddRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRent();
            }
        });
        myProfile = Session.GetUser(this);
        socUser = Session.GetCurrentSocietyUser(this);

        adapterInventoryType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listInventoryType);
        adapterInventoryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        InventoryType.setAdapter(adapterInventoryType);

        InventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String strInventoryType = InventoryType.getSelectedItem().toString();
                selectedInventoryType= hashInventoryType.get(strInventoryType);
                GetAccomodationType(selectedInventoryType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //adapterAccomodationType = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapterAccomodationType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listAccomodationType);
        adapterAccomodationType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        AccomodationType.setAdapter(adapterAccomodationType);

        AccomodationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String strAccomodationType = AccomodationType.getSelectedItem().toString();
                selectedAccomodationType= hashAccomodationType.get(strAccomodationType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        GetInventoryType();

    }

    private void GetInventoryType(){
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/InventoryType";
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");
                        int x = json.length();
                        for (int i = 0; i <x; i++) {

                            JSONObject jObj = json.getJSONObject(i);

                             String InventoryType = jObj.getString("InventoryType");
                            int InventoryTypeID = jObj.getInt("InventoryTypeID");
                            hashInventoryType.put(InventoryType, InventoryTypeID);
                            listInventoryType.add(InventoryType);
                        }
                        progressBar.setVisibility(View.GONE);
                        adapterInventoryType.notifyDataSetChanged();

                    } catch (JSONException e) {
                       int a=1;
                    }
                    catch (Exception ex)
                    {
                        int a=1;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception ex){
            int a=1;
        }

    }

    private void GetAccomodationType(int id)
    {
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Accomodation/" + id;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");
                        int x = json.length();
                        hashAccomodationType.clear();
                        adapterAccomodationType.clear();
                        for (int i = 0; i <x; i++) {

                            JSONObject jObj = json.getJSONObject(i);
                            accomodation = new Accomodation();
                            String AccomodationType = jObj.getString("AccomodationType");
                            int AccomodationTypeID = jObj.getInt("AccomodationTypeID");
                            hashAccomodationType.put(AccomodationType, AccomodationTypeID);
                            adapterAccomodationType.add(AccomodationType);
                        }


                        adapterAccomodationType.notifyDataSetChanged();

                    } catch (JSONException e) {
                       int a=1;
                    }
                    catch (Exception ex)
                    {
                        int a=1;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception ex){
            int a=1;
        }
    }
    public void AddRent(){
        String contactNumber = txtContactNumber.getText().toString();
        String contactName = txtContactName.getText().toString();
        String rent = txtRent.getText().toString();
        String Description = txtDescription.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/New";
        try {
            String reqBody = "{\"InventoryTypeID\":\""+ selectedInventoryType +"\",\"AccomodationTypeID\":\""+ selectedAccomodationType + "\",\"RentValue\":\""+ rent
                    + "\",\"Available\":\"true" +  "\",\"Description\":\""+ Description +"\",\"ContactNumber\":\""+ contactNumber +"\",\"ContactName\":\""+ contactName
                    +"\",\"UserID\":\""+ myProfile.UserID +"\",\"FlatID\":\""+ socUser.FlatID +"\",\"HouseID\":\"0\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").matches("OK")) {
                            Toast.makeText(getApplicationContext(), "Flat added for Rent.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(AddRentActivity.this, RentActivity.class);
                            startActivity(intent);
                        }
                        else if(response.getString("Response").matches("Duplicate")) {
                            Toast.makeText(getApplicationContext(), "Flat already on Rent ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else if(response.getString("Response").matches("Fail")) {
                            Toast.makeText(getApplicationContext(), "Failed... ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        int a=1;
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
        }catch (Exception ex){
        int b=1;
        }
    }
}
