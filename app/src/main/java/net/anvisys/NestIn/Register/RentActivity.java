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
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RentActivity extends AppCompatActivity {
    ProgressBar progressBar;
    SocietyUser socUser;
    RentData rentInvent;
    ListView rentListView;
    MyAdapterRent adapterRent;
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
        rentListView.findViewById(R.id.rentListView);
        adapterRent =new MyAdapterRent(RentActivity.this,0,arraylistRent);
        rentListView.setAdapter(adapterRent);

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        LoadRentData();
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
                            rentInvent.sector = jObj.getString("sector");
                            rentInvent.Floor = jObj.getInt("Floor");
                            rentInvent.FlatCity = jObj.getString("FlatCity");
                            arraylistRent.add(rentInvent);
                        }
                        progressBar.setVisibility(View.GONE);
                        adapterRent.notifyDataSetChanged();
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
     int RentInventoryID, InventoryID,RentValue,Floor;
     String Inventory, RentType,Description,ContactName,ContactNumber,FlatNumber,FlatCity,BHK,SocietyName,sector;

    }
    class MyAdapterRent extends ArrayAdapter<RentData>{
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterRent(Context context, int resource, ArrayList<RentData> objects) {

            super(context, resource, objects);
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
                    holder.txtInventory = convertView.findViewById(R.id.txtInventory);
                    holder.txtRentType = convertView.findViewById(R.id.txtRentType);
                    holder.txtDescription = convertView.findViewById(R.id.txtDescription);
                    holder.txtContactNumber = convertView.findViewById(R.id.txtContactNumber);
                    holder.txtContactName = convertView.findViewById(R.id.txtContactName);
                    holder.txtSocietyName = convertView.findViewById(R.id.txtSocietyName);
                    holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                    holder.txtFloor = convertView.findViewById(R.id.txtFloor);
                    holder.txtRentValue = convertView.findViewById(R.id.txtRentValue);
                    holder.txtFlatCity = convertView.findViewById(R.id.txtFlatCity);
                    holder.txtSector = convertView.findViewById(R.id.txtSector);
                    convertView.setTag(holder);
                }
                RentData row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtInventory.setText("Inventory: "+row.Inventory);
                holder.txtRentType.setText("RentType: "+row.RentType);
                holder.txtDescription.setText(" "+row.Description);
                holder.txtContactNumber.setText("ContactNumber: "+row.ContactNumber);
                holder.txtContactName.setText("ContactName: "+row.ContactName);
                holder.txtSocietyName.setText("Society : "+row.SocietyName);
                holder.txtFlatNumber.setText("Flat: "+row.FlatNumber);
                holder.txtFloor.setText("Floor: "+row.Floor);
                holder.txtRentValue.setText("RentValue: "+row.RentValue);
                holder.txtFlatCity.setText("FlatCity: "+row.FlatCity);
                holder.txtSector.setText("Sector: "+row.sector);
                return convertView;
            }

            catch (Exception ex)

            {
                Toast.makeText(getApplicationContext(),"Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public RentData getItem(int position) {
            // TODO Auto-generated method stub
            return arraylistRent.get(position);
        }

        @Override
        public int getPosition(RentData item) {
            return super.getPosition(item);
        }

    }
    private class ViewHolder
    {
        TextView txtInventory,txtRentType,txtDescription,txtContactNumber,txtContactName,txtSocietyName,txtFlatNumber,txtFloor,txtRentValue,txtFlatCity,txtSector;

    }
}
