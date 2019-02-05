package net.anvisys.NestIn.Guest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Object.Guest;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
public class GuestActivity extends AppCompatActivity {

    ArrayList<Guest> guestList = new ArrayList<Guest>();
    ProgressBar prgBar;
    MyAdapter adapter;
    ListView guestListView;
    Profile myProfile;
    SocietyUser socUser;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Visitor List");
        actionBar.show();
        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        prgBar=findViewById(R.id.progBar);
        prgBar.setVisibility(View.GONE);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addGuest = new Intent(GuestActivity.this,GuestCodeActivity.class);
                startActivity(addGuest);
            }
        });

        guestListView = findViewById(R.id.guestListView);
        adapter =new MyAdapter(GuestActivity.this,0,guestList);
        guestListView.setAdapter(adapter);
        GetGuestData();
    }

    private void GetGuestData()
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/visitor/" +socUser.SocietyId+ "/Res/"+socUser.ResID;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{
                    JSONArray json = jObject.getJSONArray("$values");
                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int GetCount = json.length();
                    Guest guest;
                    for (int i = 0; i < GetCount; i++) {
                        JSONObject jObj = json.getJSONObject(i);
                        guest = new Guest();
                        guest.VisitorName = jObj.getString("VisitorName");
                        guest.VisitorMobile = jObj.getString("VisitorMobile");
                        guest.VisitorAddress = jObj.getString("VisitorAddress");
                        guest.VisitPurpose = jObj.getString("VisitPurpose");
                        guest.FlatNumber = jObj.getString("Flat");
                        guest.ActualInTime = jObj.getString("ActualInTime");
                        guestList.add(guest);
                    }
                    adapter.notifyDataSetChanged();
                    prgBar.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    prgBar.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    prgBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                    convertView = inflat.inflate(R.layout.row_guest_list, null);
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
                if(Utility.GetYearOnly(row.ActualInTime)<2000)
                {
                    status = "Pending";
                    holder.txtStatus.setText(status);
                    holder.txtStatus.setBackgroundResource(R.drawable.background_red);
                    holder.txtActualInTime.setText("InTime:  --,-- ");
                    holder.statusBar.setBackgroundColor(Color.rgb(209,69,69));
                }
                else
                {
                    status = "Done";
                    holder.txtStatus.setText(status);
                    holder.txtStatus.setBackgroundResource(R.drawable.background_card_green);
                    holder.txtActualInTime.setText("InTime: "+Utility.ChangeToMonthDisplayFormat(row.ActualInTime));
                    holder.statusBar.setBackgroundColor(Color.rgb(0,127,58));
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

}
