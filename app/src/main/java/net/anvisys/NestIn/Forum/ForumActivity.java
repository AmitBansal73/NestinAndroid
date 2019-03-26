package net.anvisys.NestIn.Forum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.ImageServer;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.DashboardActivity;
import net.anvisys.NestIn.Object.Forum;
import net.anvisys.NestIn.R;
import net.anvisys.NestIn.Summary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class ForumActivity extends AppCompatActivity implements Summary.SummaryListener {

    ListView forumListView;
    Button  btnNew;
    String  strFirstName, strLastName, strFlatNumber, strUsrType, MobileNo, strSocietyName;
    Forum eachForum;
    LinkedHashMap<Integer, Forum>  listForum=  new LinkedHashMap<>();
    HashMap<Integer, List<ImageThread>>  hmImage=  new HashMap<>();
   // List<Forum> listForum=new ArrayList<Forum>();

    List<String> ListComments = new ArrayList<String>();

    int networkCounter =0;
    int threadID;
    int Comments_Count;
    ProgressBar prgBar;
    MyAdapter adapter;

    ImageView myImage;
    EditText myPostText;
    ImageView sendImage;
    Spinner spinnerCategory;
    String selectedCategory;
    View getForumView;
    FloatingActionButton fab;

    String textThread;
    int BatchCount=10;
    int EndIndex =10;
    int StartIndex =0;
    int GetCount=10;
    SocietyUser socUser;
    Profile myProfile;
    String LastForumRefreshTime = "01/01/2000 12:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
            try {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Forum");
                actionBar.show();
                prgBar = findViewById(R.id.progressBar);
                prgBar.setVisibility(View.GONE);


                fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent AddComplainActivity = new Intent(ForumActivity.this, AddForumActivity.class);
                        startActivity(AddComplainActivity);
                    }
                });

                // InitializeVariableFromIntent();
                myProfile = Session.GetUser(this);
                socUser = Session.GetCurrentSocietyUser(this);
                strSocietyName = socUser.SocietyName;
                MobileNo = myProfile.MOB_NUMBER;
                //myImage = (ImageView)findViewById(R.id.myImage);

                try {
                    String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
                    Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(myImage);

                  /*  Bitmap bmp = ImageServer.GetImageBitmap(myProfile.MOB_NUMBER, getApplicationContext());
                    if (bmp != null) {
                        myImage.setImageBitmap(bmp);
                    } */
                } catch (Exception ex) {

                }


                forumListView = (ListView) findViewById(R.id.listViewForum);
                forumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Forum itemValue = (Forum) forumListView.getItemAtPosition(position);
                        threadID = itemValue.thread_ID;
                        Comments_Count = itemValue.Comments_Count;
                        Intent ForumCompActivity = new Intent(ForumActivity.this, ForumCompActivity.class);
                        Bundle myData = new Bundle();
                        myData.putSerializable("forum", itemValue);
                        myData.putInt("Thread_ID", threadID);
                        myData.putInt("Comments_Count", threadID);
                        ForumCompActivity.putExtras(myData);
                        startActivity(ForumCompActivity);
                        ForumActivity.this.finish();
                    }
                });

                DataAccess da = new DataAccess(this);
                da.open();
                listForum = da.getAllForum(socUser.SocietyId);
                da.close();

                adapter = new MyAdapter(ForumActivity.this, 0, 0, listForum);
                forumListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                Summary.RegisterSummaryListener(this);
                //  getForumView = (View)findViewById(R.id.getForumView);
                if (Utility.IsConnected(getApplicationContext()) && ApplicationVariable.AUTHENTICATED == true) {
                    LoadRecentData();
                }
            }
            catch (Exception ex)
            {
               Log.i("Create View Forum","Exception Thrown");
            }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            if (requestCode == resultCode) {
                Forum newRow = new Forum();
                newRow.thread_ID = 1001;
                newRow.First_Post = data.getStringExtra("CurrentThread");
                newRow.FirstFlat = strFlatNumber;
                newRow.FirstUser = strFirstName;
                newRow.First_Date = Utility.CurrentDate();
                newRow.Topic = data.getStringExtra("Topic");
                listForum.put(newRow.thread_ID, newRow);
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception ex)
        {
            Log.i("onActivityResult","Exception Thrown");
        }
    }


    private void LoadForums(final int firstIndex, final int LastIndex)
    {
        try {

            prgBar.setVisibility(View.VISIBLE);
            String url = ApplicationConstants.APP_SERVER_URL + "/api/ForumDiff/NewForumDiff";

            String reqBody = "{\"StartIndex\":" + firstIndex + ",\"EndIndex\":" + LastIndex + ",\"SocietyID\":" + socUser.SocietyId + ",\"LastRefreshTime\":\"\"}";
            JSONObject jsRequest = null;

            try {
                jsRequest = new JSONObject(reqBody);
            } catch (JSONException jex) {

            }
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        JSONArray json = jsonObject.getJSONArray("$values");
                        GetCount = json.length();

                        if (firstIndex == 0 && GetCount > 0) {
                            listForum.clear();
                        }
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();
                        for (int i = 0; i < GetCount; i++) {
                            JSONObject jObj = json.getJSONObject(i);
                            eachForum = new Forum();
                            eachForum.FirstID = jObj.getInt("FirstID");
                            eachForum.thread_ID = jObj.getInt("ThreadID");
                            eachForum.Topic = jObj.getString("Topic");
                            eachForum.FirstUser = jObj.getString("Initiater");
                            eachForum.FirstFlat = jObj.getString("firstFlat");
                            eachForum.First_Post = jObj.getString("FirstThread");
                            eachForum.First_Date = jObj.getString("InitiatedAt");
                            eachForum.First_Res_Id = jObj.getInt("FirstResID");
                            eachForum.First_userID = jObj.getInt("First_User_ID");
                            eachForum.FirstUserImage = "";
                            // each.FirstUserImage = jObj.getString("FirstImage");
                            eachForum.LatestUser = jObj.getString("CurrentPostBy");
                            eachForum.LatestFlat = jObj.getString("lastFlat");
                            eachForum.Latest_Post = jObj.getString("latestThread");
                            eachForum.Latest_Date = jObj.getString("UpdatedAt");
                            eachForum.Last_Res_Id = jObj.getInt("LastResID");
                            eachForum.Last_UserID = jObj.getInt("Last_User_ID");
                            eachForum.Comments_Count = jObj.getInt("Comments_Count");
                            eachForum.LatestUserImage = "";
                            //   each.LatestUserImage = jObj.getString("LastImage");
                            if (firstIndex == 0) {
                                da.insertNewForum(eachForum, socUser.SocietyId);
                            } else {
                                listForum.put(eachForum.thread_ID, eachForum);
                            }

                        }
                        if (GetCount > 0) {
                            Session.SetForumRefreshTime(getApplicationContext());
                            ApplicationConstants.FORUM_UPDATES = 0;
                        }
                        if (firstIndex == 0) {
                            da.LimitForumData();
                            listForum = da.getAllForum(socUser.SocietyId);
                        }
                        da.close();

                        adapter.notifyDataSetChanged();
                        prgBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        prgBar.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    prgBar.setVisibility(View.GONE);

                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0, -1, 0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }catch (Exception ex){
            Log.i("LoadForums","Exception Thrown");
        }

    }

    private class ViewHolder
    {
        TextView threadID, initiatorName,initialPost,latestComment,initialDate,latestDate, latestFrom;
        ImageView FirstUserImage, LatestUserImage;
        View   latestThreadView;
        TextView linkComment;
        ListView listComments;

    }

    private class ImageThread
    {
        int Res_Id, thread_ID;
        String ResOrder;
    }

    // Custom List Adapter Class
    class MyAdapter extends BaseAdapter {

        LayoutInflater inflat;
        ViewHolder holder;
        LinkedHashMap lhm;
        public MyAdapter(Context context, int resource, int textViewResourceId, HashMap<Integer,Forum> objects) {

           //  super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat= LayoutInflater.from(context);

             lhm = new LinkedHashMap(objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_forum, null);
                    holder = new ViewHolder();
                  //  holder.threadTopic =  convertView.findViewById(R.id.threadTopic);
                    holder.FirstUserImage = convertView.findViewById(R.id.userImage);
                    holder.initiatorName =  convertView.findViewById(R.id.initiatorName);
                    //holder.threadInitiator =  convertView.findViewById(R.id.threadInitiator);
                    holder.initialPost =  convertView.findViewById(R.id.initialPost);
                    holder.initialDate =  convertView.findViewById(R.id.initialDate);
                    holder.linkComment =  convertView.findViewById(R.id.txtComments);

                    holder.LatestUserImage = convertView.findViewById(R.id.latestUserImage);
                    holder.latestFrom = convertView.findViewById(R.id.latestFrom);
                    holder.latestDate =  convertView.findViewById(R.id.txtDateComment);
                    holder.latestComment =  convertView.findViewById(R.id.latestComment);

                    holder.threadID =  convertView.findViewById(R.id.threadID);
                    holder.latestThreadView = convertView.findViewById(R.id.latestThreadView);


                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();

                if(listForum.size() ==0)
                {
                    return convertView;
                }

                else if(position>=listForum.size() && listForum.size()!=0)
                {
                    holder.FirstUserImage.setVisibility(View.GONE);
                    holder.latestThreadView.setVisibility(View.GONE);
                   // if(position>= EndIndex )
                   // {
                        StartIndex = listForum.size();
                        if(BatchCount==GetCount)
                        {
                           EndIndex = StartIndex + GetCount;
                            LoadForums(StartIndex,EndIndex);
                            StartIndex = EndIndex;
                        }
                        else
                        {
                           EndIndex = StartIndex;
                            convertView.setVisibility(View.GONE);
                        }
                  //  }
                    //holder.threadTopic.setText("");
                    holder.initiatorName.setText("");
                    //holder.threadInitiator.setText("");
                    holder.initialPost.setText("");
                    holder.initialDate.setText("");
                    holder.latestComment.setText("");
                    holder.latestFrom.setText("");
                    return convertView;
                }
                else {
                    try {
                        Forum row = getItem(position);
                        // set main thread
                        Log.i("Forum", "set main thread");
                       // holder.threadTopic.setText(row.Topic);
                        holder.initiatorName.setText(row.FirstUser+", "+row.FirstFlat);
                        //holder.threadInitiator.setText(" from " + row.FirstFlat + " started discussion on ");
                        holder.initialPost.setText(row.First_Post);
                        holder.initialDate.setText(Utility.ChangeFormat(row.First_Date));
                        holder.FirstUserImage.setVisibility(View.VISIBLE);

                        String url1 = "http://www.Nestin.online/ImageServer/User/" + row.First_userID +".png";
                        Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(holder.FirstUserImage);


                        holder.linkComment.setVisibility(View.VISIBLE);
                        holder.linkComment.setText(row.Comments_Count - 1 + " Comments");

                        // set latest thread
                        Log.i("Forum", "set latest thread");
                        if (row.Comments_Count ==1) {
                            holder.latestThreadView.setVisibility(View.GONE);
                        } else {

                            holder.latestThreadView.setVisibility(View.VISIBLE);
                            holder.latestComment.setText(row.Latest_Post);
                            holder.latestFrom.setText( row.LatestUser + ", " + row.LatestFlat);// + " replied on " + Utility.ChangeFormat(row.Latest_Date)
                            holder.latestDate.setText(Utility.ChangeFormat(row.Latest_Date));

                            String url2 = "http://www.nestin.online/ImageServer/User/" + row.Last_UserID +".png";
                            Picasso.with(getApplicationContext()).load(url2).error(R.drawable.user_image).into(holder.LatestUserImage);


                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "error creating view", Toast.LENGTH_LONG).show();
                    }

                    return convertView;
                }
            }

            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Could not Load Forum Data", Toast.LENGTH_LONG).show();
              return null;
            }

        }

        @Override
        public Forum getItem(int position) {

            if(position >=listForum.size() )
            {
                Toast.makeText(getApplicationContext(),"Loading Data",Toast.LENGTH_LONG).show();
                return (Forum) listForum.values().toArray()[position];
            }
            else
            {
                return (Forum) listForum.values().toArray()[position];
            }

        }

        @Override
        public int getCount() {
            if(BatchCount==GetCount)
            {
                return listForum.size()+1;
            }
            else
            {
                return listForum.size();
            }


           // return listForum.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent forumActivity = new Intent(ForumActivity.this,
                    DashboardActivity.class);

            forumActivity.putExtra("PARENT","FORUM");

            startActivity(forumActivity);
            ForumActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void GetImages(final int ID,final int Thread_id,final String User)
    {
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Image/Res/" + ID ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                     int id = jObject.getInt("ID");
                     String strImg = jObject.getString("ImageString");
                     da.insertImage(id,strImg);
                     da.close();
                     List<ImageThread> ImageList = hmImage.get(ID);
                    for ( ImageThread imgt: ImageList) {
                           Forum forum =   listForum.get(imgt.thread_ID);
                            if(imgt.ResOrder.matches("First"))
                            {
                                forum.FirstUserImage = strImg;
                            }
                            else if(imgt.ResOrder.matches("Last"))
                            {
                                forum.LatestUserImage = strImg;
                            }
                        }
                        hmImage.remove(ID);
                        adapter.notifyDataSetChanged();
                        }
                catch (JSONException e)
                {

                }

                catch (Exception ex)
                {
                    Log.i("GetImages","Exception Thrown");
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


    @Override
    public void OnSummaryReceived() {
        try {

            if (ApplicationConstants.FORUM_UPDATES != 0) {
                StartIndex = 0;

                if (ApplicationConstants.FORUM_UPDATES > BatchCount) {
                    EndIndex = StartIndex + BatchCount;
                } else {
                    EndIndex = StartIndex + ApplicationConstants.FORUM_UPDATES;
                }
                // LoadForums(StartIndex,EndIndex);

                StartIndex = EndIndex;
            }
        }catch (Exception ex){
            Log.i("OnSummaryReceived","Exception Thrown");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Summary.RemoveSummaryListener();
    }

    private void   AddNewPost()
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/forum/NewForum";
        String reqBody = "{\"resID\":\""+ socUser.ResID +"\",\"Topic\":\""+ selectedCategory + "\",\"CurrentThread\":\""+ textThread + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getApplicationContext(), "Post Submitted Successfully.",
                            Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    int id = -10;
                    try
                    {
                       id= jObj.getInt("ThreadID");
                    }
                    catch (JSONException js)
                    {}

                    Forum newRow = new Forum();
                    newRow.thread_ID = id ;
                    newRow.First_Post = textThread;
                    newRow.FirstFlat = strFlatNumber;
                    newRow.FirstUser = strFirstName;
                    newRow.First_Date = Utility.CurrentDate();
                    newRow.Topic = selectedCategory;
                    newRow.Comments_Count=1;
                    //newRow.First_Res_Id = Integer.parseInt(socUser.ResID);
                    newRow.FirstUserImage = "";
                    // each.FirstUserImage = jObj.getString("FirstImage");
                    newRow.LatestUser = strFirstName;
                    newRow.LatestFlat = strFlatNumber;
                    newRow.Latest_Post=textThread;
                    newRow.Latest_Date=Utility.CurrentDate();
                    //newRow.Last_Res_Id=Integer.parseInt(socUser);
                    newRow.LatestUserImage = "";

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    da.insertNewForum(newRow, socUser.SocietyId);

                    listForum.clear();
                    listForum = da.getAllForum(socUser.SocietyId);
                    da.close();
                    //listForum.put(newRow.thread_ID, newRow);
                    adapter.notifyDataSetChanged();
                    myPostText.setText("");
                    myPostText.clearFocus();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    prgBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Post could not be submitted : Try Again",
                            Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            prgBar.setVisibility(View.GONE);

        }

        finally {

        }


    }


    private void LoadRecentData()
    {

        //getForumView.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL + "/api/ForumDiff/NewForumDiff" ;
        LastForumRefreshTime = Session.GetForumRefreshTime(getApplicationContext());
        //LastForumRefreshTime = "01/01/2000 12:00:00";
        String reqBody = "{\"LastRefreshTime\":\""+ LastForumRefreshTime+"\",\"SocietyID\":\""+ socUser.SocietyId +"\"}";
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
            public void onResponse(JSONObject jArray) {

                try{
                    JSONArray json = jArray.getJSONArray("$values");
                    int Count = json.length();

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    for(int i = 0; i < Count; i++){
                        JSONObject jObj = json.getJSONObject(i);
                        eachForum=new Forum();
                        eachForum.FirstID=jObj.getInt("FirstID");
                        eachForum.thread_ID=jObj.getInt("ThreadID");
                        eachForum.Topic=jObj.getString("Topic");
                        eachForum.FirstUser=jObj.getString("Initiater");
                        eachForum.FirstFlat=jObj.getString("firstFlat");
                        eachForum.First_Post=jObj.getString("FirstThread");
                        eachForum.First_Date=jObj.getString("InitiatedAt");
                        eachForum.First_Res_Id=jObj.getInt("FirstResID");
                        eachForum.First_userID=jObj.getInt("FirstUserID");
                        eachForum.Last_UserID=jObj.getInt("LastUserID");
                        eachForum.FirstUserImage = "";
                        // each.FirstUserImage = jObj.getString("FirstImage");
                        eachForum.LatestUser = jObj.getString("CurrentPostBy");
                        eachForum.LatestFlat = jObj.getString("lastFlat");
                        eachForum.Latest_Post=jObj.getString("latestThread");
                        eachForum.Latest_Date=jObj.getString("UpdatedAt");
                        eachForum.Last_Res_Id=jObj.getInt("LastResID");
                        eachForum.Comments_Count = jObj.getInt("commentsCount");
                        eachForum.LatestUserImage = "";
                        da.insertNewForum(eachForum, socUser.SocietyId);
                        listForum.put(eachForum.thread_ID,eachForum);
                    }
                    if (Count>=0)
                    {
                        Session.SetForumRefreshTime(getApplicationContext());
                        ApplicationConstants.FORUM_UPDATES =0;

                        da.LimitForumData();
                        listForum = da.getAllForum(socUser.SocietyId);
                        adapter.notifyDataSetChanged();
                    }
                   // getForumView.setVisibility(View.GONE);
                    prgBar.setVisibility(View.GONE);
                    da.close();
                }
                catch (JSONException e)
                {
                    prgBar.setVisibility(View.GONE);
                    //getForumView.setVisibility(View.GONE);
                }

            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgBar.setVisibility(View.GONE);
               //getForumView.setVisibility(View.GONE);
            }
        });

        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);

        jsArrayRequest.setRetryPolicy(rPolicy);

        queue.add(jsArrayRequest);
        //*******************************************************************************************************

    }

}
