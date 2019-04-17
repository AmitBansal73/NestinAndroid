package net.anvisys.NestIn;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONObject;

import java.util.Calendar;

public class AddPoolOfferActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Spinner spType,spDuration;
    EditText txtWhere,txtSeatAvailable,txtVehicle,txtCost,txtDescription;
    SocietyUser socUser;
    TextView txtWhenDate,txtReturnDate,txtWhenTime,txtReturnTime;
    Button btnSubmit;
    String strWhere="",strSeatAvailable="",strWhen="",strWhenTime="",strReturnDate="",strReturnTime="",strVehicle="",strCost="",strDescription="";
    private android.app.DatePickerDialog DatePickerDialog;
    String strSelDateTime;
    Calendar calSelDateTime = Calendar.getInstance();
    Profile myProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pool_offer);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" Add Pool Offer ");
        actionBar.show();

        progressBar = findViewById(R.id.progressBar);
        spType = findViewById(R.id.spType);
        spDuration = findViewById(R.id.spDuration);
        txtWhere = findViewById(R.id.txtWhere);
        txtSeatAvailable = findViewById(R.id.txtSeatAvailable);
        txtWhenDate = findViewById(R.id.txtWhenDate);
        txtWhenTime = findViewById(R.id.txtWhenTime);
        txtReturnDate = findViewById(R.id.txtReturnDate);
        txtReturnTime = findViewById(R.id.txtReturnTime);
        txtVehicle = findViewById(R.id.txtVehicle);
        txtCost = findViewById(R.id.txtCost);
        txtDescription = findViewById(R.id.txtDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCarPool();
            }
        });
        myProfile = Session.GetUser(this);
        socUser = Session.GetCurrentSocietyUser(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.travelType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.durationType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(adapter1);

        txtWhenDate.setText( Utility.GetDateOnly(strSelDateTime));
        txtWhenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker();
            }
        });
        txtWhenTime.setText( Utility.GetTimeOnly(strSelDateTime));
        txtWhenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickTime();
            }
        });
        txtReturnDate.setText( Utility.GetDateOnly(strSelDateTime));
        txtReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker1();
            }
        });
        txtReturnTime.setText( Utility.GetTimeOnly(strSelDateTime));
        txtReturnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickTime1();
            }
        });

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

    }

    public void AddCarPool(){
        strWhere= txtWhere.getText().toString();
        strSeatAvailable= txtSeatAvailable.getText().toString();
        strWhen= txtWhenDate.getText().toString();
        strWhenTime= txtWhenTime.getText().toString();
        strReturnDate= txtReturnDate.getText().toString();
        strReturnTime= txtReturnTime.getText().toString();
        strVehicle= txtVehicle.getText().toString();
        strCost= txtCost.getText().toString();
        strDescription= txtDescription.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Add";
        try{
            String reqBody = "{\"Destination\":\""+ strWhere +"\", \"AvailableSeats\":\""+ strSeatAvailable +"\", \"InitiatedDateTime\":\""+ strWhen + "\",\"WhenTime\":\""+ strWhenTime
                    + "\",\"[ReturnDateTime\":\""+ strReturnDate + "\",\"ReturnTime\":\""+ strReturnTime + "\",\"ResID\":\""+ socUser.ResID  + "\",\"SocietyID\":\""+ socUser.SocietyId
                    +"\",\"VehicleType\":\""+ strVehicle + "\",\"SharedCost\":\""+ strCost + "\",\"Active\":\"true\",\"OneWay\":\"true\",\"Description\":\""+ strDescription +"\"}";;
            JSONObject jsRequest = new JSONObject(reqBody);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").matches("Ok")){
                            Toast.makeText(getApplicationContext(), "CarPool Added Successfully.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            AddPoolOfferActivity.this.finish();
                        }else if(response.getString("Response").matches("Fail")){
                            Toast.makeText(getApplicationContext(), "Failed.....", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        int a= 1;
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

        }
    }

    private void DatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calSelDateTime.set(year, monthOfYear, dayOfMonth);
                strSelDateTime = Utility.GetDateToString(calSelDateTime.getTime());
                txtWhenDate.setText(Utility.GetDateOnly(strSelDateTime));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }
    private void DatePicker1() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calSelDateTime.set(year, monthOfYear, dayOfMonth);

                strSelDateTime = Utility.GetDateToString(calSelDateTime.getTime());
                txtReturnDate.setText(Utility.GetDateOnly(strSelDateTime));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }
    private void PickTime() {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {


                    calSelDateTime.set(Calendar.HOUR, i);
                    calSelDateTime.set(Calendar.MINUTE, i1);

                    strSelDateTime = Utility.GetDateToString(calSelDateTime.getTime());

                    txtWhenTime.setText(Utility.GetTimeOnly(strSelDateTime));

                }
            }, 0, 0, true);
            dialog.show();
        } catch (Exception ex) {

        }
    }
    private void PickTime1() {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {


                    calSelDateTime.set(Calendar.HOUR, i);
                    calSelDateTime.set(Calendar.MINUTE, i1);

                    strSelDateTime = Utility.GetDateToString(calSelDateTime.getTime());

                    txtReturnTime.setText(Utility.GetTimeOnly(strSelDateTime));

                }
            }, 0, 0, true);
            dialog.show();
        } catch (Exception ex) {

        }
    }

}
