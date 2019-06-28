package net.anvisys.NestIn.CarPool;

import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.CarPool;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Rent.MyRentActivity;
import net.anvisys.NestIn.Rent.RentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class CarPoolActivity extends AppCompatActivity {
    ListView carListView;
    ProgressBar progressBar;
    SocietyUser socUser;
    CarPool pool;
    AdapterCarPool adapterCarPool;
    ArrayList<CarPool> arraylistCarPool=new ArrayList<>();
    LinearLayout comment;
    Button btnSubmitComment,btnClose;
    EditText txtInterest;
    TextView txtMessage;
    int PageNumber=1;
    int Count =10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" CarPool ");
        actionBar.show();

        progressBar = findViewById(R.id.progressBar);
        carListView = findViewById(R.id.carListView);

        txtMessage = findViewById(R.id.txtMessage);
        comment = findViewById(R.id.comment);
        comment.setVisibility(View.GONE);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setVisibility(View.GONE);
            }
        });
        txtInterest = findViewById(R.id.txtInterest);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddInterest();
            }
        });
        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
        adapterCarPool =new AdapterCarPool(CarPoolActivity.this,0,arraylistCarPool);
        carListView.setAdapter(adapterCarPool);
        LoadCarPoolData();
    }



    public void LoadCarPoolData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/All/" + socUser.SocietyId+"/" +socUser.ResID +"/" + PageNumber + "/" + Count;
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonResult) {
                    progressBar.setVisibility(View.GONE);
                    try {

                        int x = jsonResult.length();
                        if (x ==0)
                        {
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText("No Car Pool found !");
                        }
                        else {
                            txtMessage.setVisibility(View.GONE);
                            for (int i = 0; i < x; i++) {
                                JSONObject jObj = jsonResult.getJSONObject(i);
                                pool = new CarPool();
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


    class AdapterCarPool extends ArrayAdapter<CarPool> {
        LayoutInflater inflat;
        ViewHolder holder;
        public AdapterCarPool(Context context, int resource, ArrayList<CarPool> objects) {

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
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                CarPool row = getItem(position);

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


                holder.txtName.setText(row.FirstName + " " + row.LastName + ", " + row.FlatNo);
              //  holder.txtFlatNumber.setText(row.FlatNo);

                String url1 = "http://www.Nestin.online/ImageServer/User/" + row.UserID +".png";
                Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(holder.imageResident);

                holder.txtVehicle.setText(row.VehicleType);
                holder.txtFlat.setText(row.FlatNo);
                holder.txtCost.setText(row.Cost);
                holder.txtSeats.setText(Integer.toString(row.Available));
                holder.txtDescription.setText(row.Description);
                holder.txtComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comment.setVisibility(View.VISIBLE);
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
        TextView txtName, txtFlatNumber;
        ImageView imageResident;
    }

    public void AddInterest(){
        String strInterest = txtInterest.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/Add/Interest";
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pool, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_myRide) {
            Intent profileIntent = new Intent(CarPoolActivity.this, MyCarPoolActivity.class);
            startActivity(profileIntent);
        }

        return super.onOptionsItemSelected(item);
    }


}
