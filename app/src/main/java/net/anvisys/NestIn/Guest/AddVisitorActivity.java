package net.anvisys.NestIn.Guest;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import net.anvisys.NestIn.Common.Utility;

import net.anvisys.NestIn.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;



public class AddVisitorActivity extends AppCompatActivity {
    TextView txtStartTime,txtEndTime;
    EditText txtMobNo,txtName,txtAddress,txtPurpose;
    Button btnSubmit;
    ProgressBar prgBar;
    Calendar StartTime =Calendar.getInstance();
    Calendar EndTime =Calendar.getInstance();
    int existingUserID= 0;

    String strMobileNo, strName, strAddress,strPurpose;
    Profile myProfile;
    SocietyUser socUser;
    String strStartTime, strEndTime;
    View viewStart;
    ImageView imgContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        txtMobNo = findViewById(R.id.txtMobNo);
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPurpose = findViewById(R.id.txtPurpose);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtEndTime = findViewById(R.id.txtEndTime);
        btnSubmit = findViewById(R.id.btnSubmit);
        viewStart = findViewById(R.id.viewStart);
        imgContact = findViewById(R.id.imgContact);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Visitor");
        actionBar.show();

        socUser = Session.GetCurrentSocietyUser(this);

        prgBar=findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();

            }
        });

        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, ApplicationConstants.PICK_CONTACT);
            }
        });

        Calendar cal = Calendar.getInstance();

        strStartTime = Utility.DateToDataBaseString(cal.getTime());

        txtStartTime.setText( Utility.DateToDisplayDateTime(cal.getTime()));

        viewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickStartTime();
            }
        });

        cal.add(Calendar.HOUR,1);
        strEndTime = Utility.DateToDataBaseString(cal.getTime());
        txtEndTime.setText(Utility.DateToDisplayDateTime(cal.getTime()));


       /* txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickEndTime();
            }
        });
        */

        txtMobNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(txtMobNo.getText().length()>=10)
                {

                    GetExistingGuest(txtMobNo.getText().toString());
                }
            }
        });
    }
    private void PickStartTime()
    {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    StartTime.set(Calendar.HOUR_OF_DAY,i);
                    StartTime.set(Calendar.MINUTE, i1);
                    strStartTime = Utility.DateToDataBaseString(StartTime.getTime());
                    txtStartTime.setText(Utility.DateToDisplayDateTime(StartTime.getTime()));

                    EndTime.set(Calendar.HOUR_OF_DAY,i+1);
                    EndTime.set(Calendar.MINUTE, i1);
                    strEndTime = Utility.DateToDataBaseString(EndTime.getTime());
                    txtEndTime.setText(Utility.DateToDisplayDateTime(EndTime.getTime()));



                }
            },0, 0, true);
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }


    private void PickEndTime()
    {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    EndTime.set(Calendar.HOUR,i);
                    EndTime.set(Calendar.MINUTE, i1);
                    strEndTime = Utility.DateToDataBaseString(EndTime.getTime());
                    txtEndTime.setText(Utility.DateToDisplayDateTime(EndTime.getTime()));

                }
            },0, 0, true);
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }


    private void Submit()
    {
        strMobileNo = txtMobNo.getText().toString().replaceAll("\\s","");;
        strName = txtName.getText().toString();
        strAddress = txtAddress.getText().toString();
        strPurpose = txtPurpose.getText().toString();
        if (strMobileNo.equals("")) {
            txtMobNo.setError("Enter Mobile Number");
        }else if (strName.equals("")) {
            txtName.setError("Enter Name");
        }else if (strAddress.equals("")) {
            txtAddress.setError("Enter Address");
        }else if (strPurpose.equals("")) {
            txtPurpose.setError("Enter Purpose");
        }

      /*  if(strMobileNo.matches("")|| strName.matches("") || strAddress.matches("")|| strPurpose.matches("") )
        {
            Snackbar.make(getCurrentFocus(),"Please fill fields",Snackbar.LENGTH_LONG);
        }  */
        else
        {
            UpdateGuestRequest();
        }
    }



    private void GetExistingGuest(String mobile)
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/visitor/"+ socUser.SocietyId +"/Mob/"+ mobile;


        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{


                    existingUserID =   jObject.getInt("id");
                   if(existingUserID!=0)
                   {
                      strMobileNo = jObject.getString("VisitorMobileNo");
                      strName = jObject.getString("VisitorName");
                      strAddress = jObject.getString("VisitorAddress");

                       txtName.setText(strName);
                       txtMobNo.setText(strMobileNo);
                       txtAddress.setText(strAddress);
                   }

                    prgBar.setVisibility(View.GONE);

                }
                catch (JSONException e)
                {
                    prgBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                prgBar.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);

        queue.add(jsArrayRequest);

    }

   private void UpdateGuestRequest()
   {

       try {
           prgBar.setVisibility(View.VISIBLE);
           String url = ApplicationConstants.APP_SERVER_URL + "/api/visitor/New";

           String reqBody = "{\"VisitorMobile\":\""+ strMobileNo+"\", \"VisitorId\":\""+ existingUserID +"\", \"VisitorName\":\""+ strName + "\",\"VisitorAddress\":\""+ strAddress + "\",\"VisitPurpose\":\""+ strPurpose + "\",\"ResID\":\""+ socUser.ResID +
                   "\",\"FlatID\":\""+ socUser.FlatID + "\",\"FlatNumber\":\"" + socUser.FlatNumber + "\",\"SocietyId\":\"" + socUser.SocietyId + "\",\"StartTime\":\""+ strStartTime + "\",\"EndTime\":\""+ strEndTime +"\"}";
           JSONObject jsRequest = new JSONObject(reqBody);
           //-------------------------------------------------------------------------------------------------
           RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

           JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject jObj) {
                   prgBar.setVisibility(View.GONE);
                   try {
                       String response = jObj.getString("Response");

                       if(response.equalsIgnoreCase("Ok")) {

                           Intent output = new Intent();
                           output.putExtra("result", "Submit");
                           setResult(RESULT_OK, output);
                           finish();
                       }
                       else
                       {
                           Toast.makeText(getApplicationContext(),"Error adding visitor", Toast.LENGTH_LONG);
                       }
                   }
                   catch (JSONException ex)
                   {
                       Toast.makeText(getApplicationContext(),"Error reading response", Toast.LENGTH_LONG);
                   }



               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   String message = error.toString();

                   prgBar.setVisibility(View.GONE);
               }
           });
           RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

           jsArrayRequest.setRetryPolicy(rPolicy);
           queue.add(jsArrayRequest);

           //*******************************************************************************************************
       }
       catch (JSONException js)
       {
           Toast.makeText(getApplicationContext(),"Could not post Complaint,Contact Admin",Toast.LENGTH_LONG).show();

       }

   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent output = new Intent();
        output.putExtra("result","Cancel");
        setResult(RESULT_CANCELED, output);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ApplicationConstants.PICK_CONTACT)
        {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader;
                loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                if (c.moveToFirst()) {
                   String ContactID =  c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                   String ContactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  String Number =  c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Log.i(TAG, "Contacts Photo Uri: " + c.getString(c.getColumnIndex(Photo.PHOTO_URI)));

                    txtMobNo.setText(Number);
                    txtName.setText(ContactName);
                }
            }
        }
    }
}
