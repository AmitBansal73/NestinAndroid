package net.anvisys.NestIn.Rent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Model.Rent;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Register.SettingActivity;

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
    AdapterRent adapterRent;
    EditText txtInterest;
    ArrayList<Rent> arraylistRent=new ArrayList<>();
    NumberFormat currFormat;


    TextView txtMessage;

    int PageNumber = 1;
    int Count =10;

    View viewInterest;
    Button btnSubmitInterest,btnClose;
    Rent selectedRent;

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
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        txtMessage = findViewById(R.id.txtMessage);
        btnSubmitInterest = findViewById(R.id.btnSubmitInterest);
        btnSubmitInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddInterest();
            }
        });
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInterest.setVisibility(View.GONE);
            }
        });
        txtInterest = findViewById(R.id.txtInterest);
        viewInterest = findViewById(R.id.viewInterest);
        viewInterest.setVisibility(View.GONE);

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
        rentListView= findViewById(R.id.rentListView);
        adapterRent =new AdapterRent(RentActivity.this,0,arraylistRent);
        rentListView.setAdapter(adapterRent);
        LoadRentData();
    }

    public void LoadRentData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/" + socUser.SocietyId + "/" + PageNumber + "/" + Count;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray json) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if(PageNumber ==1)
                        {
                            arraylistRent.clear();
                        }
                      int x = json.length();
                        if (x == 0) {
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText("No Data available for Rent !");
                        } else
                        {
                            txtMessage.setVisibility(View.GONE);
                            for (int i = 0; i < x; i++) {
                                JSONObject jObj = json.getJSONObject(i);
                                rentInvent = new Rent();
                                rentInvent.RentInventoryID = jObj.getInt("RentInventoryID");
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

                                rentInvent.sector = jObj.getString("sector");
                                rentInvent.Floor = jObj.getInt("Floor");
                                rentInvent.FlatCity = jObj.getString("FlatCity");
                                rentInvent.InterestedCount = jObj.getInt("InterestedCount");
                                arraylistRent.add(rentInvent);
                            }

                        adapterRent.notifyDataSetChanged();
                    }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText("Error Reading Data !");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.VISIBLE);
                    txtMessage.setText("Server Error !");
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception ex){
            progressBar.setVisibility(View.GONE);
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText("Server Error !");
        }
    }

    class AdapterRent extends ArrayAdapter<Rent>{
        LayoutInflater inflat;
        ViewHolder holder;
        public AdapterRent(Context context, int resource, ArrayList<Rent> objects) {

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
                    convertView = inflat.inflate(R.layout.row_item_rent, null);
                    holder = new ViewHolder();
                    holder.txtInventory = convertView.findViewById(R.id.txtInventory);
                    holder.txtRentType = convertView.findViewById(R.id.txtRentType);
                    holder.txtDescription = convertView.findViewById(R.id.txtDescription);
                    holder.txtContactNumber = convertView.findViewById(R.id.txtContactNumber);
                    holder.txtContactName = convertView.findViewById(R.id.txtContactName);
                    holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                    holder.txtFloor = convertView.findViewById(R.id.txtFloor);
                   // holder.txtBHK = convertView.findViewById(R.id.txtBHK);
                    holder.txtRentValue = convertView.findViewById(R.id.txtRentValue);
                    holder.txtSector = convertView.findViewById(R.id.txtSector);
                    holder.txtComment = convertView.findViewById(R.id.txtComment);
                    holder.imageCall = convertView.findViewById(R.id.imageCall);

                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
               final Rent row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtInventory.setText(" "+row.Inventory);
                holder.txtRentType.setText(" "+row.RentType);
                holder.txtDescription.setText(row.Description);
                holder.txtContactNumber.setText(row.ContactNumber);
                holder.txtContactName.setText(row.ContactName);
                holder.txtFlatNumber.setText("  "+row.FlatNumber);
                holder.txtFloor.setText("  "+row.Floor);
               // holder.txtBHK.setText("  "+row.BHK);
                holder.txtRentValue.setText(" "+currFormat.format(row.RentValue));
                holder.imageCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String call = "tel:91-" + row.ContactNumber;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(call));
                        startActivity(i);
                    }
                });

                holder.txtComment.setText(row.InterestedCount + " Interested");
                holder.txtComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedRent = row;
                        viewInterest.setVisibility(View.VISIBLE);
                    }
                });

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Could not Load RentData", Toast.LENGTH_LONG).show();

            }
            return convertView;
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
        //txtBHK,
        TextView txtInventory,txtRentType,txtDescription,txtContactNumber,txtContactName,txtFlatNumber,txtFloor,txtRentValue,txtComment,txtSector;

        ImageView imageCall;
    }

    public void AddInterest(){
        progressBar.setVisibility(View.VISIBLE);
        String strInterest = txtInterest.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/Add/Interest";
        try {
            String reqBody = "{\"InventoryID\":\""+ selectedRent.RentInventoryID +"\",\"InterestedUserId\":\"" + socUser.ResID
                    + "\",\"DealStatus\":\"" + 1 + "\",\"Comments\":\"" + strInterest +"\"}";;
            JSONObject jsRequest = new JSONObject(reqBody);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest  jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").matches("Ok")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Interest Added Successfully.", Toast.LENGTH_SHORT).show();

                            viewInterest.setVisibility(View.GONE);
                            LoadRentData();

                        }
                        else if(response.getString("Response").matches("Duplicate")){
                            Toast.makeText(getApplicationContext(), "Interest Already Submitted", Toast.LENGTH_SHORT).show();

                        }
                        else if(response.getString("Response").matches("Fail")){
                            Toast.makeText(getApplicationContext(), " Failed ", Toast.LENGTH_SHORT).show();

                        }

                    }catch (Exception e){
                       int  a=1;
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), " Error Reading Response ", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    Toast.makeText(getApplicationContext(), " Server Error ", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception Ex){
            Toast.makeText(getApplicationContext(), " Server Error ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_myRent) {
            Intent profileIntent = new Intent(RentActivity.this, MyRentActivity.class);
            startActivity(profileIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
