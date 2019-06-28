package net.anvisys.NestIn.Register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.ProfileActivity;
import net.anvisys.NestIn.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectRoleActivity extends AppCompatActivity {


    RadioButton radHouse, radFlat,radTenant;
    Button btnSubmit;

    ListView listViewRole;

    List<SocietyUser> societyList = new ArrayList<>();
    int deleteRegIDCounter=1;
    private Integer ClickCount=0;
    private long prevTime = 0;
    Profile myProfile;

    RoleAdapter myRolesAdaptor;

    TextView txtNoRole;

    int HouseCount =0;
    int FlatCount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Select Role");
        actionBar.show();
        myProfile = Session.GetUser(getApplicationContext());

        txtNoRole = findViewById(R.id.txtNoRole);
        radHouse = findViewById(R.id.radHouse);
        radFlat = findViewById(R.id.radFlat);
        radTenant = findViewById(R.id.radTenant);
        btnSubmit = findViewById(R.id.btnSubmit);

        listViewRole = findViewById(R.id.listViewRole);
        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        societyList = da.getAllSocietyUser();
        da.close();



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radHouse.isChecked())
                {
                    Intent houseIntent = new Intent(getApplicationContext(), RoleHomeActivity.class);
                    startActivityForResult(houseIntent,ApplicationConstants.REQUEST_ADD_HOUSE);
                }
                else if (radFlat.isChecked()){
                    Intent flatIntent = new Intent(getApplicationContext(), RoleFlatActivity.class);
                    flatIntent.putExtra("Role", "Owner");
                    startActivityForResult(flatIntent,ApplicationConstants.REQUEST_ADD_OWNER);

                }
                else if (radTenant.isChecked()){
                    Intent houseIntent = new Intent(getApplicationContext(), RoleFlatActivity.class);
                    houseIntent.putExtra("Role", "Tenant");
                    startActivityForResult(houseIntent,ApplicationConstants.REQUEST_ADD_OWNER);

                }
            }
        });

        DisplayData();
    }



    private class RoleAdapter extends ArrayAdapter<SocietyUser> {

        LayoutInflater inflat;

        public RoleAdapter(Context context, int resource, int textViewResourceId, List<SocietyUser> objects) {
            super(context, resource, textViewResourceId, objects);
            this.inflat = LayoutInflater.from(context);
        }



        @Override
        public SocietyUser getItem(int position) {
            return societyList.get(position);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView,  ViewGroup parent) {
            try {
                View rowView=null;
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_roles, null);
                }
                TextView txtFlat =  convertView.findViewById(R.id.txtFlat);
                TextView txtStatus =  convertView.findViewById(R.id.txtStatus);
                TextView txtSelect  = convertView.findViewById(R.id.txtSelect);
                final SocietyUser row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);


                if(row.RoleType.matches("Individual")) {
                    txtFlat.setText("House " + row.FlatNumber );
                    txtSelect.setVisibility(View.GONE);
                    HouseCount++;
                    if(HouseCount>=2)
                    {
                        radHouse.setEnabled(false);
                    }
                }
                else if (row.RoleType.matches("Admin"))
                {
                    txtFlat.setText("Admin in " + row.FlatNumber + " in " + row.SocietyName );
                    txtSelect.setVisibility(View.GONE);
                }
                else {
                    txtFlat.setText( row.RoleType +   " in Flat  " + row.FlatNumber + " in " + row.SocietyName );
                    if(row.Status.matches("Approved"))
                    {
                        txtSelect.setVisibility(View.VISIBLE);
                        txtSelect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Session.AddCurrentSocietyUser(getApplicationContext(), row);
                                Intent resInetnt = new Intent(SelectRoleActivity.this, DashboardActivity.class);
                                startActivity(resInetnt);
                            }
                        });
                    }
                    else
                    {
                        txtSelect.setVisibility(View.GONE);
                    }
                    FlatCount++;
                    if(FlatCount>=2)
                    {
                        radFlat.setEnabled(false);
                    }
                }

                txtStatus.setText( row.Status);
                return convertView;
            }

            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_role, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_Exit) {

            SelectRoleActivity.this.finish();
        }
        else if (id == R.id.action_profile) {
            Intent profileIntent = new Intent(SelectRoleActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        }
        else if (id == R.id.action_LogOff) {

            AlertDialog.Builder builder= new AlertDialog.Builder(
                    SelectRoleActivity.this);
            builder.setTitle("Log Off");
            builder.setMessage("Are you sure");
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.cancel();
                            Log.e("info", "NO");
                        }

                    });

            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            DataAccess mDataAccess = new DataAccess(getApplicationContext());
                            mDataAccess.open();
                            mDataAccess.ClearAll();
                            mDataAccess.close();
                            Session.LogOff(getApplicationContext());
                            DeleteRegIdFromServer();
                        }
                    });


            AlertDialog Alert = builder.create();

            Alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        try {
            long time = SystemClock.currentThreadTimeMillis();

            if (prevTime == 0) {
                prevTime = SystemClock.currentThreadTimeMillis();
            }

            if (time - prevTime > 1000) {
                prevTime = time;
                ClickCount=0;
            }
            if (time - prevTime < 1000 && time > prevTime) {
                ClickCount++;
                if (ClickCount == 2) {
                    SelectRoleActivity.this.finish();
                } else {
                    prevTime = time;
                    String msg = "double click to close";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean DeleteRegIdFromServer() {
        String url = ApplicationConstants.APP_SERVER_URL +"/api/gcmregister/5";
        String reqBody = "\""+ myProfile.MOB_NUMBER + "\"}";

        try{
            //  JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,reqBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   String strResponse = response.toString();
                    SelectRoleActivity.this.finish();
                    // System.exit(0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.toString();
                    if(message.equals("com.android.volley.TimeoutError")&&deleteRegIDCounter<3)
                    {
                        deleteRegIDCounter++;
                        DeleteRegIdFromServer();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Could not un-register , may still get notifications",Toast.LENGTH_LONG).show();
                        SelectRoleActivity.this.finish();
                        System.exit(0);
                    }

                }
            });
            queue.add(jsObjRequest);
            //-------------------------------------------------------------------------------------------------
        }
        catch (Exception jex)
        {
            Toast.makeText(getApplicationContext(),jex.getMessage(),Toast.LENGTH_LONG).show();
        }


        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {

            DisplayData();


        }
        catch (Exception ex)
        {
            Log.i("onActivityResult","Exception Thrown");
        }
    }


    private void DisplayData()
    {
        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        societyList = da.getAllSocietyUser();
        da.close();
        if(societyList.size()>0)
        {
            myRolesAdaptor = new RoleAdapter(getApplicationContext(),0,0,societyList);
            listViewRole.setAdapter(myRolesAdaptor);
            listViewRole.setVisibility(View.VISIBLE);
            txtNoRole.setVisibility(View.GONE);
        }
        else
        {
            listViewRole.setVisibility(View.GONE);
            txtNoRole.setVisibility(View.VISIBLE);
            txtNoRole.setText("No flat / house with your profile.");
        }

    }
}
