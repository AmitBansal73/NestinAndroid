package net.anvisys.NestIn.Billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;

public class PaymentActivity extends AppCompatActivity {

    TextView BillingItem;
    Spinner PaymentMode;
    EditText PaymentAmount;
    EditText PaymentTransaction;
    Button PAY;
    String SocietyName;
    int ResId;
    String BillType;
    String PaidAmount;
    String PaidMode;
    String InvoiceID;
    NumberFormat currFormat;

    int PayID;
    ProgressBar prgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Payment");
        actionBar.show();

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        prgBar=(ProgressBar)findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);

      /*   BillingItem = (Spinner)findViewById(R.id.SpinnerBillingItem);
       // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterBillingType = ArrayAdapter.createFromResource(this,
                R.array.billingType, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears

        adapterBillingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BillingItem.setAdapter(adapterBillingType);
     */

        BillingItem = findViewById(R.id.SpinnerBillingItem);

        PaymentMode = findViewById(R.id.SpinnerPaymentMode);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterPaymentMode = ArrayAdapter.createFromResource(this,
                R.array.billingMode, android.R.layout.simple_spinner_item);
        adapterPaymentMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PaymentMode.setAdapter(adapterPaymentMode);
        // Specify the layout to use when the list of choices appears
        PaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PaidMode = (String) PaymentMode.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       // adapterBillingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //BillingItem.setAdapter(adapterBillingType);

        SocietyUser socUser = Session.GetCurrentSocietyUser(this);
        ResId = socUser.ResID;
        SocietyName = socUser.SocietyName;
        Intent mIntent =   getIntent();
        Bundle data = mIntent.getExtras();
        PayID = data.getInt("PayID");
        BillType = data.getString("BillType");
        PaidAmount = data.getString("BillAmount");

        BillingItem.setText(BillType);
        PaymentAmount = findViewById(R.id.editAmount);
        PaymentAmount.setText(PaidAmount);
        PaymentTransaction = findViewById(R.id.EditPaymentTransaction);
        PAY = findViewById(R.id.btnPay);
    }

    public void PayBill(View v)
    {
      BillType = BillingItem.getText().toString();
      PaidAmount = PaymentAmount.getText().toString();
        InvoiceID = PaymentTransaction.getText().toString();

        if(InvoiceID==null || InvoiceID.matches(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter Invoice ID", Toast.LENGTH_LONG).show();
        }
        if(BillType!=null && !BillType.matches("") && PaidAmount!=null && !PaidAmount.matches(""))
        {
            MakePayment();
        }
    }

    private void  MakePayment(){

        prgBar.setVisibility(View.VISIBLE);
        try {
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Billing/NewBill";


            String reqBody = "{\"PayID\":\"" + PayID + "\",\"ResId\":\"" + ResId + "\", \"PaidAmount\":\"" + PaidAmount + "\",\"TransactionID\":\"" + InvoiceID + "\",\"InvoiceID\":\"" + PayID + "\",\"PaymentMode\":\"" + PaidMode + "\"}";

            try {
                JSONObject jsRequest = new JSONObject(reqBody);
                //-------------------------------------------------------------------------------------------------
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObj) {

                        // progressDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Payment Submitted Successfully.",
                                Toast.LENGTH_SHORT).show();
                        Intent BillingIntent = new Intent(PaymentActivity.this,
                                BillingActivity.class);
                        // Bundle myData = CreateBundle();
                        // viewComplaintIntent.putExtras(myData);
                        startActivity(BillingIntent);
                        PaymentActivity.this.finish();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Payment could not be submitted : Network Error",
                                Toast.LENGTH_LONG).show();
                        prgBar.setVisibility(View.GONE);
                    }

                }) {

                };
                queue.add(jsArrayRequest);
                //*******************************************************************************************************
            } catch (JSONException js) {
                prgBar.setVisibility(View.GONE);
            }
        }
        catch (Exception ex)
        {
            prgBar.setVisibility(View.GONE);
            Toast.makeText(this, "Network Problem : Try Later", Toast.LENGTH_LONG).show();
        }
    }

}
