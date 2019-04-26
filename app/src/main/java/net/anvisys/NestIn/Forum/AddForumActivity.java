package net.anvisys.NestIn.Forum;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Custom.OvalImageView;
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AddForumActivity extends AppCompatActivity {

    TextView txtComment,txtUserName,txtFlatNumber;
    Button btnSubmit;
    ProgressBar prgBar;
    Profile myProfile;
    OvalImageView imgUser;
    SocietyUser socUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add New forum");
        actionBar.show();

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
        myProfile = Session.GetUser(getApplicationContext());
        txtComment = findViewById(R.id.txtComment);
        txtUserName =findViewById(R.id.txtUserName);
        txtFlatNumber =findViewById(R.id.txtFlatNumber);
        btnSubmit = findViewById(R.id.btnSubmit);
        prgBar = findViewById(R.id.prgBar);
        prgBar.setVisibility(View.GONE);
        imgUser = findViewById(R.id.imgUser);


        String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(imgUser);

        txtUserName.setText(myProfile.NAME);
        txtFlatNumber.setText(socUser.FlatNumber);
    }

    protected void Submit(View v)
    {
        String comment = txtComment.getText().toString();
        if (comment.length()<=5) {
            txtComment.setError("Enter Text more than 5 letter");
        }
        if(comment.length()>5)
        {
            AddNewPost(comment);
        }

    }


    private void   AddNewPost(String strComment )
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/forum/NewForum";
        String reqBody = "{\"resID\":\""+ socUser.ResID +"\",\"Topic\":\""+ "General" + "\",\"CurrentThread\":\""+ strComment+ "\",\"SocietyId\":"+ socUser.SocietyId + "}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getApplicationContext(), "Post Submitted Successfully.",
                            Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    AddForumActivity.this.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    prgBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Post could not be submitted : Try Again",
                            Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            prgBar.setVisibility(View.GONE);

        }

    }

}
