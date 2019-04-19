package net.anvisys.NestIn.Register;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
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

import net.anvisys.NestIn.AddRentActivity;
import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Object.Rent;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class RentActivity extends AppCompatActivity {
    ProgressBar progressBar;
    SocietyUser socUser;
    Rent rentInvent;
    ListView rentListView;
    MyAdapterRent adapterRent;
    EditText txtInterest;
    ArrayList<Rent> arraylistRent=new ArrayList<>();
    NumberFormat currFormat;
    LinearLayout comment;
    Button btnSubmitComment,btnClose;

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
        actionBar.setTitle(" Rent Inventory ");
        actionBar.show();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddInterest();
            }
        });
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setVisibility(View.GONE);
            }
        });
        txtInterest = findViewById(R.id.txtInterest);
        comment = findViewById(R.id.comment);
        comment.setVisibility(View.GONE);
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));
        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
        rentListView= findViewById(R.id.rentListView);
        adapterRent =new MyAdapterRent(RentActivity.this,0,arraylistRent);
        rentListView.setAdapter(adapterRent);
        LoadRentData();
    }

    public void LoadRentData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/" + socUser.SocietyId;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");
                        int x = json.length();

                        for (int i = 0; i <x; i++) {
                            JSONObject jObj = json.getJSONObject(i);
                            rentInvent = new Rent();
                            rentInvent.Inventory = jObj.getString("InventoryType");
                            rentInvent.BHK = jObj.getString("BHK");
                            rentInvent.FlatNumber = jObj.getString("FlatNumber");
                            rentInvent.ContactName = jObj.getString("ContactName");
                            rentInvent.ContactNumber = jObj.getString("ContactNumber");
                            rentInvent.Description = jObj.getString("Description");
                            rentInvent.RentType = jObj.getString("AccomodationType");
                            rentInvent.RentValue = jObj.getInt("RentValue");
                            rentInvent.SocietyName = jObj.getString("SocietyName");
                            rentInvent.InventoryID = jObj.getInt("InventoryTypeID");
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
        }catch (Exception ex){
            int a=1;
        }
    }

    class MyAdapterRent extends ArrayAdapter<Rent>{
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterRent(Context context, int resource, ArrayList<Rent> objects) {

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
                    convertView = inflat.inflate(R.layout.row_listrent, null);
                    holder = new ViewHolder();
                    holder.txtInventory = convertView.findViewById(R.id.txtInventory);
                    holder.txtRentType = convertView.findViewById(R.id.txtRentType);
                    holder.txtDescription = convertView.findViewById(R.id.txtDescription);
                    holder.txtContactNumber = convertView.findViewById(R.id.txtContactNumber);
                    holder.txtContactName = convertView.findViewById(R.id.txtContactName);
                    holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                    holder.txtFloor = convertView.findViewById(R.id.txtFloor);
                    holder.txtBHK = convertView.findViewById(R.id.txtBHK);
                    holder.txtRentValue = convertView.findViewById(R.id.txtRentValue);
                    holder.txtSector = convertView.findViewById(R.id.txtSector);
                    holder.txtComment = convertView.findViewById(R.id.txtComment);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                Rent row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtInventory.setText(" "+row.Inventory);
                holder.txtRentType.setText(" "+row.RentType);
                holder.txtDescription.setText(row.Description);
                holder.txtContactNumber.setText(row.ContactNumber);
                holder.txtContactName.setText(row.ContactName);
                holder.txtFlatNumber.setText("  "+row.FlatNumber);
                holder.txtFloor.setText("  "+row.Floor);
                holder.txtBHK.setText("  "+row.BHK);
                holder.txtRentValue.setText(" "+currFormat.format(row.RentValue));
                holder.txtComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comment.setVisibility(View.VISIBLE);
                    }
                });
                return convertView;
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Could not Load RentData", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public Rent getItem(int position) {
            // TODO Auto-generated method stub
            return arraylistRent.get(position);
        }

        @Override
        public int getPosition(Rent item) {
            return super.getPosition(item);
        }

    }
    private class ViewHolder
    {
        TextView txtInventory,txtRentType,txtDescription,txtContactNumber,txtContactName,txtBHK,txtFlatNumber,txtFloor,txtRentValue,txtComment,txtSector;
    }

    public void AddInterest(){
        progressBar.setVisibility(View.VISIBLE);
        String strInterest = txtInterest.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/Add/Interest";
        try {
            String reqBody = "{\"Interest\":\""+ strInterest +"\"}";;
            JSONObject jsRequest = new JSONObject(reqBody);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest  jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").matches("Ok")) {
                            Toast.makeText(getApplicationContext(), "Interest Added Successfully.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            comment.setVisibility(View.GONE);
                        }else if(response.getString("Response").matches("Fail")){}
                        Toast.makeText(getApplicationContext(), " Failed ", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                       int  a=1;
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    progressBar.setVisibility(View.GONE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception Ex){

        }
    }

}
