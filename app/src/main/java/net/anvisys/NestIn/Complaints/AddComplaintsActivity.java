package net.anvisys.NestIn.Complaints;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class AddComplaintsActivity extends AppCompatActivity {

	Spinner categoryListView,severityList;
	EditText dateEditText,complaintEditText;
	Button submitButton,cancel;
    String selectedCategory="",selectedSeverity="",strUserID = "",strFirstName = "",strLastName = "",strFlatNumber = "",strUsrType = "",MobileNo = "",strSocietyName ="",strUserName ="";
    int networkCounter = 0;
    int intCounter = 0;
    TextView txtFlatNumber,txtUserName;
    ProgressBar prgBar;
    SocietyUser socUser;
    Profile myProfile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_complaints);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Complaint");
        actionBar.show();

		categoryListView =  findViewById(R.id.categoryListView);
		dateEditText =  findViewById(R.id.editTextDate);
		complaintEditText =  findViewById(R.id.complaintEditText);
		submitButton =  findViewById(R.id.btnSubmit);
        severityList = findViewById(R.id.severityList);
        txtUserName = findViewById(R.id.txtUserName);
        txtFlatNumber = findViewById(R.id.txtFlatNumber);

        prgBar=findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);

            DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDate = formatter.format(new Date());
          //  InitializeVariableFromIntent();
            myProfile = Session.GetUser(this);
            socUser = Session.GetCurrentSocietyUser(this);

            strSocietyName = socUser.SocietyName;
            strFlatNumber = socUser.FlatNumber;
            strUserName = myProfile.NAME;
          //  initializeDomain();

        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        List<String> listCompType= da.getAllComplaintType();
        da.close();

            // Create an ArrayAdapter using the string array and a default spinner layout
                   ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                            listCompType);
            // Specify the layout to use when the list of choices appears
                   adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
                  categoryListView.setAdapter(adapterType);

                  dateEditText.setText(currentDate);
                  txtUserName.setText(strUserName);
                  txtFlatNumber.setText(strFlatNumber);
                  submitButton.setOnClickListener(new clicker());

                categoryListView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    selectedCategory = categoryListView.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

        selectedSeverity = "Medium";
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

	class  clicker implements OnClickListener {
        public void onClick(View v) {
            {
                if (v == cancel) {
                    Toast.makeText(getApplicationContext(), "MenuActivity Clicked", Toast.LENGTH_SHORT).show();
                    Log.i("AddComplaint User Name:", strFirstName);
                    // System.out.println("AddComplaintsActivity Login User Name:"+logUserName);
                    Intent MenuActivity = new Intent(AddComplaintsActivity.this, DashboardActivity.class);
                    Bundle myData = CreateBundle();
                    MenuActivity.putExtras(myData);
                    startActivity(MenuActivity);
                    AddComplaintsActivity.this.finish();
                }
            }
            if (v == submitButton) {

                    AddNewComplaint();
            }
        }
    }

        @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        /*	Intent MenuActivity = new Intent(AddComplaintsActivity.this,
	        			DashboardActivity.class);
	            Bundle myData = CreateBundle();
	            MenuActivity.putExtras(myData);
                startActivity(MenuActivity);
                AddComplaintsActivity.this.finish();
                Log.d(this.getClass().getName(), "back button pressed");*/
	        }
	        return super.onKeyDown(keyCode, event);
	    }


    private void   AddNewComplaint()
    {
        try {
            prgBar.setVisibility(View.VISIBLE);
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Complaint";

          //  String intType = ApplicationVariable.complaintTypeDomain.get(selectedCategory);
          //  int intType = ApplicationVariable.CompliantType.valueOf(selectedCategory).value;

            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            int intType = da.getComplaintTypeKey(selectedCategory);
            da.close();

            String intStatus = "1";
            String intSeverity = "2";
            String compDescription = complaintEditText.getText().toString();
            String reqBody = "{\"UserID\":\""+ socUser.ResID +"\", \"SocietyID\":\""+ socUser.SocietyId +"\", \"CompType\":\""+ intType + "\",\"FlatNumber\":\""+ strFlatNumber
                    + "\",\"AssignedTo\":\"\",\"CompSeverity\":\""+ intSeverity + "\",\"CompDescription\":\""+ compDescription + "\",\"CompStatusID\":\""+ intStatus +"\"}";
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getApplicationContext(), "Complaint Submitted Successfully.",
                            Toast.LENGTH_SHORT).show();
                    Intent viewComplaintIntent = new Intent(AddComplaintsActivity.this,
                            ViewComplaintsActivity.class);
                  //  Bundle myData = CreateBundle();
                   // viewComplaintIntent.putExtras(myData);
                    startActivity(viewComplaintIntent);
                    prgBar.setVisibility(View.GONE);
                    AddComplaintsActivity.this.finish();
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
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error Updating Complaint",Toast.LENGTH_LONG).show();

        }


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
        myData.putString("Category", selectedCategory);
        myData.putString("Severity", selectedSeverity);
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
                    ApplicationVariable.complaintTypeDomain = new HashMap<>(6);
                    int count = newObj.length();
                    for(int i = 0; i < count; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        ApplicationVariable.complaintTypeDomain.put(ID,Value);
                    }

                    JSONObject compSeverity = jObject.getJSONObject("Severity");
                    newObj = compSeverity.getJSONArray("$values");
                    ApplicationVariable.complaintSeverityDomain = new HashMap<>(6);
                    int y = newObj.length();
                    for(int i = 0; i < y; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        ApplicationVariable.complaintSeverityDomain.put(ID,Value);
                    }
                    JSONObject compStatus = jObject.getJSONObject("ComplaintStatus");
                    newObj = compStatus.getJSONArray("$values");
                    ApplicationVariable.complaintStatusDomain = new HashMap<>(6);
                     int z = newObj.length();
                    for(int i = 0; i < z; i++){
                        JSONObject jTypeObj = newObj.getJSONObject(i);
                        String  ID=  jTypeObj.getString("Value");
                        String Value = jTypeObj.getString("ID");
                        ApplicationVariable.complaintStatusDomain.put(ID,Value);
                    }
                }
                catch (JSONException e)
                {}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();
                if(message.equals("com.android.volley.TimeoutError")&&networkCounter<5)
                {
                    networkCounter++;
                    initializeDomain();
                }

            }
        });
        queue.add(jsArrayRequest);
    }
	
}
