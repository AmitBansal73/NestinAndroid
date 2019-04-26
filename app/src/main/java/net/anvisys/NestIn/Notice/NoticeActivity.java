package net.anvisys.NestIn.Notice;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.MyGCMListnerService;
import net.anvisys.NestIn.Object.Messages;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeActivity extends AppCompatActivity implements
        MyGCMListnerService.GCMListener{

    String strUserID ,strResID,strFirstName, strLastName ,strFlatNumber, strUsrType , MobileNo, selectedCategory, selectedSeverity, strSocietyName;

    DataAccess _databaseAccess;
    ListView noticeList;

    private List<Messages> listMessages = new ArrayList<>();
    private MessageAdapter adapter;
    ProgressBar prgBar;
    //SocietyUser socUser;
    int networkCounter;
    HashMap<Integer, Bitmap> hmImage=  new HashMap<>();
    SocietyUser socUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notice);

            Toolbar toolbar =  findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Notifications");
            actionBar.show();



           // InitializeVariableFromIntent();
            socUser = Session.GetCurrentSocietyUser(this);
            strSocietyName = socUser.SocietyName;

            noticeList =  findViewById(R.id.listViewNotification);
            prgBar = findViewById(R.id.listPrgBar);
            prgBar.setVisibility(View.GONE);
            adapter = new MessageAdapter(this);
            adapter.notifyDataSetChanged();
            noticeList.setAdapter(adapter);
            Intent mIntent = getIntent();
            _databaseAccess = new DataAccess(getApplicationContext());
            _databaseAccess.open();
            listMessages = _databaseAccess.getAllNotice(socUser.SocietyId);
            _databaseAccess.close();
            Session.SetNoticeCount(getApplicationContext(), 0);
            MyGCMListnerService.setGCMNotificationListener(this);

        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Failed to create Notice Activity..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String message = intent.getStringExtra("msg");
        Long time = System.currentTimeMillis();
         Messages msg = new Messages();
        if (msg!=null ) {
            _databaseAccess = new DataAccess(getApplicationContext());
            _databaseAccess.open();
            _databaseAccess.insertNewNotice(msg, socUser.SocietyId);
            _databaseAccess.close();
            listMessages.add(0, msg);

        adapter.notifyDataSetChanged();
        }
    }



    private class MessageAdapter extends BaseAdapter {

        LayoutInflater inflater;

        public MessageAdapter(Activity activity) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            try {
                if (view == null) {
                    view = inflater.inflate(R.layout.row_item_notification, null);
                }

                TextView txtMessage =  view.findViewById(R.id.txtNotice);
                TextView txtTimestamp =  view.findViewById(R.id.txtTimeStamp);
                ImageView imageBox =  view.findViewById(R.id.imageBox);
                ImageView adminImage =  view.findViewById(R.id.adminImage);
                view.setTag(txtMessage);
                final Messages message = listMessages.get(position);
                if (message.isFile == 1) {
                    imageBox.setVisibility(View.VISIBLE);
                    final String fileName = message.notice_id +"__"+ message.filename;
                    String[] fileArray = fileName.split("\\.");
                    String appType ="";
                    String extension = fileArray[fileArray.length-1];
                    if (extension.contains("pdf")) {
                        imageBox.setImageResource(R.drawable.pdf_image);
                        appType = "application/pdf";

                    } else if (extension.contains("doc")) {
                        imageBox.setImageResource(R.drawable.doc_image);
                        appType = "application/msword";
                    }
                    else if(extension.contains("xls"))
                    {
                        imageBox.setImageResource(R.drawable.excel_image);
                        appType = "application/vnd.ms-excel";
                    }
                    else if(extension.contains("jpg")|| extension.contains("jpeg") || extension.contains("png"))
                    {
                        Bitmap bmp = ImageServer.GetImageBitmapFromExternal(fileName, getApplicationContext());
                        imageBox.setImageBitmap(bmp);
                        appType = "application/jpg";
                    }

                    else {
                        imageBox.setImageResource(R.drawable.file_image);
                    }
                    final String app = appType;
                    imageBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SCM/" + fileName);

                            if(!file.exists())
                            {
                                Toast.makeText(getApplicationContext(),"File has been removed",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), app);
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // Instruct the user to install a PDF reader here, or something
                                }
                            }
                        }
                    });
                } else {
                    imageBox.setVisibility(View.GONE);
                }

                txtMessage.setText(message.message);
                txtTimestamp.setText("Sent Notification on: " + Utility.ChangeFormat(message.timestamp));

                String url1 = "http://www.Nestin.online/ImageServer/User/" + message.userID +".png";
                Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(adminImage);

            }
            catch (Exception ex)
            {
                Utility.HandleException(getApplicationContext(),"NoticeCreateView",ex.getMessage());
            }

             return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return listMessages.get(position);
        }

        @Override
        public int getCount() {
            return listMessages.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            _databaseAccess = new DataAccess(getApplicationContext());
            _databaseAccess.open();
             boolean result = _databaseAccess.deleteAllNotice();
            _databaseAccess.close();
            listMessages.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"Clear All Pressed",Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.action_retreive) {
            getNoticeFromServer();
            Toast.makeText(getApplicationContext(),"Retrieve All Pressed",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getNoticeFromServer()
    {
        prgBar.setVisibility(View.VISIBLE);
        String url =  ApplicationConstants.APP_SERVER_URL + "/api/Notifications/" + socUser.SocietyId ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {

                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();
                    _databaseAccess = new DataAccess(getApplicationContext());
                    _databaseAccess.open();
                    _databaseAccess.deleteAllNotice();
                    for(int i = 0; i < x; i++){
                        Messages msg = new Messages();
                        JSONObject jObj = json.getJSONObject(i);
                        msg.notice_id = jObj.getInt("ID");
                        if(msg.notice_id<0)
                        {
                            continue;
                        }
                        msg.message = jObj.getString("Notification1");
                        msg.sent_by = jObj.getInt("send_by");
                        msg.timestamp =jObj.getString("Date");
                        msg.filename =jObj.getString("AttachName");
                        msg.valid_till =jObj.getString("EndDate");

                        if(msg.filename!= null && !msg.filename.equalsIgnoreCase("")&& !msg.filename.equalsIgnoreCase("null"))
                        {
                            getNoticeFromServer(msg.notice_id, msg.filename);
                            msg.isFile =1;
                            msg.filename = jObj.getString("AttachName");
                            getNoticeFromServer(msg.notice_id, msg.filename);
                        }
                        else
                        {
                            msg.isFile =0;
                            msg.filename = "";
                        }

                        _databaseAccess.insertNewNotice(msg, socUser.SocietyId);
                    }
                    listMessages = _databaseAccess.getAllNotice(socUser.SocietyId);
                    _databaseAccess.close();
                    adapter.notifyDataSetChanged();
                    prgBar.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    prgBar.setVisibility(View.GONE);}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                prgBar.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent noticeActivity = new Intent(NoticeActivity.this,DashboardActivity.class);

            noticeActivity.putExtra("PARENT","NOTICE");
          //  Bundle myData = CreateBundle();
          //  menuActivity.putExtras(myData);
            startActivity(noticeActivity);
            NoticeActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        MyGCMListnerService.removeGCMNotificationListener();
        super.onDestroy();
    }

    public void InitializeVariableFromIntent()
    {
        Intent mIntent = getIntent();
        strFirstName = mIntent.getStringExtra("name");
        strSocietyName = mIntent.getStringExtra("SocietyName");
        MobileNo = mIntent.getStringExtra("mobileno");
        strFlatNumber = mIntent.getStringExtra("flatno");
        strUsrType = mIntent.getStringExtra("usertype");
        strLastName = mIntent.getStringExtra("LastName");
        strUserID = mIntent.getStringExtra("UserID");
        strResID = mIntent.getStringExtra("ResiID");
        selectedSeverity = mIntent.getStringExtra("Severity");
    }


    public Bundle CreateBundle(){
        Bundle myData = new Bundle();
        myData.putString("UserID", strUserID);
        myData.putString("ResiID", strResID);
        myData.putString("name", strFirstName);
        myData.putString("mobileno", MobileNo);
        myData.putString("LastName", strLastName);
        myData.putString("EmailId", "abc@xyz.in");
        myData.putString("SocietyName", strSocietyName);;
        myData.putString("flatno", strFlatNumber);
        myData.putString("usertype", strUsrType);
        myData.putString("Category", selectedCategory);
        myData.putString("Severity", selectedSeverity);
        myData.putString("FromActivity", "NoticeActivity");
        return myData;
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("msg");
            //do other stuff here
        }
    };

    @Override
    public void OnMessageReceived(String Message) {
        try {
            _databaseAccess = new DataAccess(getApplicationContext());
            _databaseAccess.open();
            listMessages = _databaseAccess.getAllNotice(socUser.SocietyId);
            _databaseAccess.close();
            adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            int a =1;
        }
    }

    private void GetImages(final int ID)
    {
        String url = ApplicationConstants.APP_SERVER_URL+"/api/Image/GetByResID/" + ID ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int id = jObject.getInt("ID");
                    String strImg = jObject.getString("ImageString");
                    da.insertImage(id,strImg);
                    da.close();
                    hmImage.remove(ID);
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e)
                {

                }

                catch (Exception ex)
                {
                    int b =8;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
    }

    public void getNoticeFromServer( final int notice_id, final String file_name)
    {

        String url =  ApplicationConstants.APP_SERVER_URL + "/api/Notifications";
        String reqBody = "{\"Notice_ID\":\""+ notice_id + "\",\"FileName\":\""+ file_name +  "\"}";
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,reqBody , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObj) {

                try{
                    String ImageByte =jObj.getString("imageByte");
                    String fileName = notice_id +"__" +file_name;
                    byte[] imgByte = Base64.decode(ImageByte, Base64.DEFAULT);
                    ImageServer.SaveFileToExternal(imgByte, fileName, getApplicationContext());
                }
                catch (JSONException e)
                {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();

            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************
    }
}

