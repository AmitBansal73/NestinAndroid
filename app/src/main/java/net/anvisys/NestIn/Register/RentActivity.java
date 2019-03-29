package net.anvisys.NestIn.Register;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Object.Forum;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RentActivity extends AppCompatActivity {

    Spinner spRentType,spRoomType;
    EditText txtCity,txtRent;
    Button btnSearch;
    ProgressBar progressBar;
    SocietyUser socUser;
    RentData rentInvent;
    ArrayList<RentData> arraylistRent=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" NestIn ");
        actionBar.show();

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        spRentType = findViewById(R.id.spRentType);
        spRoomType = findViewById(R.id.spRoomType);
        txtCity = findViewById(R.id.txtCity);
        txtRent = findViewById(R.id.txtCity);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadRentData();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rentType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRentType.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.roomType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomType.setAdapter(adapter1);

    }
    public void LoadRentData(){
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/" + socUser.SocietyId;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");

                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jObj = json.getJSONObject(i);
                            rentInvent = new RentData();
                            rentInvent.Inventory = jObj.getString("Inventory");
                            rentInvent.BHK = jObj.getString("BHK");
                            rentInvent.FlatNumber = jObj.getString("FlatNumber");
                            rentInvent.ContactName = jObj.getString("ContactName");
                            rentInvent.ContactNumber = jObj.getString("ContactNumber");
                            rentInvent.Description = jObj.getString("Description");
                            rentInvent.RentType = jObj.getString("RentType");
                            rentInvent.RentValue = jObj.getInt("RentValue");
                            rentInvent.SocietyName = jObj.getString("SocietyName");
                            rentInvent.InventoryID = jObj.getInt("InventoryID");
                            rentInvent.RentInventoryID = jObj.getInt("RentInventoryID");
                        }
                       // adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);




        }catch (Exception ex){}
    }



    private class RentData
    {
     int RentInventoryID, InventoryID,RentValue;
     String Inventory, RentType,Description,ContactName,ContactNumber,FlatNumber,BHK,SocietyName;

    }
    class MyAdapterFlats extends ArrayAdapter<RentData>{
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterFlats(Context context, int resource, int textViewResourceId, ArrayList<RentData> objects) {

            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arraylistRent.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_flatlist, null);
                    holder = new ViewHolder();
                   // holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                  //  holder.txtIntercom = convertView.findViewById(R.id.txtIntercom);
                    convertView.setTag(holder);
                }
                RentData row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
               // holder.txtFlatNumber.setText(row.FlatNumber);
                //  holder.txtFloor.setText(row.Floor);
                //  holder.txtBlock.setText(row.Block);
                //  holder.txtArea.setText(row.FlatArea);
                // holder.txtIntercom.setText(row.IntercomNumber);
                return convertView;
            }

            catch (Exception ex)

            {
                Toast.makeText(getApplicationContext(),"Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public int getPosition(RentData item) {
            return super.getPosition(item);
        }

        @Override
        public RentData getItem(int position) {
            return arraylistRent .get(position);
        }
    }
    private class ViewHolder
    {
        TextView txt ;
    }
}
