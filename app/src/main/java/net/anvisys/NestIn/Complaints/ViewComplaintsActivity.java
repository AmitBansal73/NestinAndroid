package net.anvisys.NestIn.Complaints;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Model.Complaint;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewComplaintsActivity extends AppCompatActivity implements Summary.SummaryListener {


    String  compDate,compDec,compStatus,compType,compSeverity;
    int Comp_Row_ID,compID;
    Complaint eachCompRow;

    List<Complaint> compAllList = new ArrayList<>();
   // List<Complaint> compArrayList=new ArrayList<Complaint>();
    ListView listViewComplaint;
    View retrieveView;
    ProgressBar progBarComplaint;
    String LastComplaintRefreshTime="";
    MyAdapter compListViewAdapter;
    FloatingActionButton fab;

    int BatchCount=10;
    int EndIndex =10;
    int StartIndex =0;
    int GetCount =0;
    int PageNumber =1;
    int Count = 10;

    String Status = "Open";
    Snackbar snackbar;
    boolean IsRetreiving = false;
    SocietyUser socUser;
    TextView noData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_complaints);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Complaints");
        actionBar.show();
        progBarComplaint=(ProgressBar)findViewById(R.id.progBarComplaint);
        progBarComplaint.setVisibility(View.GONE);
    //    Summary.RegisterSummaryListener(ViewComplaintsActivity.this);

         listViewComplaint=findViewById(R.id.listViewComplaint);
         noData = findViewById(R.id.noData);

       // spinnerStatus = (Spinner) findViewById(R.id.spinStatus);
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.complaintViewStatus, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //   spinnerStatus.setAdapter(adapterStatus);


       // compListViewAdapter = new MyAdapter(ViewComplaintsActivity.this, 0, compArrayList);

        fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent AddComplainActivity = new Intent(ViewComplaintsActivity.this, AddComplaintsActivity.class);
                        startActivityForResult(AddComplainActivity, ApplicationConstants.REQUEST_ADD_COMPLAINT);

                    }
                });



