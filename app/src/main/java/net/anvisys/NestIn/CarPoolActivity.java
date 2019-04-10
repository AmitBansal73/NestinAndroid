package net.anvisys.NestIn;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Object.CarPool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CarPoolActivity extends AppCompatActivity {
    ListView carListView;
    ProgressBar progressBar;
    SocietyUser socUser;
    CarPool pool;
    MyAdapterCarPool adapterCarPool;
    ArrayList<CarPool> arraylistCarPool=new ArrayList<>();
    LinearLayout comment;
    Button btnSubmitComment;
    EditText txtInterest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool);

        progressBar = findViewById(R.id.progressBar);
        carListView = findViewById(R.id.carListView);
        comment = findViewById(R.id.comment);
        comment.setVisibility(View.GONE);
        txtInterest = findViewById(R.id.txtInterest);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setVisibility(View.GONE);
            }
        });

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());

        LoadCarPoolData();
    }

    public void LoadCarPoolData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/self" + socUser.SocietyId +"/"+ socUser.ResID+ "/0/20";
        try{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");
                        int x = json.length();
                        for (int i = 0; i <x; i++) {
                            JSONObject jObj = json.getJSONObject(i);
                            pool = new CarPool();
                            pool.Destination = jObj.getString("Destination");
                            pool.Available = jObj.getInt("Available");
                            pool.TotalSeats = jObj.getInt("TotalSeats");
                            pool.StartTime = jObj.getString("StartTime");
                            pool.ReturnTime = jObj.getString("ReturnTime");
                            pool.Cost = jObj.getInt("Cost");
                            pool.Contact = jObj.getString("Contact");
                            pool.Description = jObj.getString("Description");
                            arraylistCarPool.add(pool);
                        }
                        progressBar.setVisibility(View.GONE);
                        adapterCarPool.notifyDataSetChanged();

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
    class MyAdapterCarPool extends ArrayAdapter<CarPool> {
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterCarPool(Context context, int resource, ArrayList<CarPool> objects) {

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
                    convertView = inflat.inflate(R.layout.row_listrent, null);
                    holder = new ViewHolder();
                    holder.txtDestination = convertView.findViewById(R.id.txtDestination);
                    holder.txtStartTime = convertView.findViewById(R.id.txtStartTime);
                    holder.txtDescription = convertView.findViewById(R.id.txtDescription);
                    holder.txtReturnTime = convertView.findViewById(R.id.txtReturnTime);
                    holder.txtVehicle = convertView.findViewById(R.id.txtVehicle);
                    holder.txtSeats = convertView.findViewById(R.id.txtSeats);
                    holder.txtCost = convertView.findViewById(R.id.txtCost);
                    holder.txtComment = convertView.findViewById(R.id.txtComment);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                CarPool row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtDestination.setText(row.Destination);
                holder.txtStartTime.setText(row.StartTime);
                holder.txtDescription.setText(row.Description);
                holder.txtReturnTime.setText(row.ReturnTime);
                holder.txtVehicle.setText(row.VehicleType);
                holder.txtSeats.setText(row.TotalSeats);
                holder.txtCost.setText(row.Cost);
                holder.txtAvailable.setText(row.Available);
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
        TextView txtDestination,txtStartTime,txtReturnTime,txtVehicle,txtSeats,txtCost,txtAvailable,txtDescription,txtComment;
    }

}
