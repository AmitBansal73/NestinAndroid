package net.anvisys.NestIn.Billing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Custom.OvalImageView;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Model.Bill;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;

public class BillingActivity extends AppCompatActivity implements Summary.SummaryListener {


    private HashMap<String,Bill> billingItemList = new HashMap<>();
    ListView listViewBill;
    private BillAdapter adapter;
    ProgressBar prgBarBilling;
    Snackbar snackbar;
    NumberFormat currFormat;
    OvalImageView imageProfile;
    int selYear,selMonth;
    TextView billMonth,name;
    Profile myProfile;
    SocietyUser socUser;
    ImageView action_previous,action_Next;
    private android.app.DatePickerDialog monthDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Bills");
        actionBar.show();
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        billMonth = findViewById(R.id.billMonth);
        billMonth.setText(Utility.ChangeToMonthDisplayFormat(Utility.GetCurrentDateTimeLocal()));

        myProfile = Session.GetUser(this);
        socUser = Session.GetCurrentSocietyUser(this);
        name = findViewById(R.id.txtProfile);
        name.setText(myProfile.NAME);
        imageProfile = findViewById(R.id.imageProfile);
        String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(imageProfile);


      /*  Bitmap bmp = ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, this);
        if (bmp == null) {
            ImageServer.SaveStringAsBitmap(myProfile.strImage, myProfile.MOB_NUMBER, this);
            bmp = ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, this);
        }

        imageProfile.setImageBitmap(bmp); */


        prgBarBilling=(ProgressBar)findViewById(R.id.prgBarBilling);
        prgBarBilling.setVisibility(View.GONE);
        listViewBill = (ListView) findViewById(R.id.listViewBill);
        adapter = new BillAdapter(this);
        listViewBill.setAdapter(adapter);

        Summary.RegisterSummaryListener(BillingActivity.this);

        //getBillingData();

