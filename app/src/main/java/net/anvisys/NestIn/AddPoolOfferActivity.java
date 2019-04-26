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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Date;

public class AddPoolOfferActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText txtWhere,txtSeatAvailable,txtVehicle,txtCost,txtDescription;
    SocietyUser socUser;
    TextView txtJourneyDate,txtReturnDate,txtJourneyTime,txtReturnTime;
    Button btnSubmit;
    String strWhere="",strSeatAvailable="",strWhen="",strWhenTime="",strReturnDate="",strReturnTime="",strVehicle="",strCost="",strDescription="";
    private android.app.DatePickerDialog DatePickerDialog;

    String strJourneyDateTime;
    String strReturnDateTime;
    Calendar calendarJourney = Calendar.getInstance();
    Calendar calendarReturn = Calendar.getInstance();

    Profile myProfile;
    RadioGroup radioGroup,radioGroup1;
    RadioButton rbOneWay,rbTwoWay,rbOneTime,rbDaily;

    boolean oneWay= false;
    int PoolType = 1;


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
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup1 = findViewById(R.id.radioGroup1);
        rbOneWay = findViewById(R.id.rboneWay);
        rbTwoWay = findViewById(R.id.rbTwoWay);
        rbOneTime = findViewById(R.id.rbOneTime);
        rbDaily = findViewById(R.id.rbDaily);
        txtWhere = findViewById(R.id.txtWhere);
        txtSeatAvailable = findViewById(R.id.txtSeatAvailable);
        txtJourneyDate = findViewById(R.id.txtJourneyDate);
        txtJourneyTime = findViewById(R.id.txtJourneyTime);
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
      /*  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.travelType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.durationType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(adapter1); */

        strJourneyDateTime = Utility.CurrentDate();

        txtJourneyDate.setText( Utility.GetDateOnly(strJourneyDateTime));
        txtJourneyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JourneyDatePicker();
            }
        });
        txtJourneyTime.setText( Utility.GetTimeOnly(strJourneyDateTime));
        txtJourneyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JourneyTimePicker();
            }
        });

        strReturnDateTime = Utility.CurrentDate();

        txtReturnDate.setText( Utility.GetDateOnly(strReturnDateTime));
        txtReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnDatePicker();
            }
        });
        txtReturnTime.setText( Utility.GetTimeOnly(strReturnDateTime));
        txtReturnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnTimePicker();
            }
        });

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
    }
    public void AddCarPool(){

        if (rbOneWay.isChecked()){
            oneWay = true;
        }
        else
        {
            oneWay= false;
        }

        if (rbOneTime.isChecked()){
            PoolType = 1;
        }
        else
        {
            PoolType = 2;
        }


        strReturnDate= txtReturnDate.getText().toString();
        strReturnTime= txtReturnTime.getText().toString();


        strWhere= txtWhere.getText().toString();
        strSeatAvailable= txtSeatAvailable.getText().toString();
        strVehicle= txtVehicle.getText().toString();
        strCost= txtCost.getText().toString();
        strDescription= txtDescription.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String strInitiatedDate = Utility.CurrentDate();

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Add";

        try{
            String reqBody = "{\"Destination\":\""+ strWhere +"\", \"AvailableSeats\":\""+ strSeatAvailable +"\", \"InitiatedDateTime\":\""+ strInitiatedDate + "\",\"JourneyDateTime\":\""+ strJourneyDateTime
                    + "\",\"ReturnDateTime\":\""+ strReturnDateTime + "\",\"ResID\":\""+ socUser.ResID  + "\",\"SocietyID\":\""+ socUser.SocietyId
                    +"\",\"VehicleType\":\""+ strVehicle + "\",\"SharedCost\":\""+ strCost + "\" ,\"Active\":\"true\",\"OneWay\":\""+ oneWay + "\",\"PoolTypeID\":"
                    + PoolType +",\"Description\":\""+ strDescription +"\"}";



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



    private void JourneyDatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calendarJourney.set(year, monthOfYear, dayOfMonth);
                strJourneyDateTime = Utility.GetDateToString(calendarJourney.getTime());
                txtJourneyDate.setText(Utility.GetDateOnly(strJourneyDateTime));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }





    private void ReturnDatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calendarReturn.set(year, monthOfYear, dayOfMonth);

                strReturnDateTime = Utility.GetDateToString(calendarReturn.getTime());
                txtReturnDate.setText(Utility.GetDateOnly(strReturnDateTime));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }
    private void JourneyTimePicker() {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {


                    calendarJourney.set(Calendar.HOUR, i);
                    calendarJourney.set(Calendar.MINUTE, i1);

                    strJourneyDateTime = Utility.GetDateToString(calendarJourney.getTime());
                    String text = i + ":" + i1;
                    txtJourneyTime.setText(text);

                }
            }, 0, 0, true);
            dialog.show();
        } catch (Exception ex) {

        }
    }
    private void ReturnTimePicker() {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {


                    calendarReturn.set(Calendar.HOUR, i);
                    calendarReturn.set(Calendar.MINUTE, i1);
                    strReturnDateTime = Utility.GetDateToString(calendarReturn.getTime());
                    String text = i + ":" + i1;
                    txtReturnTime.setText(text);

                }
            }, 0, 0, true);
            dialog.show();
        } catch (Exception ex) {

        }
    }

}
