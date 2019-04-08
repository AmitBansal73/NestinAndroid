package net.anvisys.NestIn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import net.anvisys.NestIn.Object.Rent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarPoolActivity extends AppCompatActivity {
    ListView carListView;
    ProgressBar progressBar;
    SocietyUser socUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool);

        progressBar = findViewById(R.id.progressBar);
        carListView = findViewById(R.id.carListView);

        socUser = Session.GetCurrentSocietyUser(getApplicationContext());
    }

    public void LoadCarPoolData(){

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/CarPool/" + socUser.SocietyId + "/0/20";
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
                         /*   rentInvent = new Rent();
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
                            arraylistRent.add(rentInvent); */
                        }
                        progressBar.setVisibility(View.GONE);
                      //  adapterRent.notifyDataSetChanged();

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

}
