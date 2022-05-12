package net.anvisys.NestIn.Vendor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Toast;

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
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Model.Vendor;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class VendorActivity extends AppCompatActivity implements Summary.SummaryListener {

    ListView vendorList;
    VendorAdaptor vendorAdaptor;
    HashMap<Integer,Vendor> listAllVendors = new HashMap<>();
    HashMap<Integer,Vendor> listDisplayedVendors = new HashMap<>();
    Spinner spinnerCategory;
    Snackbar snackbar;
    String LastRefreshTime ="01/01/2010 12:00:00";
    TextView summaryText, txtMessage;
    SocietyUser socUser;

    String selectedCategory = "All";
    int PageNumber =1;
    int Count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Vendor Directory");
        actionBar.show();

        txtMessage = findViewById(R.id.txtMessage);

        //summaryText = (TextView)findViewById(R.id.summaryText);
        vendorList =  findViewById(R.id.vendorList);

        socUser = Session.GetCurrentSocietyUser(this);

       // InitializeVariableFromIntent();
        initializeVendorList();

        DataAccess da = new DataAccess(this);
        da.open();
        listAllVendors = da.getAllVendors(socUser.SocietyId);
        listDisplayedVendors = da.getAllVendors(socUser.SocietyId);
        da.close();

        vendorAdaptor= new VendorAdaptor(this);
        vendorAdaptor.notifyDataSetChanged();
        vendorList.setAdapter(vendorAdaptor);

        if( listAllVendors.size() == 0)
        {
            LastRefreshTime = "01/01/2000 12:00:00";
        }
        else
        {
            LastRefreshTime = Session.GetVendorRefreshTime(this);
        }

        if(Utility.IsConnected(getApplicationContext()) && ApplicationVariable.AUTHENTICATED == true) {
                initializeVendorList();
                }
        else
        {
            Toast.makeText(this, "Not Connected, Working offline", Toast.LENGTH_LONG).show();
        }
        //Summary.RegisterSummaryListener(this);
    }





    private class VendorAdaptor extends BaseAdapter {

        LayoutInflater inflater;

        public VendorAdaptor(Activity activity) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            try {
                return listDisplayedVendors.size();
            }
            catch (Exception ex)
            {
                return 0;
            }
        }
        @Override
        public Object getItem(int position) {
            try {
                return (Vendor) listDisplayedVendors.values().toArray()[position];
            }
            catch (Exception ex)
            {
                return  null;
            }
           // return listDisplayedVendors.get(position);
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                View view = convertView;
                if (view == null) {
                    view = inflater.inflate(R.layout.row_item_vendor, null);
                }
                ImageView img =  view.findViewById(R.id.imageView);
                TextView txtName =  view.findViewById(R.id.vendorName);
                TextView txtAddress =  view.findViewById(R.id.vendorAddress);
                TextView txtCont1 =  view.findViewById(R.id.cont1);
                ImageView imageCall = view.findViewById(R.id.imageCall);
                TextView txtOffer = view.findViewById(R.id.txtOffer);
               // TextView txtEmail = view.findViewById(R.id.txtEmail);
               // TextView txtUrl = view.findViewById(R.id.txtUrl);
               // TextView txtOffer = view.findViewById(R.id.txtOffer);
               // TextView txtCont2 =  view.findViewById(R.id.cont2);
               // TextView imageText = view.findViewById(R.id.imageText);
                final Vendor vendor = (Vendor)listDisplayedVendors.values().toArray()[position];

              // Vendor vendor = listDisplayedVendors.get(position)
              //  img.setImageResource(R.drawable.offers3);
                txtName.setText(vendor.vendorName);
                txtAddress.setText(vendor.VendorAddress);
                txtCont1.setText(vendor.VendorCont1);
                img.setVisibility(View.VISIBLE);
                imageCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String call = "tel:91-" + vendor.VendorCont1;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(call));
                        startActivity(i);
                    }
                });

               // txtEmail.setText(vendor.VendorEmail);
               // txtUrl.setText(vendor.VendorUrl);
               // txtOffer.setText(vendor.VendorOffer);
               // txtCont2.setText(vendor.VendorCont2);
                String url1 = "http://www.Nestin.online/ImageServer/Vendor/" + vendor.VendorID +".png";
                Picasso.with(getApplicationContext()).load(url1).error(R.drawable.icon_shopping).into(img);

                if(vendor.OfferId == 0)
                {
                    txtOffer.setVisibility(View.GONE);
                }
                else
                {
                    txtOffer.setText(vendor.VendorOffer);
                }

               /* if(vendor.strImage!=null) {
                    Bitmap bmp = ImageServer.getBitmapFromString(vendor.strImage, getApplicationContext());
                    img.setImageBitmap(bmp);
                    img.setVisibility(View.VISIBLE);
                    imageText.setVisibility(View.GONE);
                }
                else
                {
                    img.setVisibility(View.INVISIBLE);
                    imageText.setVisibility(View.VISIBLE);
                    imageText.setText(vendor.vendorName.substring(0,1));
                    int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                    int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    imageText.setBackgroundColor(randomAndroidColor);

                }*/
                return view;
            }
            catch (Exception ex)
            {
                Toast.makeText(VendorActivity.this, "Filter Error", Toast.LENGTH_SHORT).show();
                int b=5;
                return null;
            }
        }

        // Filter Class
        public void filter(String selectedCategory) {

            try {
                //  charText = selectedStatus.toLowerCase(Locale.getDefault());
                String selection = selectedCategory.toLowerCase(Locale.getDefault());
                listDisplayedVendors.clear();


                if (selectedCategory.matches("All")) {
                    listDisplayedVendors.putAll(listAllVendors);
                } else {
                    for (Vendor vend : listAllVendors.values()) {

                        if (vend.Category.toLowerCase(Locale.getDefault()).matches(selection)) {

                            listDisplayedVendors.put(vend.VendorID, vend);
                        }
                    }
                }
               // summaryText.setText("Total " + listDisplayedVendors.size() + "vendors in selected category");

                    notifyDataSetChanged();

            }
            catch (Exception ex)
            {
                Toast.makeText(VendorActivity.this, "Filter Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.medical)
        {
            selectedCategory = "Medicine";
            vendorAdaptor.filter(selectedCategory);
        }
        else if (id == R.id.paper)
        {

            selectedCategory = "Paper";
            vendorAdaptor.filter(selectedCategory);
        }
        else if (id == R.id.water)
        {
            selectedCategory = "Water";
            vendorAdaptor.filter(selectedCategory);
        }
        else if (id == R.id.Grocery)
        {
            selectedCategory = "Grocery";
            vendorAdaptor.filter(selectedCategory);
        }
        else if (id == R.id.DryCleaner)
        {
            selectedCategory = "DryCleaner";
            vendorAdaptor.filter(selectedCategory);
        }
        else if (id == R.id.all)
        {
            selectedCategory = "All";
            vendorAdaptor.filter(selectedCategory);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            Intent shopActivity = new Intent(VendorActivity.this,
                    DashboardActivity.class);

            shopActivity.putExtra("PARENT","VENDOR");

          //  Bundle myData = CreateBundle();
           // MenuActivity.putExtras(myData);
            startActivity(shopActivity);
            VendorActivity.this.finish();
            Log.d(this.getClass().getName(), "back button pressed");

        }
        return super.onKeyDown(keyCode, event);
    }
    public void initializeVendorList()
    {
       // ShowSnackBar( "Refreshing List","");

        String url = ApplicationConstants.APP_SERVER_URL+  "/api/Vendor/Get/" + socUser.SocietyId +"/"
                +  selectedCategory + "/" + PageNumber+ "/" + Count;

/*
        JSONObject jsRequest=null;
        try {
            String reqBody = "{\"value\":\""+ LastRefreshTime+ "\",\"SocietyID\":" + socUser.SocietyId  + "}";
            jsRequest = new JSONObject(reqBody);
        }
        catch (JSONException jex)
        {
            int a=1;
        }
        catch (Exception ex)
        {
            int a=1;
        }*/

        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jArray) {

                try{
                    int x = jArray.length();
                    if( x>0) {
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();
                        for (int i = 0; i < x; i++) {
                            JSONObject jTypeObj = jArray.getJSONObject(i);
                            Vendor newShop = new Vendor();
                            newShop.VendorID = jTypeObj.getInt("VendorID");

                            newShop.Category = jTypeObj.getString("ShopCategory");
                            newShop.vendorName = jTypeObj.getString("VendorName");
                            newShop.VendorCont1 = jTypeObj.getString("ContactNumber");
                            newShop.VendorAddress = jTypeObj.getString("Address");
                           // newShop.strImage = jTypeObj.getString("VendorIcon");
                           // newShop.VendorEmail =  jTypeObj.getString("Email");
                             newShop.OfferId=  jTypeObj.getInt("OfferID");
                             newShop.VendorOffer=  jTypeObj.getString("offer");

                            da.insertNewVendor(newShop, socUser.SocietyId);
                            if (listAllVendors.containsKey(newShop.VendorID)) {
                                listAllVendors.remove(newShop.VendorID);
                            }

                            if (listDisplayedVendors.containsKey(newShop.VendorID)) {
                                listDisplayedVendors.remove(newShop.VendorID);
                            }

                            listAllVendors.put(newShop.VendorID, newShop);
                            listDisplayedVendors.put(newShop.VendorID, newShop);
                        }
                        da.close();
                        //HideSnackBar();

                        if(listDisplayedVendors.size()>0) {
                            txtMessage.setVisibility(View.VISIBLE);
                            vendorAdaptor.notifyDataSetChanged();
                        }
                        else
                        {
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText( "No Vendor found for " + selectedCategory);
                        }
                          //  Session.SetVendorRefreshTime(getApplicationContext());
                            ApplicationConstants.VENDOR_UPDATES = 0;
                           // LoadImage(x);
                    }
                }
                catch (JSONException e)
                {
                  //  HideSnackBar();
                }
                catch (Exception ex)
                {
                    int b =8;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowSnackBar("Could not refresh data", "Retry");
            }
        });
        RetryPolicy policy = new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };

        jsArrayRequest.setRetryPolicy(policy);
        queue.add(jsArrayRequest);


    }


    private void Call1(View v)
    {

    }

   private void LoadImage(int x)
    {
        try {

            int StartIndex = 0;

            while (x > 3) {
                int EndIndex = StartIndex + 3;
                GetImages(StartIndex, EndIndex);
                StartIndex = EndIndex;
                x = x - 3;
            }
            if (x > 0 && x < 3) {
                int finalIndex = StartIndex + x;
                GetImages(StartIndex, finalIndex);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this,"Error in Load Image", Toast.LENGTH_LONG).show();
        }
    }
    private void GetImages(int StartIndex, int EndIndex)
    {
        ShowSnackBar( "Refreshing Images","");

        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Vendor/Images" ;

        String reqBody = "{\"StartIndex\":\""+StartIndex+ "\",\"EndIndex\":\""+EndIndex+ "\",\"SocietyID\":\""+socUser.SocietyId+"\"}";
        JSONObject jsRequest=null;

        try {
            jsRequest = new JSONObject(reqBody);
        }
        catch (JSONException jex)
        {

        }
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    JSONArray jArray =  jObject.getJSONArray("$values");
                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int x = jArray.length();
                    for(int i = 0; i < x; i++){
                        JSONObject jTypeObj = jArray.getJSONObject(i);
                        int ID = jTypeObj.getInt("ID");
                        Vendor newShop;
                        if(listAllVendors.containsKey(ID))
                        {
                           newShop = listAllVendors.get(ID);
                            newShop.strImage=  jTypeObj.getString("ImageString");}
                        else{
                           newShop = new Vendor();
                            newShop.strImage=  jTypeObj.getString("ImageString");
                            listAllVendors.put(ID,newShop);
                        }

                        if(listDisplayedVendors.containsKey(ID))
                        {
                           newShop = listDisplayedVendors.get(ID);
                            newShop.strImage=  jTypeObj.getString("ImageString");}
                        else{
                           newShop = new Vendor();
                            newShop.strImage=  jTypeObj.getString("ImageString");
                            listDisplayedVendors.put(ID,newShop);
                        }
                        Vendor filteredShop = listDisplayedVendors.get(ID);
                        filteredShop.strImage=  jTypeObj.getString("ImageString");
                            if(newShop.strImage!= null) {

                                da.insertVendorImage(newShop.VendorID, newShop.strImage);
                            }
                        listDisplayedVendors.put(newShop.VendorID  ,newShop);
                    }
                    da.close();
                    HideSnackBar();
                    vendorAdaptor.notifyDataSetChanged();
                }
                catch (JSONException e)
                {
                    HideSnackBar();
                }

                catch (Exception ex)
                {
                    int b =8;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowSnackBar("Could not refresh data", "Retry");
            }
        });
        RetryPolicy policy = new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };

        jsArrayRequest.setRetryPolicy(policy);
        queue.add(jsArrayRequest);


    }

    @Override
    public void OnSummaryReceived() {
        if (ApplicationConstants.VENDOR_UPDATES>0)
        {
            initializeVendorList();
        }
    }

    private void ShowSnackBar( String htmlString, String buttonText)
    {

       snackbar = Snackbar
                .make(vendorList, htmlString, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
       // snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        snackBarView.setBackgroundColor(getResources().getColor(R.color.blue));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {

            }
        });

        snackbar.setAction(buttonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeVendorList();
            }
        });
    }

    private void HideSnackBar()
    {
        if(snackbar.isShown())
        {
            snackbar.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Summary.RemoveSummaryListener();
    }
}