        if(Utility.IsConnected(this) && ApplicationVariable.AUTHENTICATED == true)
        {
                LoadBillingData();
        }
        else
        {
            DataAccess da = new DataAccess(this);
            da.open();
            billingItemList = da.getAllBill(socUser.ResID);
            da.close();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Not Connected, Working offline", Toast.LENGTH_LONG).show();
        }

    }



    private class BillAdapter extends BaseAdapter{

        LayoutInflater inflater;

        public BillAdapter(Activity activity) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return billingItemList.size();
        }

        @Override
        public Bill getItem(int position) {
            return (Bill)billingItemList.values().toArray()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.row_item_bill, null);
            }
            try {
                TextView txtbillType = view.findViewById(R.id.billType);
                TextView paidDate = view.findViewById(R.id.paidDate);
                TextView paidAmount = view.findViewById(R.id.paidAmount);
                TextView currentBill = view.findViewById(R.id.currentBill);
                TextView TotalAmount = view.findViewById(R.id.TotalAmount);
                TextView txtDueDate = view.findViewById(R.id.DueDate);


                Button btnPay = view.findViewById(R.id.btnPay);

                final Bill billItem = getItem(position);

                btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utility.IsConnected(getApplicationContext()) && ApplicationVariable.AUTHENTICATED == true) {
                            Intent paymentIntent = new Intent(BillingActivity.this, PaymentActivity.class);
                            Bundle paymentBundle = new Bundle();
                            paymentBundle.putInt("PayID", billItem.PayID);
                            paymentBundle.putString("BillType", billItem.BillType);
                            paymentBundle.putString("BillAmount", billItem.TotalPayable);
                            paymentIntent.putExtras(paymentBundle);
                            startActivity(paymentIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Can not Pay in Offline Mode", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                currFormat = NumberFormat.getCurrencyInstance();
                currFormat.setCurrency(Currency.getInstance("INR"));
                txtbillType.setText(" " + billItem.BillType);
                // paidAmount.setText(billItem.AmountPaid);
                // txtcurrDue.setText(billItem.Amount);
                if (billItem.AmountPaid > 0) {
                    //       txtAmountPaid.setVisibility(View.VISIBLE);
                    //  paidAmount.setText("Paid: INR "+Integer.toString( billItem.AmountPaid));
                    TotalAmount.setText(currFormat.format(billItem.CurrentMonthBalance));

                } else {
                    //  txtAmountPaid.setVisibility(View.GONE);
                    TotalAmount.setText(currFormat.format(Long.parseLong(billItem.TotalPayable)));
                }

                currentBill.setText(currFormat.format(billItem.Amount));
                paidDate.setText("   on  " + Utility.ChangeFormat(billItem.PaidDate));
                paidAmount.setText("Paid: " + currFormat.format(billItem.AmountPaid));
                txtDueDate.setText("Due On:  " + Utility.ChangeFormat(billItem.DueDate));
            }
            catch (Exception ex)
            {

                ShowSnackBar( "Error creating view", "");
            }


            return view;
        }
    }

    public  void SelectMonth(View v){
        Calendar newCalendar = Calendar.getInstance();

     monthDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();

                newDate.set(year, monthOfYear, dayOfMonth);
                selYear = newDate.get(Calendar.YEAR);
                selMonth = newDate.get(Calendar.MONTH);

                LoadBillingDataOfMonth(selYear, selMonth);

                TextView  billMonth = findViewById(R.id.billMonth);
                billMonth.setText(Utility.DateToDisplayDateTime( newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        monthDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent billingActivity = new Intent(BillingActivity.this,
                    DashboardActivity.class);

            billingActivity.putExtra("PARENT","BILLING");
           // Bundle myData = CreateBundle();

            startActivity(billingActivity);
            BillingActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void LoadBillingData()
    {
        ShowSnackBar( "Synchronising Billing Data","");
        // prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Billing/"+ socUser.SocietyId  +"/Flat/" + socUser.FlatNumber ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {

                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    if(x>0)
                    {
                        da.deleteAllBill();
                    }
                    for(int i = 0; i < x; i++){
                        JSONObject jObj = json.getJSONObject(i);
                        Bill bill = new Bill();
                        bill.BillType = jObj.getString("BillType");
                        bill.Balance = jObj.getString("PreviousMonthBalance");
                        bill.Amount = jObj.getInt("CurrentBillAmount");
                        bill.TotalPayable = jObj.getString("AmountTobePaid");
                        bill.DueDate = jObj.getString("PaymentDueDate");
                        bill.PayID = jObj.getInt("PayID");
                        bill.CurrentMonthBalance =jObj.getInt("CurrentMonthBalance");
                        bill.PaidDate = jObj.getString("AmountPaidDate");
                        bill.AmountPaid = jObj.getInt("AmountPaid");
                        da.insertNewBill(bill, socUser.ResID);
                    }
                    if (x>0)
                    {
                        Session.SetBillRefreshTime(getApplicationContext());
                        ApplicationConstants.BILL_UPDATES =0;
                        billingItemList = da.getAllBill(socUser.ResID);
                    }
                    adapter.notifyDataSetChanged();
                    HideSnackBar();
                    if(x == 0)
                    {
                        ShowSnackBar( "No Data to Display", "");
                    }
                }
                catch (JSONException e)
                {
                    ShowSnackBar( "Could Not Synchronize, try later", "Retry");
                    // prgBar.setVisibility(View.GONE);
                }
                catch (Exception ex)
                {
                    ShowSnackBar( "Could Not Synchronize, try later", "Retry");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();

                ShowSnackBar( "Could Not Synchronize, try later", "Retry");
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************
    }


    private void LoadBillingDataOfMonth(int Year, int Month)
    {
        ShowSnackBar( "Synchronising Billing Data","");
        // prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Billing/"+  socUser.SocietyId +"/Flat/" + socUser.FlatNumber + "/" + Integer.toString(Year) + "/" + Integer.toString(Month);
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {

                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    if(x>0)
                    {
                        da.deleteAllBill();
                    }
                    for(int i = 0; i < x; i++){
                        JSONObject jObj = json.getJSONObject(i);
                        Bill bill = new Bill();
                        bill.BillType = jObj.getString("BillType");
                        bill.Balance = jObj.getString("PreviousMonthBalance");
                        bill.Amount = jObj.getInt("CurrentBillAmount");
                        bill.TotalPayable = jObj.getString("AmountTobePaid");
                        bill.DueDate = jObj.getString("PaymentDueDate");
                        bill.PayID = jObj.getInt("PayID");
                        bill.CurrentMonthBalance =jObj.getInt("CurrentMonthBalance");
                        bill.PaidDate = jObj.getString("AmountPaidDate");
                        bill.AmountPaid = jObj.getInt("AmountPaid");
                        da.insertNewBill(bill, socUser.ResID);
                    }

                    if (x>0)
                    {
                        Session.SetBillRefreshTime(getApplicationContext());
                        ApplicationConstants.BILL_UPDATES =0;
                        billingItemList = da.getAllBill(socUser.ResID);
                    }
                    adapter.notifyDataSetChanged();
                    HideSnackBar();
                    if(x == 0)
                    {
                        ShowSnackBar( "No Data to Display", "");
                    }
                }
                catch (JSONException e)
                {
                    ShowSnackBar( "Could Not Synchronize, try later", "Retry");
                    // prgBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();

                ShowSnackBar( "Could Not Synchronize, try later", "Retry");
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************
    }

    private void ShowSnackBar(String htmlString, String ButtonText)
    {

        snackbar = Snackbar
                .make(listViewBill, htmlString, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        snackBarView.setBackgroundColor(getResources().getColor(R.color.blue));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
            }
        });

        snackbar.setAction(ButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadBillingData();
            }
        });
    }

    private void HideSnackBar()
    {
        if(snackbar.isShown())
        {
            snackbar.dismiss();
        }
    }

    @Override
    public void OnSummaryReceived() {
        if(ApplicationConstants.BILL_UPDATES!=0)
        {
            LoadBillingData();
        }
    }
}
