package net.anvisys.NestIn.Complaints;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ReOpenComplaintsActivity extends AppCompatActivity {

	TextView textViewDate,textViewCategory,textViewComplaintDesc;
	Spinner spinnerStatusType, spinnerSeverity;
	EditText editTextReason;
    Button cancelButton;
	Button buttonSubmit;
    HashMap<String,String> complaintTypeDomain;
    HashMap<String,String> complaintSeverityDomain;
    HashMap<String,String> complaintStatusDomain;
    String selectedStatusType="";

    String selectedSeverity="";
    String strUserID = "";
     String strFirstName = "";
    String strLastName = "";
    String strFlatNumber = "";
    String strUsrType = "";
    String MobileNo = "";
    String strSocietyName ="";
    String compDate,compDec,compStatus,compType, compSeverity, compReason;
    Integer compID;
    ProgressBar prgBar;
    SocietyUser socUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reopen_complaints);

		textViewDate = (TextView) findViewById(R.id.textViewDate);
		textViewCategory = (TextView) findViewById(R.id.textViewCategory);
		textViewComplaintDesc = (TextView) findViewById(R.id.textViewDesc);
		spinnerStatusType = (Spinner) findViewById(R.id.spinnerStatus);
        spinnerSeverity = (Spinner) findViewById(R.id.spinnerSeverity);
        prgBar = (ProgressBar)findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);
        editTextReason = (EditText) findViewById(R.id.editReason);
        //editTextReopenComplaintDesc.setText("Test");
		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        // nagaraju added for cancel button   and header //
         cancelButton= (Button)findViewById(R.id.cancelButton);

        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        List<String> listCompStatus= da.getAllComplaintStatus();
        da.close();

        // Create an ArrayAdapter using the string array and a default spinner layout
          ArrayAdapter<String> adapterStatus =new  ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listCompStatus);
        // Specify the layout to use when the list of choices appears
          adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatusType.setAdapter(adapterStatus);


        InitializeComplaintData();
        socUser = Session.GetCurrentSocietyUser(this);
        strSocietyName = socUser.SocietyName;
        initializeDomain();



          textViewDate.setText(compDate);
          textViewCategory.setText(compType);
          textViewCategory.setTextSize(10);

          //Log.i("Add Complaints Activity User Name:",logUserName);
          System.out.println("Add Complaints Activity User Name:" + strFirstName);
            int intSelectedStatus = adapterStatus.getPosition(compStatus);
         //   int intSelectedSeverity = adapterSeverity.getPosition(compSeverity);
          //  spinnerSeverity.setSelection(intSelectedSeverity);
            spinnerStatusType.setSelection(intSelectedStatus);

          buttonSubmit.setOnClickListener(new clicker());
          cancelButton.setOnClickListener(new clicker());

          spinnerStatusType.setOnItemSelectedListener(new OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                  // your code here
                  selectedStatusType = spinnerStatusType.getSelectedItem().toString();
                 // System.out.println("Spinner value...." + spinnerStatusType.getSelectedItem().toString());
              }

              @Override
              public void onNothingSelected(AdapterView<?> parentView) {
                  // your code here
              }
          });

        spinnerSeverity.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSeverity = spinnerSeverity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.add_complaints, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class  clicker implements OnClickListener
    {
    	public void onClick(View v)
        {
            if(v == cancelButton)
            {
               // Log.i("ReOpenComplaintActivity login username ",logUserName);
                Intent ViewActivity = new Intent(ReOpenComplaintsActivity.this,
                        ViewComplaintsActivity.class);
              //  Bundle myData = CreateBundle();
               // ViewActivity.putExtras(myData);
                startActivity(ViewActivity);
                ReOpenComplaintsActivity.this.finish();

            }

           /// end of code
            if(v == buttonSubmit)
    		{
                compReason = editTextReason.getText().toString();
                   updateComplaintByVolley();
              // System.out.println("Society Id:" + society_id);
               // System.out.println("Mobile No:" + mobileno);
             }
    }

}

	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

	        	Intent MenuActivity = new Intent(ReOpenComplaintsActivity.this,
	        			DashboardActivity.class);
	            Bundle myData = CreateBundle();
	            MenuActivity.putExtras(myData);
                startActivity(MenuActivity);
                ReOpenComplaintsActivity.this.finish();
                Log.d(this.getClass().getName(), "back button pressed");
	            }
	        return super.onKeyDown(keyCode, event);
	    }
	  
    private void  updateComplaintByVolley(){

        prgBar.setVisibility(View.VISIBLE);
        try {
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Complaint/" + compID.toString();

            String intType = complaintTypeDomain.get(compType);
            String intSeverity = complaintSeverityDomain.get(selectedSeverity);
            String intStatus = complaintStatusDomain.get(selectedStatusType);
            compReason = editTextReason.getText().toString();

            String reqBody = "{\"CompId\":\"" + compID.toString() + "\", \"CompType\":\"" + intType + "\",\"UserID\":\"" + socUser.ResID + "\",\"AssignedTo\":\"" + 1 + "\",\"CompSeverity\":\""
                    + intSeverity + "\",\"CompDescription\":\"" + compReason + "\",\"CompStatusID\":\"" + intStatus + "\"}";

            try {
                JSONObject jsRequest = new JSONObject(reqBody);
                //-------------------------------------------------------------------------------------------------
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObj) {

                        // progressDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Complaint Submitted Successfully.",
                                Toast.LENGTH_SHORT).show();
                        Intent viewComplaintIntent = new Intent(ReOpenComplaintsActivity.this,
                                ViewComplaintsActivity.class);
                       // Bundle myData = CreateBundle();
                       // viewComplaintIntent.putExtras(myData);
                        startActivity(viewComplaintIntent);
                        ReOpenComplaintsActivity.this.finish();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Complaint could not be submitted : Network Error",
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



    public void InitializeComplaintData()
    {
        Intent mIntent = getIntent();
        compSeverity = mIntent.getStringExtra("Severity");
        compDate = mIntent.getStringExtra("comp_date");
        compDec = mIntent.getStringExtra("comp_desc");
        compStatus = mIntent.getStringExtra("comp_status");
        compType = mIntent.getStringExtra("comp_type");
        compID = mIntent.getIntExtra("comp_ID",0);
    }

    public Bundle CreateBundle(){

        Bundle myData = new Bundle();
        myData.putString("UserID", strUserID);
        myData.putInt("ResiID", socUser.ResID);
        myData.putString("name", strFirstName);
        myData.putString("mobileno", MobileNo);
        myData.putString("LastName", strLastName);
        myData.putString("EmailId", "abc@xyz.in");
        myData.putString("SocietyName", strSocietyName);
        myData.putString("flatno", strFlatNumber);
        myData.putString("usertype", strUsrType);
        myData.putString("FromActivity", "ReOpenComplaintsActivity");
        return myData;
    }

    public void initializeDomain()
    {

        String url = ApplicationConstants.APP_SERVER_URL + "/api/Domain" ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue( getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    JSONObject compType = jObject.getJSONObject("ComplaintType");
                    JSONArray newObj = compType.getJSONArray("$values");
                    complaintTypeDomain = new HashMap<>(6);
                    int x = newObj.length();
                    for(int i = 0; i < x; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        complaintTypeDomain.put(ID,Value);
                    }
                    JSONObject compSeverity = jObject.getJSONObject("Severity");
                    newObj = compSeverity.getJSONArray("$values");
                    complaintSeverityDomain = new HashMap<>(6);
                   // jType =  jObj.getJSONArray("$values");
                    int y = newObj.length();
                    for(int i = 0; i < y; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        complaintSeverityDomain.put(ID,Value);
                    }
                    JSONObject compStatus = jObject.getJSONObject("ComplaintStatus");
                    newObj = compStatus.getJSONArray("$values");

                    complaintStatusDomain = new HashMap<>(6);
                   // jType =  jObj.getJSONArray("$values");
                    int z = newObj.length();
                    for(int i = 0; i < z; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        complaintStatusDomain.put(ID,Value);
                    }

                }
                catch (JSONException e)
                {}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsArrayRequest);


    }
}

