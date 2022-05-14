package net.anvisys.NestIn.Guest;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.Volley;
import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.Guest;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

public class VisitorActivity extends AppCompatActivity {

    ArrayList<Guest> guestList = new ArrayList<Guest>();
    ProgressBar prgBar;
    MyAdapter adapter;
    ListView guestListView;
    Profile myProfile;
    SocietyUser socUser;
    FloatingActionButton fab;

    int PageNumber = 1;
    int Count =10;

    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Visitor");
        actionBar.show();

        txtMessage = findViewById(R.id.txtMessage);
        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        prgBar=findViewById(R.id.progBar);
        prgBar.setVisibility(View.GONE);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addGuest = new Intent(VisitorActivity.this, AddVisitorActivity.class);
                startActivityForResult(addGuest, ApplicationConstants.REQUEST_ADD_Visitor);
            }
        });

        guestListView = findViewById(R.id.guestListView);
        adapter =new MyAdapter(VisitorActivity.this,0,guestList);
        guestListView.setAdapter(adapter);
        GetGuestData();
    }

    private void GetGuestData()
    {
        txtMessage.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/visitor/" +socUser.SocietyId+ "/Res/"+socUser.ResID + "/" + PageNumber + "/" + Count;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jResult) {
                prgBar.setVisibility(View.GONE);
                try{

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int GetCount = jResult.length();
                    if(GetCount>0)
                    {
                        guestList.clear();

                            Guest guest;
                            for (int i = 0; i < GetCount; i++) {
                                JSONObject jObj = jResult.getJSONObject(i);
                                guest = new Guest();
                                guest.VisitorName = jObj.getString("VisitorName");
                                guest.VisitorMobile = jObj.getString("VisitorMobile");
                                guest.VisitorAddress = jObj.getString("VisitorAddress");
                                guest.VisitPurpose = jObj.getString("VisitPurpose");
                                guest.FlatNumber = jObj.getString("Flat");
                                guest.ActualInTime = jObj.getString("ActualInTime");
                                guest.ExpectedEndTime = jObj.getString("EndTime");
                                guestList.add(guest);
                            }

                            adapter.notifyDataSetChanged();

                        }
                        else
                        {
                            if(guestList.size()==0)
                            {
                                txtMessage.setVisibility(View.VISIBLE);
                                txtMessage.setText("No Data found!");
                            }
                        }
                }
                catch (JSONException e)
                {
                    if(guestList.size()==0)
                    {
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText("Error Reading Data!");
                    }
                    prgBar.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    if(guestList.size()==0)
                    {
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText("Error Reading Data!");
                    }
                    prgBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(guestList.size()==0)
                {
                    txtMessage.setVisibility(View.VISIBLE);
                    txtMessage.setText("Server Error!");
                }
                prgBar.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************

    }

    private class ViewHolder
    {
        TextView txtName,txtMobile,txtAddress,txtFlat,txtActualInTime,txtStatus;
        View statusBar;

    }

    class MyAdapter extends ArrayAdapter<Guest> {
        LayoutInflater inflat;
        ViewHolder holder;

        public MyAdapter(@NonNull Context context, int resource, ArrayList<Guest> objects) {
            super(context, resource,objects);
              inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return guestList.size();
        }

        @Nullable
        @Override
        public Guest getItem(int position) {
            return guestList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try {

                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_visitor, null);
                    holder = new ViewHolder();
                    holder.txtName = convertView.findViewById(R.id.txtName);
                    holder.txtMobile = convertView.findViewById(R.id.txtMobile);
                    holder.txtAddress = convertView.findViewById(R.id.txtAddress);
                    holder.txtFlat = convertView.findViewById(R.id.txtFlat);
                    holder.txtActualInTime = convertView.findViewById(R.id.txtActualInTime);
                    holder.txtStatus = convertView.findViewById(R.id.txtStatus);
                    holder.statusBar = convertView.findViewById(R.id.statusBar);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                Guest row = getItem(position);

                holder.txtName.setText(row.VisitorName );
                holder.txtMobile.setText(row.VisitorMobile);
                holder.txtAddress.setText(row.VisitorAddress );
                holder.txtFlat.setText("Flat: "+row.FlatNumber);
                String status="";


                Date InTime = Utility.DBStringToLocalDate(row.ActualInTime);
                Date ExpectedDateTime = Utility.DBStringToLocalDate(row.ExpectedEndTime);

                Date today = new Date();
                boolean expired = today.after(ExpectedDateTime);


                if((Utility.GetYearOnly(row.ActualInTime)<2000) &&  expired)
                {
                    status = "Expired";
                    holder.txtStatus.setText(status);
                    holder.txtStatus.setBackgroundResource(R.drawable.background_red);
                    holder.txtActualInTime.setText("Last Time: " +Utility.DateToDisplayDateTime(ExpectedDateTime));
                   // holder.statusBar.setBackgroundColor(Color.rgb(209,69,69));
                }
                else if((Utility.GetYearOnly(row.ActualInTime)<2000) &&  !expired)
                {
                    status = "Pending";
                    holder.txtStatus.setText(status);
                    holder.txtStatus.setBackgroundResource(R.drawable.background_yellow);
                    holder.txtActualInTime.setText("Expected Before: " + Utility.DateToDisplayDateTime(ExpectedDateTime));
                   // holder.statusBar.setBackgroundColor(Color.rgb(209,69,69));
                }
                else if (Utility.GetYearOnly(row.ActualInTime)>2018)
                {
                    status = "Done";
                    holder.txtStatus.setText(status);
                    holder.txtStatus.setBackgroundResource(R.drawable.background_green);
                    holder.txtActualInTime.setText("In Time: "+ Utility.DateToDisplayDateTime(InTime));
                  //  holder.statusBar.setBackgroundColor(Color.rgb(0,127,58));
                }
                return convertView;
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Could not Load Guest Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == ApplicationConstants.REQUEST_ADD_Visitor)
        {
            if(resultCode == RESULT_OK) {
                GetGuestData();
            }
        }

    }
}
