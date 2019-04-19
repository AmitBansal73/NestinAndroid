package net.anvisys.NestIn;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Object.FlatInfo;
import net.anvisys.NestIn.Object.Rent;
import net.anvisys.NestIn.Register.RentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFlatActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView txtName,txtEmail,txtContact,txtFlat,txtFloor,txtBHK,txtBlock,txtIntercom,txtFlatArea,txtAddress,txtResID,txtAdmin,txtSocietyName;
    ImageView imageProfile;
    SocietyUser socUser;
    Profile myProfile;
    FlatInfo flatInfo;
    Button addCarPool,addRent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_flat);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" My Flat ");
        actionBar.show();

        progressBar = findViewById(R.id.progressBar);
        imageProfile = findViewById(R.id.imageProfile);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtContact = findViewById(R.id.txtContact);
        txtFlat = findViewById(R.id.txtFlat);
        txtFloor = findViewById(R.id.txtFloor);
        txtBHK = findViewById(R.id.txtBHK);
        txtBlock = findViewById(R.id.txtBlock);
        txtIntercom = findViewById(R.id.txtIntercom);
        txtFlatArea = findViewById(R.id.txtFlatArea);
        txtAddress = findViewById(R.id.txtAddress);
        txtResID = findViewById(R.id.txtResID);
        txtSocietyName = findViewById(R.id.txtSocietyName);
        txtAdmin = findViewById(R.id.txtAdmin);
        addCarPool = findViewById(R.id.addCarPool);
        addCarPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFlatActivity.this,AddPoolOfferActivity.class);
                startActivity(intent);
            }
        });
        addRent = findViewById(R.id.addRent);
        addRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFlatActivity.this, AddRentActivity.class);
                startActivity(intent);
            }
        });
        socUser = Session.GetCurrentSocietyUser(this);
        myProfile = Session.GetUser(this);
        String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image1).into(imageProfile);

        LoadFlatInfo();
    }

    public void LoadFlatInfo(){
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Flat/" + socUser.FlatID;
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        flatInfo = new FlatInfo();
                        flatInfo.FirstName = response.getString("OwnerFirstName");
                        flatInfo.LastName = response.getString("OwnerLastName");
                        flatInfo.EmailId = response.getString("OwnerEmail");
                        flatInfo.Contact = response.getString("OwnerMobile");
                        flatInfo.FlatNo = response.getString("FlatNumber");
                        flatInfo.Floor = response.getString("Floor");
                        flatInfo.Bhk = response.getString("BHK");
                        flatInfo.Block = response.getString("Block");
                        flatInfo.Intercom = response.getString("IntercomNumber");
                        flatInfo.FlatArea = response.getString("FlatArea");
                        flatInfo.ResId = response.getString("OwnerResID");
                        flatInfo.UserId = response.getString("OwnerUserID");
                        flatInfo.Address = response.getString("OwnerAddress");

                        txtName.setText("  "+flatInfo.FirstName +" "+ flatInfo.LastName);
                        txtEmail.setText("  "+flatInfo.EmailId);
                        txtContact.setText("  "+flatInfo.Contact);
                        txtFlat.setText("  "+flatInfo.FlatNo);
                        txtFloor.setText("  "+flatInfo.Floor);
                        txtBHK.setText("  "+flatInfo.Bhk);
                        txtBlock.setText("  "+flatInfo.Block);
                        txtIntercom.setText("  "+flatInfo.Intercom);
                        txtFlatArea.setText("  "+flatInfo.FlatArea);
                        txtResID.setText("  "+flatInfo.ResId);
                        txtAddress.setText("  "+flatInfo.Address);
                        txtSocietyName.setText("  "+socUser.SocietyName);
                        txtAdmin.setText("   Updating..");

                    } catch ( JSONException e) {
                        progressBar.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);
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

        }
    }
}
