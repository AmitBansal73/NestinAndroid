package net.anvisys.NestIn.Complaints;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.Complaint;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComplaintHistoryActivity extends AppCompatActivity {


    ProgressBar progBarCompHistory;

    String strSocietyName ="";
    String strFlatNumber="";
    Complaint currentComplaint = new Complaint();

    List<Complaint> compList = new ArrayList<>();
    ListView listViewComplaint;
    Spinner spinnerStatus,spinnerStatusType;
    MyAdapter adapter;
    Button sendButton,btnCancle;
    int newStatus;
    EditText txtNewDescription;
    TextView txtCompDate,txtTicket,compDescription,textType;
    Button btnClose;
    EditText editComment;
    View viewComment;
    SocietyUser socUser;

    boolean IsUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Complaint History");
        actionBar.show();
        btnClose = findViewById(R.id.btnClose);
       // editComment = findViewById(R.id.editComment);
        btnClose.setVisibility(View.VISIBLE);
        viewComment = findViewById(R.id.viewComment);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewComment.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
                listViewComplaint.setVisibility(View.GONE);
            }
        });

        try {
            Intent mIntent = getIntent();
            currentComplaint.comp_severity = mIntent.getStringExtra("Severity");
            currentComplaint.comp_date = mIntent.getStringExtra("comp_date");
            currentComplaint.comp_desc = mIntent.getStringExtra("comp_desc");
            currentComplaint.LastStatus = mIntent.getStringExtra("comp_status");
            currentComplaint.comp_type = mIntent.getStringExtra("comp_type");
            currentComplaint.comp_id = mIntent.getIntExtra("comp_ID", 0);
            currentComplaint.FirstID = mIntent.getIntExtra("comp_row_ID",0);

            if(currentComplaint.LastStatus.matches("Closed"))
            {
                btnClose.setVisibility(View.VISIBLE);
                btnClose.setText("Re-Open");
                newStatus = 6;
            }
            else
            {
                btnClose.setVisibility(View.VISIBLE);
                btnClose.setText("Close");
                newStatus = 5;
            }

            txtCompDate = findViewById(R.id.txtCompDate);
            txtTicket = findViewById(R.id.txtTicket);
            compDescription = findViewById(R.id.compDescription);
            textType = findViewById(R.id.textType);
            listViewComplaint = findViewById(R.id.listViewComplaint);
            txtCompDate.setText(Utility.GetDate(currentComplaint.comp_date));
            txtTicket.setText("Ticket No: " + Integer.toString(currentComplaint.comp_id));
            compDescription.setText("Description: "+ currentComplaint.comp_desc);
            textType.setText("Category: " + currentComplaint.comp_type);
            listViewComplaint.setVisibility(View.VISIBLE);

            spinnerStatus =  findViewById(R.id.spinnerStatus);
            progBarCompHistory =  findViewById(R.id.progBarCompHistory);

            sendButton = findViewById(R.id.sendButton);
            btnCancle = findViewById(R.id.btnCancel);

            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewComment.setVisibility(View.GONE);
                    btnClose.setVisibility(View.VISIBLE);
                    txtNewDescription.setText("");
                }
            });


            txtNewDescription = findViewById(R.id.txtNewDescription);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Submit();
                }
            });
            progBarCompHistory.setVisibility(View.GONE);

              socUser = Session.GetCurrentSocietyUser(this);

            strSocietyName = socUser.SocietyName;
            strFlatNumber = socUser.FlatNumber;

            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
           // List<String> listCompStatus= da.getAllComplaintStatus();
           // da.close();

            LoadComplaintHistory();


        }
        catch (Exception ex)
        {
            int a = 5;
            a++;
        }
    }

    public void Submit()
    {
        try {
            if (txtNewDescription.getText().toString().matches("")) {
                Toast.makeText(this, "Please add Comment", Toast.LENGTH_SHORT).show();
            } else if(newStatus == 5) {
                currentComplaint.comp_desc = txtNewDescription.getText().toString();
                currentComplaint.comp_status = "Complete";
                currentComplaint.AssignedTo = "Admin";
                UpdateComplaint();
            }
            else if( newStatus == 6)
            {
                currentComplaint.comp_desc = txtNewDescription.getText().toString();
                currentComplaint.comp_status = "Re-OPen";
                currentComplaint.AssignedTo = "Admin";
                UpdateComplaint();
            }
        }
        catch (Exception ex)
        {

        }

    }

    class MyAdapter extends ArrayAdapter<Complaint> {

        LayoutInflater inflat;


        public MyAdapter(Context context, int resource, int textViewResourceId, List<Complaint> objects) {

            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return compList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                View rowView=null;
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.comp_history_row_item, null);
                }
                TextView cStatus =  convertView.findViewById(R.id.cStatus);
                TextView cDesc =  convertView.findViewById(R.id.cDesc);
                TextView cSeverity =  convertView.findViewById(R.id.cSeverity);
                TextView txtDateTime = convertView.findViewById(R.id.txtDateTime);
                TextView txtUser = convertView.findViewById(R.id.txtUser);

                Complaint row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                Date ComplaintDate = Utility.DBStringToLocalDate(row.comp_date);

                txtUser.setText(row.Action_By);
                txtDateTime.setText( Utility.DateToDisplayDateTime(ComplaintDate));
                cStatus.setText("Status: "+row.comp_status);
                cDesc.setText("Comment : "+row.comp_desc);
                cSeverity.setText(" Assigned to " + row.AssignedTo);
                return convertView;
            }


            catch (Exception ex)

            {
                Toast.makeText(getApplicationContext(), "Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }


        @Override
        public int getPosition(Complaint item) {
            return super.getPosition(item);
        }

        @Override
        public Complaint getItem(int position) {
            return compList.get(position);
        }
    }



    private void LoadComplaintHistory()
    {
        progBarCompHistory.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL + "/api/Complaint/" + currentComplaint.comp_id ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray json) {
                try{

                    int x = json.length();
                    for(int i = 0; i < x; i++){
                        JSONObject jObj = json.getJSONObject(i);
                        Complaint  eachRow=new Complaint();
                        eachRow.comp_id=jObj.getInt("CompID");
                        eachRow.FirstID = jObj.getInt("ID");
                        if( currentComplaint.FirstID != eachRow.FirstID)
                        {
                            eachRow.comp_desc=jObj.getString("Descrption");
                            eachRow.AssignedTo=jObj.getString("AssignedTo");
                            eachRow.comp_status=jObj.getString("CompStatus");
                            eachRow.comp_severity=jObj.getString("Severity");
                            eachRow.comp_date=jObj.getString("ModifiedAt");
                            eachRow.comp_type =jObj.getString("CompType");
                            eachRow.Action_By = jObj.getString("FirstName");
                            if(compList.size()==0)
                            {
                                adapter =new MyAdapter(ComplaintHistoryActivity.this,0, 0, compList);
                                listViewComplaint.setAdapter(adapter );
                            }
                            compList.add(eachRow);
                        }

                    }

                    adapter =new MyAdapter(ComplaintHistoryActivity.this,0, 0, compList);
                    listViewComplaint.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progBarCompHistory.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    progBarCompHistory.setVisibility(View.INVISIBLE);
                }
                catch (Exception ex)
                {
                    int a=1;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progBarCompHistory.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);


        queue.add(jsArrayRequest);

        //*******************************************************************************************************

    }




    private void   UpdateComplaint()
    {
        try {
            progBarCompHistory.setVisibility(View.VISIBLE);
            String url = ApplicationConstants.APP_SERVER_URL + "/api/Complaint";

            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            int intType = da.getComplaintTypeKey(currentComplaint.comp_type);

            da.close();

            //int type = ApplicationVariable.CompliantType.valueOf(currentComplaint.comp_type).value;
            String intSeverity = "2";

            String reqBody = "{\"UserID\":\""+ socUser.ResID +"\", \"CompID\":\""+ currentComplaint.comp_id+"\", \"FlatNumber\":\""+ strFlatNumber
                    +"\", \"CompType\":\""+ intType +  "\",\"AssignedTo\":\""+ 1 + "\",\"CompSeverity\":\""+ intSeverity + "\",\"CompDescription\":\""
                    + txtNewDescription.getText().toString() + "\",\"CompStatusID\":\""+ newStatus +"\"}";
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                     try {
                         String res  = jObj.getString("Response");

                         if(res.matches("Ok")) {
                             Toast.makeText(getApplicationContext(), "Complaint Updated Successfully.",
                                     Toast.LENGTH_SHORT).show();
                             txtNewDescription.setText("");
                             IsUpdate = true;
                             LoadComplaintHistory();
                           //  compList.add(currentComplaint);
                             progBarCompHistory.setVisibility(View.GONE);
                             viewComment.setVisibility(View.GONE);
                         }
                         else
                         {

                             Toast.makeText(getApplicationContext(), "Failed to Update.",
                                     Toast.LENGTH_SHORT).show();
                         }

                     }catch (Exception ex)
                     {

                     }
                     listViewComplaint.setVisibility(View.VISIBLE);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    Toast.makeText(getApplicationContext(),"Error Updating Complaint",Toast.LENGTH_LONG).show();
                    progBarCompHistory.setVisibility(View.GONE);

                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            progBarCompHistory.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Could not post Complaint,Contact Admin",Toast.LENGTH_LONG).show();

        }
        catch (Exception ex){
            progBarCompHistory.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Error Updating complaint",Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra("result", "Submit");
        setResult(RESULT_OK, output);
         ComplaintHistoryActivity.this.finish();
        super.onBackPressed();
    }
}
