package net.anvisys.NestIn.CarPool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.CarPool;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MyCarPoolActivity extends AppCompatActivity {

    ListView listViewMyCarPool;
    TextView txtMessage;
    FloatingActionButton fabMyPool;
    SocietyUser socUser;
    int PageNumber =1;
    int Count = 10;

    ProgressBar progressBar;
    AdapterMyCarPool adapterCarPool;
    ArrayList<CarPool> arraylistCarPool=new ArrayList<>();

    View viewComment;
    CarPool selectedCarPool;

    Button btnSubmit, btnCancel;
    TextView txtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car_pool);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("My Pool Offer");
        actionBar.show();

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        listViewMyCarPool = findViewById(R.id.listViewMyCarPool);
        txtMessage = findViewById(R.id.txtMessage);
        fabMyPool = findViewById(R.id.fabMyPool);
        progressBar = findViewById(R.id.progressBar);
        viewComment = findViewById(R.id.viewComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        txtComment = findViewById(R.id.txtComment);

        adapterCarPool = new AdapterMyCarPool(getApplicationContext(),0,arraylistCarPool);
        listViewMyCarPool.setAdapter(adapterCarPool);

        fabMyPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MyCarPoolActivity.this, AddPoolOfferActivity.class);
                startActivityForResult(newIntent, ApplicationConstants.REQUEST_ADD_CARPOOL);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompletePool();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewComment.setVisibility(View.GONE);
                txtComment.setText("");
            }
        });

        LoadMyCarPoolData();
    }


    public void LoadMyCarPoolData(){
        txtMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Self/" + socUser.SocietyId+"/" +socUser.ResID +"/"
                + PageNumber + "/" + Count;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonResult) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        arraylistCarPool.clear();
                        int x = jsonResult.length();
                        if (x ==0)
                        {
                            listViewMyCarPool.setVisibility(View.GONE);
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText("No Car Pool found !");
                        }
                        else {
                            listViewMyCarPool.setVisibility(View.VISIBLE);
                            txtMessage.setVisibility(View.GONE);
                            for (int i = 0; i < x; i++) {
                                JSONObject jObj = jsonResult.getJSONObject(i);
                                CarPool  pool = new CarPool();
                                pool.CarPoolID = jObj.getInt("VehiclePoolID");
                                pool.Destination = jObj.getString("Destination");
                                pool.Available = jObj.getInt("AvailableSeats");
                                pool.FlatNo = jObj.getString("FlatNumber");
                                pool.StartTime = jObj.getString("InitiatedDateTime");
                                pool.ReturnTime = jObj.getString("ReturnDateTime");
                                pool.Cost = jObj.getString("SharedCost");
                                pool.Contact = jObj.getString("MobileNo");
                                pool.Description = jObj.getString("Description");
                                pool.VehicleType = jObj.getString("VehicleType");
                                pool.FirstName = jObj.getString("FirstName");
                                pool.LastName = jObj.getString("LastName");
                                pool.UserID = jObj.getInt("UserID");
                                pool.OneWay = jObj.getBoolean("OneWay");
                                pool.Status = jObj.getBoolean("Active");
                                pool.InterestedCount = jObj.getInt("InterestedCount");
                                arraylistCarPool.add(pool);
                            }
                            adapterCarPool.notifyDataSetChanged();
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


    class AdapterMyCarPool extends ArrayAdapter<CarPool> {
        LayoutInflater inflat;
        ViewHolder holder;
        public AdapterMyCarPool(Context context, int resource, ArrayList<CarPool> objects) {

            super(context, resource, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return arraylistCarPool.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_carpool, null);
                    holder = new ViewHolder();
                    holder.txtDestination = convertView.findViewById(R.id.txtDestination);
                    holder.txtStartTime = convertView.findViewById(R.id.txtStartTime);
                    holder.txtDescription = convertView.findViewById(R.id.txtDescription);
                    holder.txtReturnTime = convertView.findViewById(R.id.txtReturnTime);
                    holder.txtVehicle = convertView.findViewById(R.id.txtVehicle);
                    holder.txtSeats = convertView.findViewById(R.id.txtSeats);
                    holder.txtCost = convertView.findViewById(R.id.txtCost);
                    holder.txtComment = convertView.findViewById(R.id.txtComment);
                    holder.txtFlat= convertView.findViewById(R.id.txtFlat);
                    holder.txtName = convertView.findViewById(R.id.txtName);
                    // holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                    holder.imageResident= convertView.findViewById(R.id.imageResident);
                    holder.txtClose = convertView.findViewById(R.id.txtClose);
                    holder.viewHeader = convertView.findViewById(R.id.viewHeader);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();


                holder.txtClose.setVisibility(View.VISIBLE);

                final CarPool row = getItem(position);

                Date startTime = Utility.DBStringToLocalDate(row.StartTime);
                Date endTime = Utility.DBStringToLocalDate(row.ReturnTime);

                // Log.d("Dish Name", row.complaint_type);
                if(!row.OneWay)
                {
                    holder.txtDestination.setText("Return journey to " + row.Destination  + " On " + Utility.DateToDisplayDateTime(startTime));
                }
                else
                {
                    holder.txtDestination.setText("One Way to " + row.Destination + " On " + Utility.DateToDisplayDateTime(startTime));
                }

                holder.txtStartTime.setText(Utility.DateToDisplayDateTime(startTime));
                holder.txtReturnTime.setText(Utility.DateToDisplayDateTime(endTime));

                holder.txtComment.setText(row.InterestedCount + " interested");
                holder.txtName.setText(row.FirstName + " " + row.LastName + ", " + row.FlatNo);
                //  holder.txtFlatNumber.setText(row.FlatNo);

                String url1 = "http://www.Nestin.online/ImageServer/User/" + row.UserID +".png";
                Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(holder.imageResident);

                holder.txtVehicle.setText(row.VehicleType);
                holder.txtFlat.setText(row.FlatNo);
                holder.txtCost.setText(row.Cost);
                holder.txtSeats.setText(Integer.toString(row.Available));
                holder.txtDescription.setText(row.Description);

                if(!row.Status)
                {
                    holder.txtClose.setVisibility(View.GONE);
                    holder.viewHeader.setBackgroundColor(Color.rgb(225, 38, 41));
                }

                holder.txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCarPool = row;
                        viewComment.setVisibility(View.VISIBLE);
                    }
                });

                return convertView;
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Could not Load RentData", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public CarPool getItem(int position) {
            // TODO Auto-generated method stub
            return arraylistCarPool.get(position);
        }

        @Override
        public int getPosition(CarPool item) {
            return super.getPosition(item);
        }
    }
    private class ViewHolder
    {
        TextView txtDestination,txtStartTime,txtReturnTime,txtVehicle,txtSeats,txtCost,txtAvailable,txtFlat,txtDescription,txtComment;
        TextView txtName, txtFlatNumber, txtClose;
        ImageView imageResident;
        View viewHeader;
    }



    private void CompletePool()
    {
        String comment = txtComment.getText().toString();
        int CarPoolID = selectedCarPool.CarPoolID;
        txtMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Status";

        String reqBody = "{\"Destination\":\"\", \"AvailableSeats\":\"\", \"InitiatedDateTime\":\"\",\"JourneyDateTime\":\""
                + "\",\"ReturnDateTime\":\"\",\"ResID\":\""+ socUser.ResID  + "\",\"VehiclePoolID\":\""+ CarPoolID
                +"\",\"VehicleType\":\"\",\"SharedCost\":\"\" ,\"Active\":\"false\",\"OneWay\":\"\",\"PoolTypeID\":\""
                +"\",\"Description\":\""+ comment +"\"}";


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
                            LoadMyCarPoolData();
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

        if(requestCode == ApplicationConstants.REQUEST_ADD_CARPOOL && resultCode == RESULT_OK)
        {
            LoadMyCarPoolData();
        }
    }
}
