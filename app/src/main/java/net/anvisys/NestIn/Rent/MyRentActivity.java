package net.anvisys.NestIn.Rent;

import android.content.Context;
import android.content.Intent;

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
import android.widget.Button;
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
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Model.Rent;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class MyRentActivity extends AppCompatActivity {

    FloatingActionButton fabMyRent;
    ListView listViewMyRent;
    ProgressBar progressBar;
    TextView txtMessage;
    int PageNumber =1;
    int Count =10;
    SocietyUser socUser;
    NumberFormat currFormat;

    Rent selectedRent;

    ArrayList<Rent> arrayListMyRent = new ArrayList<>();
    MyAdapterRent adapterRent;

    View viewComment;
    TextView txtComment;
    Button btnSubmit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rent);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("My Rent Inventory");
        actionBar.show();

        viewComment = findViewById(R.id.viewComment);
        txtComment = findViewById(R.id.txtComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        fabMyRent = findViewById(R.id.fabMyRent);
        listViewMyRent = findViewById(R.id.listViewMyRent);
        progressBar = findViewById(R.id.progressBar);
        txtMessage = findViewById(R.id.txtMessage);
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewComment.setVisibility(View.GONE);
                txtComment.setText("");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteRentInventory();
            }
        });

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        fabMyRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MyRentActivity.this, AddRentActivity.class);
                startActivityForResult(newIntent, ApplicationConstants.REQUEST_ADD_RENT);
            }
        });

        adapterRent =new MyAdapterRent(MyRentActivity.this,0,arrayListMyRent);
        listViewMyRent.setAdapter(adapterRent);

        LoadMyRentData();
    }

    public void LoadMyRentData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/Find/" + socUser.FlatID + "/0";
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray json) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        arrayListMyRent.clear();
                        int x = json.length();
                        if (x == 0) {
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText("No Data available for Rent !");
                        } else
                        {
                            txtMessage.setVisibility(View.GONE);
                            for (int i = 0; i < x; i++) {
                                JSONObject jObj = json.getJSONObject(i);
                               Rent rentInvent = new Rent();
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
                                rentInvent.InterestedCount = jObj.getInt("InterestedCount");
                                arrayListMyRent.add(rentInvent);
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
            int a=1;
        }
    }


    class MyAdapterRent extends ArrayAdapter<Rent> {
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterRent(Context context, int resource, ArrayList<Rent> objects) {

            super(context, resource, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayListMyRent.size();
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
                    holder.txtClose = convertView.findViewById(R.id.txtClose);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                holder.imageCall.setVisibility(View.GONE);

                holder.txtClose.setVisibility(View.VISIBLE);

                final Rent row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtInventory.setText(" "+row.Inventory);
                holder.txtRentType.setText(" "+row.RentType);
                holder.txtDescription.setText(row.Description);
                holder.txtContactNumber.setText(row.ContactNumber);
                holder.txtContactName.setText(row.ContactName);
                holder.txtFlatNumber.setText("  "+row.FlatNumber);
                holder.txtFloor.setText("  "+row.Floor);
                holder.txtComment.setText(row.InterestedCount + " Interested");
                // holder.txtBHK.setText("  "+row.BHK);
                holder.txtRentValue.setText(" "+currFormat.format(row.RentValue));

                holder.txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedRent = row;
                        viewComment.setVisibility(View.VISIBLE);
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
            return arrayListMyRent.get(position);
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
        TextView txtClose;
    }


    private  void CompleteRentInventory(){

        String comment = txtComment.getText().toString();
        int RentItemID = selectedRent.RentInventoryID;
        txtMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/RentInventory/Close";

        String reqBody = "{\"InventoryId\":\""+ RentItemID +"\",\"AccomodationTypeID\":\"\",\"RentValue\":\""
                + "\",\"Available\":\"false" +  "\",\"Description\":\""+ comment +"\",\"ContactNumber\":\"\",\"ContactName\":\""
                +"\",\"UserID\":\"\",\"FlatID\":\""+ socUser.FlatID +"\",\"HouseID\":\"0\"}";

        try{
            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResult) {
                    viewComment.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        String Response = jsonResult.getString("Response");

                        if(Response.equalsIgnoreCase("Ok"))
                        {
                            Toast.makeText(getApplicationContext(),"Closed Successfully", Toast.LENGTH_LONG).show();
                            LoadMyRentData();
                        }
                        else if(Response.equalsIgnoreCase("Fail"))
                        {
                            Toast.makeText(getApplicationContext(),"Failed to complete", Toast.LENGTH_LONG).show();
                        }
                        else if(Response.equalsIgnoreCase("NotExist"))
                        {
                            Toast.makeText(getApplicationContext(),"Data do not exist", Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Error Reading Data !", Toast.LENGTH_LONG).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Server Error!", Toast.LENGTH_LONG).show();

                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
        }catch (Exception ex){
            int a=1;
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
        {
            LoadMyRentData();
        }
    }
}
