package net.anvisys.NestIn.Forum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Custom.OvalImageView;
import net.anvisys.NestIn.Object.Forum;
import net.anvisys.NestIn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ForumCompActivity extends AppCompatActivity {
    //String strUserID ,strResID,strFirstName, strLastName ,strFlatNumber, strUsrType , MobileNo, strSocietyName;
    int ThreadID;
    int Comments_Count;
    String Topic;
    String thisPost;
    ListView forumCompleteView;
    EditText newPost;
    ImageView sendButton;
    EachRow each;
    ArrayList<EachRow> arraylist=new ArrayList<EachRow>();
    HashMap<Integer,List<ImageThread>> hmImage=  new HashMap<>();
    int networkCounter =0;
    MyAdapter adapter;

    ProgressBar listPrgBar;
    ProgressBar postPrgBar;
    Profile myProfile;
    SocietyUser socUser;
    Forum FirstForum;

    ImageView imgFirstUser;
    TextView firstResident,firstPost,txtDay,txtMonth,txtTime,imageText;
    Bitmap myBmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_comp);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Forum Details");
        actionBar.show();

       // InitializeVariableFromIntent();
        Intent mIntent = getIntent();
       // ThreadID = mIntent.getIntExtra("Thread_ID", 0);
       // Comments_Count =  mIntent.getIntExtra("Comments_Count", 0);
        FirstForum =  (Forum)mIntent.getSerializableExtra("forum");
        myProfile = Session.GetUser(this);
        socUser = Session.GetCurrentSocietyUser(this);

        txtDay = findViewById(R.id.txtDay);
        txtMonth = findViewById(R.id.txtMonth);
        txtTime = findViewById(R.id.txtTime);
        txtDay = findViewById(R.id.txtDay);
        // set the initial comment
        imgFirstUser=findViewById(R.id.imgFirstUser);

        firstResident = findViewById(R.id.txtFirstUser);

        firstResident.setText(FirstForum.FirstUser + ", " + FirstForum.FirstFlat );
        firstPost = findViewById(R.id.txtFirstPost);
        firstPost.setText(FirstForum.First_Post);

        txtDay.setText(Utility.GetDayOnly(FirstForum.First_Date));
        txtMonth.setText(Utility.GetMonthOnly(FirstForum.First_Date));
        txtTime.setText(Utility.GetTimeOnly(FirstForum.First_Date));
       // imageText= findViewById(R.id.imageText);
        forumCompleteView = findViewById(R.id.forumCompList);
        newPost = findViewById(R.id.txtNewComment);
        sendButton = findViewById(R.id.sendButton);
        listPrgBar=findViewById(R.id.listPrgBar);
        listPrgBar.setVisibility(View.GONE);

        sendButton.setOnClickListener(new clicker());

       // myBmp =  ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, this);

        String url1 = "http://www.Nestin.online/ImageServer/User/" + FirstForum.First_userID +".png";
        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(imgFirstUser);

      /*  if(socUser.ResID == FirstForum.First_Res_Id)
        {
            if(myBmp!=null) {
                imgFirstUser.setImageBitmap(myBmp);
            }
        }
        else {
            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            String img = da.GetImage(FirstForum.First_Res_Id);
            da.close();
            Bitmap bmpUser = ImageServer.getBitmapFromString(img, getApplicationContext());
            if (bmpUser != null) {
                imgFirstUser.setImageBitmap(bmpUser);
            }
        } */

        if(FirstForum.Comments_Count == 1)
        {
            forumCompleteView.setVisibility(View.INVISIBLE);
        }
        else if(FirstForum.Comments_Count == 2)
        {
            EachRow each2=new EachRow();
            ThreadID=FirstForum.thread_ID;
            each2.ID =0;
            Topic=FirstForum.Topic;
            //each2.Last_Name=FirstForum.LatestUser;
            each2.First_Name=FirstForum.LatestUser;
            each2.Flat=FirstForum.LatestFlat;
            each2.Thread=FirstForum.Latest_Post;
            each2.Updated_On=FirstForum.Latest_Date;
            each2.userID =FirstForum.Last_UserID;

            arraylist.add(each2);
            adapter =new MyAdapter(ForumCompActivity.this,0, 0, arraylist);
            forumCompleteView.setAdapter(adapter );
            adapter.notifyDataSetChanged();

        }
        else {
            LoadThread();
        }
    }

    private void LoadThread()
    {
        listPrgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL + "/api/forum/" +socUser.SocietyId+"/Thread/"+ FirstForum.thread_ID ;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jArray) {

                try{
                   JSONArray json = jArray.getJSONArray("$values");
                    int x = json.length();
                    for(int i = 0; i < x; i++){
                        JSONObject jObj = json.getJSONObject(+i);
                        each=new EachRow();
                        ThreadID=jObj.getInt("ThreadID");
                        each.ID = jObj.getInt("ID");

                        if(FirstForum.FirstID != each.ID)
                        {
                            Topic=jObj.getString("Topic");
                           // each.Last_Name=jObj.getString("LastName");
                            each.First_Name=jObj.getString("FirstName");
                            each.Flat=jObj.getString("FlatNumber");
                            each.Thread=jObj.getString("Thread");
                            each.Updated_On=jObj.getString("UpdatedOn");
                            each.ResID =jObj.getInt("ResID");
                            each.userID = jObj.getInt("UserID");
                            if(arraylist.size()==0)
                            {
                                adapter =new MyAdapter(ForumCompActivity.this,0, 0, arraylist);
                                forumCompleteView.setAdapter(adapter );
                            }
                            arraylist.add(each);
                        }
                    }
                    adapter =new MyAdapter(ForumCompActivity.this,0, 0, arraylist);
                    forumCompleteView.setAdapter(adapter );
                    adapter.notifyDataSetChanged();
                    listPrgBar.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    listPrgBar.setVisibility(View.INVISIBLE);
                }
                catch (Exception ex){
                    listPrgBar.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                listPrgBar.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);

        //*******************************************************************************************************

    }

    class EachRow
    {
        String First_Name,aLast_Name,Thread,Updated_On, Flat, strImage;
        Integer ResID, ID, Thread_ID,userID;
    }

    private class ViewHolder
    {
        TextView txtResident,txtday,txtMonth,txtTime,txtComments,imageText;

        OvalImageView resiImage;
        View rowView;

    }

    private class ImageThread
    {
        int Res_Id, Forum_ID;
    }

    class MyAdapter extends ArrayAdapter<EachRow>{

        LayoutInflater inflat;
        ViewHolder holder;

        public MyAdapter(Context context, int resource, int textViewResourceId, ArrayList<EachRow> objects) {

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
                View rowView=null;
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_forum_details, null);
                    holder = new ViewHolder();
                    holder.txtResident =  convertView.findViewById(R.id.txtResident);
                    holder.txtday =  convertView.findViewById(R.id.txtday);
                    holder.txtMonth =  convertView.findViewById(R.id.txtMonth);
                    holder.txtTime =  convertView.findViewById(R.id.txtTime);
                    holder.txtComments =  convertView.findViewById(R.id.txtComments);
                    holder.resiImage =  convertView.findViewById(R.id.resiImage);
                   // holder.imageText =  convertView.findViewById(R.id.imageText);
                    convertView.setTag(holder);
                }

                holder = (ViewHolder) convertView.getTag();
                EachRow row = getItem(position);
                // Log.d("Dish Name", row.complaint_type);

                String strRes = row.First_Name + ", " + row.Flat;
                holder.txtResident.setText(strRes);
                holder.txtComments.setText(row.Thread);
                holder.txtday.setText(Utility.GetDayOnly(row.Updated_On));
                holder.txtMonth.setText(Utility.GetMonthOnly(row.Updated_On));
                holder.txtTime.setText(Utility.GetTimeOnly(row.Updated_On));

              /*  holder.imageText.setText(row.First_Name.substring(0,1));
                int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                holder.imageText.setBackgroundColor(randomAndroidColor);  */
               // holder.imageText.setBackgroundResource(R.drawable.circular_background);

             /*   if(FirstForum.First_userID == row.userID)
                {
                    holder.resiImage.setImageBitmap(myBmp);
                }

                else {  */

                    String url1 = "http://www.Nestin.online/ImageServer/User/" + row.userID +".png";
                    Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(holder.resiImage);

                /*    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    String img = da.GetImage(row.ResID);

                   try {
                        if (img.matches("") || img == null) {
                            if (hmImage.containsKey(row.ResID)) {
                                ImageThread imgT = new ImageThread();
                                imgT.Res_Id = row.ResID;
                                imgT.Forum_ID = row.ID;
                                List<ImageThread> ImageList = hmImage.get(row.ResID);
                                ImageList.add(imgT);


                            } else {
                                List<ImageThread> ImageList = new ArrayList<ImageThread>();
                                ImageThread imgT = new ImageThread();
                                imgT.Res_Id = row.ResID;
                                imgT.Forum_ID = row.ID;
                                ImageList.add(imgT);
                                hmImage.put(imgT.Res_Id, ImageList);
                                GetImages(row.ResID, row.ID, row.First_Name.substring(0,1));
                                holder.imageText.setVisibility(View.GONE);
                            }
                        } else {
                            Bitmap bmp = ImageServer.getBitmapFromString(img, getContext());
                            holder.resiImage.setImageBitmap(bmp);
                            holder.imageText.setVisibility(View.GONE);
                        }

                    }
                    catch (Exception ex)
                    {
                        int a=1;
                    }  */
               // }

                return convertView;
            }

            catch (Exception ex)

            {
                Toast.makeText(getApplicationContext(),"Could not Load Forum Data", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        public int getPosition(EachRow item) {
            return super.getPosition(item);
        }

        @Override
        public EachRow getItem(int position) {
            return arraylist.get(position);
        }
    }

    protected void SendReply(View v)
    {
        try {
            thisPost = newPost.getText().toString();
            AddComment();
        }
        catch (Exception ex)
        {

        }

    }

    class  clicker implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (v == sendButton) {
               // Toast.makeText(getApplicationContext(), "Post Button Clicked", Toast.LENGTH_SHORT).show();
               // Log.i("Add Post User Name:", strFirstName);
                // System.out.println("AddComplaintsActivity Login User Name:"+logUserName);
                thisPost = newPost.getText().toString();
                AddComment();

            }
        }
    }


    private void   AddComment()
    {
        listPrgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/forum/NewForum";
        try {
        String reqBody = "{\"resID\":\""+ socUser.ResID +"\",\"SocietyID\":\"" + socUser.SocietyId + "\", \"ThreadID\":\""+ FirstForum.thread_ID + "\",\"Topic\":\"General\",\"CurrentThread\":\""+ thisPost + "\"}";

            JSONObject jsRequest = new JSONObject(reqBody);
            //-----------------------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getApplicationContext(), "Post Added Successfully.",
                            Toast.LENGTH_SHORT).show();
                    listPrgBar.setVisibility(View.GONE);
                    int id = 10000;
                    try
                    {
                        id= jObj.getInt("ThreadID");
                    }
                    catch (JSONException js)
                    {

                    }
                    EachRow row = new EachRow();
                    row.ID = 11000;
                    row.Thread = thisPost;
                    row.Thread_ID = id;
                    row.First_Name = myProfile.NAME;
                    row.strImage = "";
                    row.Updated_On = Utility.CurrentDate();
                    row.ResID = socUser.ResID ;
                    row.Flat = socUser.FlatNumber;
                    arraylist.add(row);
                    newPost.setText("");

                    if (adapter==null){
                        adapter =new MyAdapter(ForumCompActivity.this,0, 0, arraylist);
                        forumCompleteView.setAdapter(adapter );
                    }
                 adapter.notifyDataSetChanged();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Post could not be submitted : Network Error",
                            Toast.LENGTH_LONG).show();
                    listPrgBar.setVisibility(View.GONE);
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

            jsArrayRequest.setRetryPolicy(rPolicy);

            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            listPrgBar.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            listPrgBar.setVisibility(View.GONE);
        }
        finally {

        }
    }


    private void GetImages(final int Res_ID,final int Thread_id, final String FirstLetter)
    {
        String url = ApplicationConstants.APP_SERVER_URL+"/api/Image/" + Res_ID ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    JSONArray jArray =  jObject.getJSONArray("$values");
                    String strImg = "";
                    int x = jArray.length();
                    if (x!=0 ) {
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();

                    for(int i = 0; i < x; i++){
                        JSONObject jTypeObj = jArray.getJSONObject(i);
                        int id = jTypeObj.getInt("ID");
                        strImg = jTypeObj.getString("ImageString");
                        da.insertImage(id,strImg);
                    }
                    da.close();
                    }
                    else {
                        strImg = FirstLetter;
                        imageText.setText(FirstLetter);
                        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                        imageText.setBackgroundColor(randomAndroidColor);
                    }
                    List<ImageThread> ImageList = hmImage.get(Res_ID);

                        for ( ImageThread imgt: ImageList) {

                            EachRow row =   arraylist.get(imgt.Forum_ID);
                            row.strImage = strImg;

                        }

                        hmImage.remove(Res_ID);

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent frmActivity = new Intent(ForumCompActivity.this,
                    ForumActivity.class);
           // Bundle myData = CreateBundle();
           // frmActivity.putExtras(myData);
            startActivity(frmActivity);
            ForumCompActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
