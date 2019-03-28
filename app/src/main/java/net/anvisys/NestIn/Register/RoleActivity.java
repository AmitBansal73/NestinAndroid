package net.anvisys.NestIn.Register;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Forum.ForumCompActivity;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoleActivity extends AppCompatActivity {

    Spinner spPurpose;
    Button btnNewSociety, btnExistingSociety,btnHouse;
    String selectedPurpose="",strSociety="",strHouse="",strSector="",strFlat="",strLocality="",strCity="",strState="",strPincode="",TotalFlats="";
    LinearLayout content_indepndent_house,content_existing_society, content_new_society ;
    Profile myProfile;
    SocietyUser socUser;
    TextView txtSearchSoc,txtSearchFlat;
    EditText txtSocietyNew,txtHouse,txtSector,txtLocality,txtCity,txtState,txtPincode,selectSociety,selectFlat,txtTotalFlats,txtSectorNew,txtCityNew,txtStateNew,txtPincodeNew;
    ProgressBar progressBar;
    Society soc;
    Flats flat;
    MyAdapter adapter;
    MyAdapterFlats adapterFlats;
    ListView societyList,flatList;
    ArrayList<Society> arraylist=new ArrayList<>();
    ArrayList<Flats> arraylistflat=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" NestIn ");
        actionBar.show();

       // btnSubmit = findViewById(R.id.btnSubmit);
        spPurpose = findViewById(R.id.spPurpose);
        content_new_society = findViewById(R.id.newRegistration);
        content_indepndent_house = findViewById(R.id.indipendent);
        content_existing_society = findViewById(R.id.existingSociety);
        selectSociety = findViewById(R.id.selectSociety);
        txtSocietyNew = findViewById(R.id.txtSocietyNew);
        txtHouse = findViewById(R.id.txtHouse);
        txtSector = findViewById(R.id.txtSector);
        txtTotalFlats= findViewById(R.id.txtTotalFlats);
        selectFlat= findViewById(R.id.selectFlat);
       // txtLocality= findViewById(R.id.txtLocality);
        //txtCity= findViewById(R.id.txtCity);
       // txtState= findViewById(R.id.txtState);
       // txtPincode= findViewById(R.id.txtPincode);
        txtCityNew= findViewById(R.id.txtCityNew);
        txtSectorNew= findViewById(R.id.txtSectorNew);
        txtPincodeNew= findViewById(R.id.txtPincodeNew);


        myProfile = Session.GetUser(this);
        socUser = Session.GetCurrentSocietyUser(this);
        btnExistingSociety = findViewById(R.id.btnExistingSociety);
        btnHouse = findViewById(R.id.btnHouse);
        btnNewSociety = findViewById(R.id.btnNewSociety);
        progressBar = findViewById(R.id.progressBar);
        societyList = findViewById(R.id.societyList);
        flatList = findViewById(R.id.flatList);
        txtSearchSoc = findViewById(R.id.txtSearchSoc);
        txtSearchSoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                societyList.setVisibility(View.VISIBLE);
                GetSociety();
            }
        });
        txtSearchFlat = findViewById(R.id.txtSearchFlat);
        txtSearchFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFlat.setVisibility(View.VISIBLE);
                GetFlat();
            }
        });
        btnExistingSociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExistingSociety();
            }
        });

        btnHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHouse();
            }
        });

        btnNewSociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewSociety();
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LoginPurpose, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPurpose.setAdapter(adapter);

        spPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedPurpose = spPurpose.getSelectedItem().toString();
                 if (selectedPurpose.equalsIgnoreCase("Indipendent House"))
                {
                    content_indepndent_house.setVisibility(View.VISIBLE);
                    content_existing_society.setVisibility(View.GONE);
                    content_new_society.setVisibility(View.GONE);
                }else if (selectedPurpose.equalsIgnoreCase("Enroll in Existing Society"))
                {
                    content_existing_society.setVisibility(View.VISIBLE);
                    content_indepndent_house.setVisibility(View.GONE);
                    content_new_society.setVisibility(View.GONE);
                }else if (selectedPurpose.equalsIgnoreCase("Request For New Society Registration"))
                {
                    content_new_society.setVisibility(View.VISIBLE);
                    content_indepndent_house.setVisibility(View.GONE);
                    content_existing_society.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }

    private  void AddExistingSociety()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/SocietyUser";
        try{
            String reqBody =  "{\"UserId\":\"" +myProfile.UserID+"\",\"FlatID\":\"" +socUser.FlatID+"\",\"Type\":\"" +socUser.RoleType+"\",\"ServiceType\":\"" +socUser.ServiceType+"\",\"CompanyName\":\""
                    +socUser.CompanyName+"\",\"ActiveDate\":\" \",\"DeActiveDate\":\" \",\"ModifiedDate\":\" \",\"SocietyID\":\"" +socUser.SocietyId +"\",\"Status\":\"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }catch (Exception e){

        }
    }

    private  void AddNewSociety()
    {
        progressBar.setVisibility(View.VISIBLE);
        strSociety= txtSocietyNew.getText().toString();
        TotalFlats = txtTotalFlats.getText().toString();
        strSector = txtSectorNew.getText().toString();
        strPincode = txtPincodeNew.getText().toString();
        strCity = txtCityNew.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/SocietyUser";
        try{
            String reqBody =  "{\"SocietyName\":\"" +strSociety+"\",\"TotalFlats\":\"" +TotalFlats+"\",\"Sector\":\"" +strSector+"\",\"City\":\"" +strCity+"\",\"PinCode\":\"" +strPincode +"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        if (result.matches("Fail")) {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else{
                            Toast.makeText(getApplicationContext(), "Society Added Successfully", Toast.LENGTH_LONG).show();
                        }


                    }catch (Exception ex){

                    }
                    progressBar.setVisibility(View.GONE);
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


        }catch (Exception e){

        }
    }

    public void AddHouse(){
        progressBar.setVisibility(View.VISIBLE);
        strSociety= txtSocietyNew.getText().toString();
        strCity = txtCity.getText().toString();
        strPincode = txtPincode.getText().toString();
        strLocality = txtLocality.getText().toString();
        strSector = txtSector.getText().toString();
        strState = txtState.getText().toString();
        strHouse = txtHouse.getText().toString();
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Add/House/"+ myProfile.UserID;
        try {
            String reqBody =  "{\"UserId\":\"" +myProfile.UserID+"\",\"Society\":\"" +strSociety+"\",\"City\":\"" +strCity+"\",\"State\":\"" +strState+"\",\"Pincode\":\""
                    +strPincode+"\",\"HouseID\":\""+strHouse+"\",\"Sector\":\"" +strSector+"\",\"Locality\":\"" +strLocality+"\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   try {
                       if(response.getString("Response").matches("Ok"))
                       {
                           Toast.makeText(getApplicationContext(), "House Added Successfully.", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(RoleActivity.this, DashboardActivity.class);
                           startActivity(intent);
                           RoleActivity.this.finish();
                           progressBar.setVisibility(View.GONE);

                       }else if (response.getString("Response").matches("Fail")){
                           Toast.makeText(getApplicationContext(), " Failed ", Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                       }

                   }catch (Exception ex){

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


        }catch (Exception e){

        }
    }

    public void GetSociety(){
        strSociety= selectSociety.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Society/"+ strSociety;
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jArray) {
                    try{

                        JSONArray json = jArray.getJSONArray("$values");
                        int x = json.length();
                        for(int i = 0; i < x; i++){
                            JSONObject jObj = json.getJSONObject(+i);
                            soc = new Society();
                            soc.SocietyName= jObj.getString("SocietyName");
                            soc.SocietyID = jObj.getInt("SocietyID");
                            soc.City = jObj.getString("City");
                            soc.TotalFlats = jObj.getInt("TotalFlats");
                            soc.PinCode = jObj.getInt("PinCode");
                            soc.Sector = jObj.getInt("Sector");
                            arraylist.add(soc);
                        }
                        adapter =new MyAdapter(RoleActivity.this,0, 0, arraylist);
                        societyList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        societyList.setVisibility(View.VISIBLE);

                    }catch (JSONException js){
                        int e= 1;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
    }

    class Society
    {
        String SocietyName,City;
        Integer TotalFlats,Sector,PinCode,SocietyID;
    }
    class Flats
    {
        String FlatNumber,Block;
        Integer IntercomNumber,Floor,FlatArea,SocietyID;
    }



    public void GetFlat()
    {
        strFlat = selectFlat.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/User/Flat/"+ soc.SocietyID+"/"+selectFlat ;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {
                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();
                    for(int i = 0; i < x; i++){
                        JSONObject jObj = json.getJSONObject(+i);
                        flat = new Flats();
                        flat.FlatNumber= jObj.getString("FlatNumber");
                        flat.Block = jObj.getString("Block");
                        flat.FlatArea= jObj.getInt("FlatArea");
                        flat.Floor = jObj.getInt("Floor");
                        flat.IntercomNumber = jObj.getInt("IntercomNumber");
                        flat.SocietyID = jObj.getInt("SocietyID");
                        //arraylist1.add(flat);
                    }
                    adapterFlats =new MyAdapterFlats(RoleActivity.this,0, 0, arraylistflat);
                    flatList.setAdapter(adapterFlats );
                    adapterFlats.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    flatList.setVisibility(View.VISIBLE);

                }catch (JSONException js){
                    int e= 1;
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
    }




    class MyAdapter extends ArrayAdapter<Society>{
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapter(Context context, int resource, int textViewResourceId, ArrayList<Society> objects) {

            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arraylist.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_societylist, null);
                    holder = new ViewHolder();
                    holder.txtLocality = convertView.findViewById(R.id.txtLocality);
                    holder.txtCity = convertView.findViewById(R.id.txtCity);
                    holder.txtState = convertView.findViewById(R.id.txtState);
                    holder.txtPincode = convertView.findViewById(R.id.txtPincode);
                    holder.txtArea = convertView.findViewById(R.id.txtArea);
                    holder.txtFloor = convertView.findViewById(R.id.txtFloor);
                    holder.txtBlock = convertView.findViewById(R.id.txtBlock);
                    holder.txtIntercom = convertView.findViewById(R.id.txtIntercom);
                    holder.txtSociety = convertView.findViewById(R.id.txtSociety);
                    convertView.setTag(holder);
                }
               Society row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtSociety.setText(row.SocietyName);
               // holder.txtCity.setText(row.City);
               // holder.txtPincode.setText(row.PinCode);
                return convertView;
            }

            catch (Exception ex)

            {
                Toast.makeText(getApplicationContext(),"Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public int getPosition(Society item) {
            return super.getPosition(item);
        }

        @Override
        public Society getItem(int position) {
            return arraylist.get(position);
        }
    }
    private class ViewHolder
    {
        TextView txtLocality,txtCity,txtState,txtPincode,txtArea,txtFloor,txtBlock,txtIntercom,txtSociety,txtFlatNumber;
    }

    class MyAdapterFlats extends ArrayAdapter<Flats>{
        LayoutInflater inflat;
        ViewHolder holder;
        public MyAdapterFlats(Context context, int resource, int textViewResourceId, ArrayList<Flats> objects) {

            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arraylistflat.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_flatlist, null);
                    holder = new ViewHolder();
                    holder.txtFlatNumber = convertView.findViewById(R.id.txtFlatNumber);
                    holder.txtArea = convertView.findViewById(R.id.txtArea);
                    holder.txtFloor = convertView.findViewById(R.id.txtFloor);
                    holder.txtBlock = convertView.findViewById(R.id.txtBlock);
                    holder.txtIntercom = convertView.findViewById(R.id.txtIntercom);
                    convertView.setTag(holder);
                }
                Flats row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);
                holder.txtFlatNumber.setText(row.FlatNumber);
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
        public int getPosition(Flats item) {
            return super.getPosition(item);
        }

        @Override
        public Flats getItem(int position) {
            return arraylistflat .get(position);
        }
    }
}