// Added by Amit to open ReopenComplain
        listViewComplaint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Id) {

                try {
                    int ItemPosition = position;
                    Complaint itemValue = (Complaint) listViewComplaint.getItemAtPosition(position);
                    compDate = itemValue.comp_date;
                    compDec = itemValue.comp_desc;
                    compStatus = itemValue.LastStatus;
                    compType = itemValue.comp_type;
                    compID = itemValue.comp_id;
                    compSeverity = itemValue.comp_severity;
                    Comp_Row_ID = itemValue.FirstID;


                   // Intent ReOpenComplaintsActivity = new Intent(ViewComplaintsActivity.this, ReOpenComplaintsActivity.class);
                    Intent ReOpenComplaintsActivity = new Intent(ViewComplaintsActivity.this, ComplaintHistoryActivity.class);
                    Bundle myData = new Bundle();
                    myData.putString("Severity", compSeverity);
                    myData.putString("comp_date", compDate);
                    myData.putString("comp_desc", compDec);
                    myData.putString("comp_status", compStatus);
                    myData.putString("comp_type", compType);
                    myData.putInt("comp_ID", compID);
                    myData.putInt("comp_row_ID", Comp_Row_ID);
                    ReOpenComplaintsActivity.putExtras(myData);
                    startActivityForResult(ReOpenComplaintsActivity, ApplicationConstants.REQUEST_EDIT_COMPLAINT);
                   // ViewComplaintsActivity.this.finish();
                }
                catch (Exception ex)
                {

                }
            }
        });
        //nagaraju added

        socUser = Session.GetCurrentSocietyUser(this);

        DataAccess da = new DataAccess(this);
        da.open();
        compAllList = da.getAllComplaint(socUser.ResID);
      //  compArrayList = da.getAllComplaint();
        da.close();

        compListViewAdapter = new MyAdapter(ViewComplaintsActivity.this, 0, compAllList);
        listViewComplaint.setAdapter(compListViewAdapter);
        //compListViewAdapter.notifyDataSetChanged();
       // retrieveView = (View)findViewById(R.id.retreiveView);

        if(Utility.IsConnected(getApplicationContext()) && ApplicationVariable.AUTHENTICATED == true) {
           /* if (compAllList.size() == 0) {
                LoadComplaints(StartIndex, EndIndex,Status);
            } else {
                LoadRecentData();
            }*/
            LoadComplaints(StartIndex, EndIndex,Status);

        }

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_complaints, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


         if(id == R.id.action_resolve)
        {
           // compArrayList.clear();
            //compListViewAdapter.notifyDataSetChanged();
            Status = "Closed";
            StartIndex =0; EndIndex=10;
            LoadComplaints(StartIndex, EndIndex,Status);
           // compListViewAdapter.filter("Closed");
           // compListViewAdapter.notifyDataSetChanged();

        }
        else if(id == R.id.action_Open)
        {
           // compArrayList.clear();
           // compListViewAdapter.notifyDataSetChanged();
            Status = "Open";
            StartIndex =0; EndIndex=10;
            LoadComplaints(StartIndex, EndIndex,Status);
           // compListViewAdapter.filter("Assigned");
           // compListViewAdapter.notifyDataSetChanged();
        }
        else if(id == R.id.action_all)
        {
            //compArrayList.clear();
           // compListViewAdapter.notifyDataSetChanged();
            Status = "All";
            StartIndex =0; EndIndex=10;
            LoadComplaints(StartIndex, EndIndex,Status);
           // compListViewAdapter.filter("All");
           // compListViewAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    // Custom List Adapter Class
    class MyAdapter extends ArrayAdapter<Complaint>
    {
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapter(Context context, int textViewResourceId,
                List<Complaint> objects)
        {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat=LayoutInflater.from(context);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_complaints, null);
                    holder = new ViewHolder();
                    holder.txtViewComplaintId =  convertView.findViewById(R.id.txtViewComplaintId);
                    holder.txtComplaintDate =  convertView.findViewById(R.id.txtComplaintDate);
                    holder.txtComplaintDesc =  convertView.findViewById(R.id.txtComplaintDesc);
                    holder.txtTypeAssigned =  convertView.findViewById(R.id.txtTypeAssigned);
                    holder.txtCompStatus =  convertView.findViewById(R.id.txtCompStatus);
                    holder.txtComplaintType =  convertView.findViewById(R.id.txtComplaintType);
                    holder.statusBar = convertView.findViewById(R.id.statusBar);
                    holder.txtflat = convertView.findViewById(R.id.txtflat);
                    holder.employeeContact = convertView.findViewById(R.id.employeeContact);
                    holder.imageCall = convertView.findViewById(R.id.imageCall);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
//--------------pos +1
                if(position == compAllList.size())
                {
                    if(!IsRetreiving && GetCount>0) {
                        StartIndex = EndIndex;
                        EndIndex = StartIndex + BatchCount;
                        LoadComplaints(StartIndex, EndIndex, Status);
                    }
                }
                else {
                        try {
                          final Complaint row = getItem(position);
                            Date ComplaintDate = Utility.DBStringToLocalDate(row.comp_date);
                            holder.statusBar.setVisibility(View.VISIBLE);
                            holder.txtComplaintDate.setText(Utility.GetDate(row.comp_date));
                            holder.txtCompStatus.setText(row.LastStatus);
                           // holder.txtflat.setText(socUser.FlatNumber);

                            holder.txtflat.setText( Utility.DateToDisplayTimeOnly(ComplaintDate));

                            holder.txtComplaintDesc.setText(row.comp_desc);
                            holder.txtTypeAssigned.setText(" Assigned to: " + row.AssignedTo);
                            holder.txtViewComplaintId.setText("Ticket No: " + Integer.toString(row.comp_id));
                            //holder.complaintAssigned.setText(row.comp_severity);
                            holder.txtComplaintType.setText("Category: " + row.comp_type);
                            if (row.LastStatus.matches("Closed")) {
                                holder.statusBar.setBackgroundColor(Color.rgb(0, 172, 119));
                            } else if (row.LastStatus.matches("Assigned")) {
                                holder.statusBar.setBackgroundColor(Color.rgb(255, 195, 0));
                            } else if (row.LastStatus.matches("New")) {
                                holder.statusBar.setBackgroundColor(Color.rgb(51, 153, 255));
                            }

                            holder.employeeContact.setText(row.employee_contact);
                            holder.imageCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String call = "tel:91-" + row.employee_contact;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(call));
                                    startActivity(i);
                                }
                            });

                        }

                        catch (Exception ex)
                        {
                            Toast.makeText(getApplicationContext(),"Error in creating List View", Toast.LENGTH_LONG).show();
                        }
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in creating List View", Toast.LENGTH_LONG).show();
            }
            return convertView;

        }

        @Override
        public Complaint getItem(int position) {
            // TODO Auto-generated method stub
          /*  if(position >=compAllList.size() )
            {
                Toast.makeText(getApplicationContext(),"Loading Data",Toast.LENGTH_LONG).show();
                return null;
            }
            else
            {
                return compAllList.get(position);
            }*/
          Complaint compItem;

          try{
              compItem = compAllList.get(position);
          }
          catch (Exception ex)
          {
              compItem = new Complaint();
              compItem.comp_id = -999;
              compItem.comp_status="";
          }

            return compItem;
        }

        @Override
        public int getCount() {
            int itemCount;
            try{
                itemCount = compAllList.size();
            }
            catch (Exception ex)
            {
                itemCount = 0;
            }

         /*   if(BatchCount==GetCount)
            {
                return compAllList.size();
            }
            else
            {
                return compAllList.size();
            }*/
            return itemCount;
        }

        private class ViewHolder
        {
            TextView txtViewComplaintId,txtComplaintDate,txtComplaintDesc,txtTypeAssigned,txtCompStatus,txtComplaintType,txtflat,employeeContact ;
            View statusBar;
            ImageView imageCall;
        }
    }


    private void LoadComplaints(final int firstIndex, final int lastIndex, final String CompStatus)
    {
        IsRetreiving = true;
        noData.setVisibility(View.GONE);
        progBarComplaint.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/Complaint/Get/" + Status + "/"
                + socUser.SocietyId + "/" + socUser.FlatNumber + "/" + PageNumber +"/"+  Count;

    /*    String reqBody = "{\"StartIndex\":\""+ firstIndex+ "\",\"EndIndex\":\""+ lastIndex+ "\",\"FlatNumber\":\""+ socUser.FlatNumber+ "\",\"SocietyID\":\""+ socUser.SocietyId +"\",\"CompStatus\":\""+ CompStatus +"\",\"LastRefreshTime\":\"\"}";
        JSONObject jsRequest=null;

        try {
            jsRequest = new JSONObject(reqBody);
        }
        catch (JSONException jex)
        {
        }*/
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jArray) {

                try{

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                     GetCount = jArray.length();

                    if(PageNumber==1 && GetCount>0)
                    {
                        compAllList.clear();
                    }
                    for(int i = 0; i< GetCount; i++){
                        JSONObject jObj = jArray.getJSONObject(i);
                        eachCompRow=new Complaint();
                        eachCompRow.FirstID = jObj.getInt("FirstID");
                        eachCompRow.comp_id=jObj.getInt("CompID");
                        eachCompRow.comp_date=jObj.getString("InitiatedAt");
                        eachCompRow.comp_desc=jObj.getString("InitialComment");
                        eachCompRow.AssignedTo=jObj.getString("EmployeeName");
                        eachCompRow.Age =jObj.getInt("Age");
                        eachCompRow.StatusCount=jObj.getInt("StatusCount");
                        eachCompRow.LastStatus=jObj.getString("LastStatus");
                        eachCompRow.comp_type=jObj.getString("CompType");
                        eachCompRow.LatestComment=jObj.getString("LastComment");
                        eachCompRow.comp_severity="Medium";
                        eachCompRow.comp_status = "Initiated";
                        eachCompRow.employee_contact = jObj.getString("EmployeeContact");

                        if(CompStatus.matches("Open") && firstIndex ==0) {
                            da.insertNewComplaint(eachCompRow, socUser.ResID);
                        }

                         compAllList.add(firstIndex+i,eachCompRow);
                    }


                    if(CompStatus.matches("Open")&&firstIndex ==0) {
                        da.LimitComplaintData();
                         //compAllList = da.getAllComplaint();
                        //compArrayList = da.getAllComplaint();
                        //Session.SetComplaintRefreshTime(getApplicationContext());
                    }
                    da.close();
                    IsRetreiving = false;
                    progBarComplaint.setVisibility(View.GONE);

                    if(compAllList.size()==0)
                    {
                        noData.setVisibility(View.VISIBLE);
                        noData.setText(" No Data for " +Status + " Complaints");
                    }
                    else if (GetCount>0)
                    {
                        noData.setVisibility(View.GONE);
                        //ApplicationConstants.COMPLAINT_UPDATES=0;
                        compListViewAdapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException je)
                {
                    IsRetreiving = false;
                    progBarComplaint.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    noData.setText(" Error Reading Data, contact support");
                }
                catch (Exception ex)
                {
                    IsRetreiving = false;
                    progBarComplaint.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    noData.setText(" Error Reading Data, contact support");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                IsRetreiving = false;
                progBarComplaint.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
                noData.setText(" Network Error, Try again");
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);

        queue.add(jsArrayRequest);
        //*******************************************************************************************************
    }


    @Override
    public void OnSummaryReceived() {
        if(ApplicationConstants.COMPLAINT_UPDATES!=0)
        {
            StartIndex=0;

            if(ApplicationConstants.COMPLAINT_UPDATES>BatchCount)
            {

                EndIndex = StartIndex + BatchCount;
            }
            else
            {
                EndIndex = StartIndex + ApplicationConstants.COMPLAINT_UPDATES;
            }
            LoadComplaints(StartIndex, EndIndex,Status);

            StartIndex=EndIndex;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                Intent compActivity = new Intent(ViewComplaintsActivity.this,DashboardActivity.class);
                compActivity.putExtra("PARENT","COMPLAINTS");
                startActivity(compActivity);
                ViewComplaintsActivity.this.finish();
            }
            catch (Exception ex)
            {

            }
     }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
        {
            LoadComplaints(0, 10, Status);
        }
    }

    private void LoadRecentData()
   {
       IsRetreiving = true;
      // retrieveView.setVisibility(View.VISIBLE);
      // ShowSnackBar("Checking new data","");
       String url = ApplicationConstants.APP_SERVER_URL +"/api/ComplaintDiff" ;
       LastComplaintRefreshTime = Session.GetComplaintRefreshTime(getApplicationContext());
       String reqBody = "{\"ResId\":\""+ socUser.ResID +"\",\"LastRefreshTime\":\""+ LastComplaintRefreshTime +"\"}";
       JSONObject jsRequest=null;

       try {
           jsRequest = new JSONObject(reqBody);
       }
       catch (JSONException jex)
       {}

       //-------------------------------------------------------------------------------------------------
       RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
       JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject jArray) {

               try{
                   JSONArray json = jArray.getJSONArray("$values");

                  int count = json.length();
                  if(count>0) {
                      DataAccess da = new DataAccess(getApplicationContext());
                      da.open();
                      for (int i = 0; i < count; i++) {
                          JSONObject jObj = json.getJSONObject(i);
                          eachCompRow = new Complaint();
                          eachCompRow.FirstID = jObj.getInt("FirstID");
                          eachCompRow.comp_id = jObj.getInt("CompID");
                          eachCompRow.comp_date = jObj.getString("InitiatedAt");
                          eachCompRow.comp_desc = jObj.getString("InitialComment");
                          eachCompRow.Age = jObj.getInt("Age");
                          eachCompRow.StatusCount = jObj.getInt("StatusCount");
                          eachCompRow.LastStatus = jObj.getString("LastStatus");
                          eachCompRow.comp_type = jObj.getString("CompType");
                          eachCompRow.LatestComment = jObj.getString("LastComment");
                          eachCompRow.comp_severity = "Medium";
                          eachCompRow.comp_status = "Initiated";
                          if(da.checkComplaintExist(eachCompRow.comp_id))
                          {
                              da.deleteComplaint(eachCompRow.comp_id);
                          }
                          da.insertNewComplaint(eachCompRow, socUser.ResID);
                          //compArrayList.add(i, eachCompRow);
                          compAllList.add(i, eachCompRow);
                      }
                      if (count > 0) {
                          da.LimitComplaintData();
                          // compList = da.getAllComplaint();
                          // compArrayList = da.getAllComplaint();
                          Session.SetComplaintRefreshTime(getApplicationContext());
                          ApplicationConstants.COMPLAINT_UPDATES = 0;
                          compListViewAdapter.notifyDataSetChanged();
                      }
                      da.close();
                  }
                   IsRetreiving = false;
                  // retrieveView.setVisibility(View.GONE);
                   // HideSnackBar();
               }
               catch (JSONException e)
               {
                   //retrieveView.setVisibility(View.GONE);
                  // HideSnackBar();
                   IsRetreiving = false;
                   int a=1;
               }
               catch (Exception ex)
               {
                   IsRetreiving = false;
                   int a=1;
               }

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {


               //retrieveView.setVisibility(View.GONE);
            //   ShowSnackBar("Could not refresh", "Try Again");
           }
       });

       RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

       jsArrayRequest.setRetryPolicy(rPolicy);

       queue.add(jsArrayRequest);
       //*******************************************************************************************************
   }

    private void ShowSnackBar( String htmlString, String buttonText)
    {

        snackbar = Snackbar
                .make(listViewComplaint, htmlString, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
      //  snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {

            }
        });

        snackbar.setAction(buttonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  LoadRecentData();
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

}
