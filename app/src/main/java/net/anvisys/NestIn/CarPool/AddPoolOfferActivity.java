package net.anvisys.NestIn.CarPool;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class AddPoolOfferActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText txtWhere,txtSeatAvailable,txtVehicle,txtCost,txtDescription;
    SocietyUser socUser;
    TextView txtStartDate,txtReturnDate,txtJourneyTime,txtReturnTime;
    Button btnSubmit;
    String strWhere="",strSeatAvailable="",strWhen="",strWhenTime="",strReturnDate="",strReturnTime="",strVehicle="",strCost="",strDescription="";


    private android.app.DatePickerDialog DatePickerDialog;
    String strStartDateTime;
    String strReturnDateTime;

    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarReturn = Calendar.getInstance();

    Profile myProfile;
    RadioGroup radioRoundTrip,radioFrequency;
    RadioButton rbOneWay,rbTwoWay,rbOneTime,rbDaily;
    View viewReturn, viewFrequency;

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
        radioRoundTrip = findViewById(R.id.radioRoundTrip);
        radioFrequency = findViewById(R.id.radioFrequency);
        rbOneWay = findViewById(R.id.rboneWay);
        rbTwoWay = findViewById(R.id.rbTwoWay);
        rbOneTime = findViewById(R.id.rbOneTime);
        rbDaily = findViewById(R.id.rbDaily);
        txtWhere = findViewById(R.id.txtWhere);
        txtSeatAvailable = findViewById(R.id.txtSeatAvailable);
        txtStartDate = findViewById(R.id.txtStartDate);
       // txtJourneyTime = findViewById(R.id.txtJourneyTime);

        viewReturn = findViewById(R.id.viewReturn);
        txtReturnDate = findViewById(R.id.txtReturnDate);
        txtReturnTime = findViewById(R.id.txtReturnTime);
        txtVehicle = findViewById(R.id.txtVehicle);
        txtCost = findViewById(R.id.txtCost);
        txtDescription = findViewById(R.id.txtDescription);
        btnSubmit = findViewById(R.id.btnSubmit);

        viewFrequency = findViewById(R.id.viewFrequency);

        if(rbOneWay.isChecked())
        {
            viewFrequency.setVisibility(View.GONE);
            viewReturn.setVisibility(View.GONE);
        }

        radioRoundTrip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbOneWay.isChecked())
                {
                   // radioOneTime.setEnabled(false);
                    viewReturn.setVisibility(View.GONE);
                    viewFrequency.setVisibility(View.GONE);

                }
                else if(rbTwoWay.isChecked())
                {

                    viewReturn.setVisibility(View.VISIBLE);
                    viewFrequency.setVisibility(View.VISIBLE);
                }
                            }
        });


        radioFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(rbOneTime.isChecked())
                {
                    rbOneWay.setEnabled(true);
                }
                else if(rbDaily.isChecked())
                {
                    rbOneWay.setEnabled(false);

                    txtStartDate.setText( Utility.DateToDisplayTimeOnly(calendarStart.getTime()));
                    txtReturnDate.setText( Utility.DateToDisplayTimeOnly(calendarReturn.getTime()));

                }
            }
        });


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


        strStartDateTime = Utility.DateToDataBaseString(calendarStart.getTime());

        txtStartDate.setText( Utility.DateToDisplayDateTime(calendarStart.getTime()));

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetDateTime( "Start");
            }
        });

       /* txtJourneyTime.setText( Utility.GetTimeOnly(strJourneyDateTime));
        txtJourneyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JourneyTimePicker();
            }
        }); */

        calendarReturn.add(Calendar.HOUR, 3);

        strReturnDateTime = Utility.DateToDataBaseString(calendarReturn.getTime());
        txtReturnDate.setText( Utility.DateToDisplayDateTime(calendarReturn.getTime()));

        txtReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDateTime("Return");
            }
        });


    /*    txtReturnTime.setText( Utility.GetTimeOnly(strReturnDateTime));
        txtReturnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnTimePicker();
            }
        }); */

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
    }



    public void AddCarPool(){
        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
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

        String strInitiatedDate = Utility.CurrentDate();

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Add";

        try{
            String reqBody = "{\"Destination\":\""+ strWhere +"\", \"AvailableSeats\":\""+ strSeatAvailable +"\", \"InitiatedDateTime\":\""+ strInitiatedDate + "\",\"JourneyDateTime\":\""+ strStartDateTime
                    + "\",\"ReturnDateTime\":\""+ strReturnDateTime + "\",\"ResID\":\""+ socUser.ResID  + "\",\"SocietyID\":\""+ socUser.SocietyId
                    +"\",\"VehicleType\":\""+ strVehicle + "\",\"SharedCost\":\""+ strCost + "\" ,\"Active\":\"true\",\"OneWay\":\""+ oneWay + "\",\"PoolTypeID\":"
                    + PoolType +",\"Description\":\""+ strDescription +"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if(response.getString("Response").matches("Ok")){

                            btnSubmit.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "CarPool Added Successfully.", Toast.LENGTH_SHORT).show();

                            AddPoolOfferActivity.this.finish();

                        }else if(response.getString("Response").matches("Fail")){

                            Toast.makeText(getApplicationContext(), "Failed.....", Toast.LENGTH_SHORT).show();

                        }
                    }catch (Exception e){
                        int a= 1;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    btnSubmit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);


        }catch (Exception ex){
            btnSubmit.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void SetDateTime(String WhichWay)
    {
        if (WhichWay.matches("Start"))
        {
            if (rbDaily.isChecked())
            {
                JourneyTimePicker("Start");
            }
            else
            {
                JourneyDatePicker("Start");
            }
        }
        else if (WhichWay.matches("Return"))
        {
            if (rbDaily.isChecked())
            {
                JourneyTimePicker("Return");
            }
            else
            {
                JourneyDatePicker("Return");
            }
        }


    }


    private void JourneyDatePicker(final String WhichWay) {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                if(WhichWay.matches("Start")) {
                    calendarStart.set(year, monthOfYear, dayOfMonth);
                   // strStartDateTime = Utility.DateToDataBaseString(calendarStart.getTime());
                   // txtStartDate.setText(Utility.DateToDisplayDateTime(calendarStart.getTime()));

                    JourneyTimePicker("Start");
                }
                else {
                    calendarReturn.set(year, monthOfYear, dayOfMonth);
                    JourneyTimePicker("Return");
                }

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if(WhichWay.matches("Start")) {
            DatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 3600000);
        }
        else
        {
            DatePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis() + 3600000);
        }

        DatePickerDialog.show();

    }





    private void ReturnDatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calendarReturn.set(year, monthOfYear, dayOfMonth);

                strReturnDateTime = Utility.DateToDataBaseString(calendarReturn.getTime());
                txtReturnDate.setText(Utility. DateToDisplayDateTime(calendarReturn.getTime()));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }


    private void JourneyTimePicker(final String WhichWay) {
        try {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    if(WhichWay.matches("Start")) {
                        calendarStart.set(Calendar.HOUR, i);
                        calendarStart.set(Calendar.MINUTE, i1);
                        strStartDateTime = Utility.DateToDataBaseString(calendarStart.getTime());

                        if(rbOneTime.isChecked()) {
                            txtStartDate.setText(Utility.DateToDisplayDateTime(calendarStart.getTime()));
                        }
                        else if(rbDaily.isChecked())
                        {
                            txtStartDate.setText(Utility.DateToDisplayTimeOnly(calendarStart.getTime()));
                        }
                    }
                    else{

                        calendarReturn.set(Calendar.HOUR, i);
                        calendarReturn.set(Calendar.MINUTE, i1);
                        strReturnDateTime = Utility.DateToDataBaseString(calendarReturn.getTime());

                        if(rbOneTime.isChecked()) {
                            txtReturnDate.setText(Utility.DateToDisplayDateTime(calendarReturn.getTime()));
                        }
                        else if(rbDaily.isChecked())
                        {
                            txtReturnDate.setText(Utility.DateToDisplayTimeOnly(calendarReturn.getTime()));
                        }
                    }
                }
            }, 10, 10, false);



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
                    strReturnDateTime = Utility. DateToDataBaseString(calendarReturn.getTime());
                    String text = i + ":" + i1;
                    txtReturnTime.setText(text);

                }
            }, 0, 0, true);
            dialog.show();
        } catch (Exception ex) {

        }
    }

}
