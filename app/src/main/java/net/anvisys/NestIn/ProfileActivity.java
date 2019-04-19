package net.anvisys.NestIn;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import net.anvisys.NestIn.Custom.OvalImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    OvalImageView profileImage;
    TextView Mobile;
    TextView txtName;
    TextView txtEmail;
    TextView txtLocation;
    ListView flatViewList;
    EditText editName,editEmail,editLocation,editMobile;
    Profile myProfile;
    SocietyUser socUser;
    Bitmap newBitmap;
    Button btnImageUpload,btnUpdateProfile,btnCancel,btnOk;
    TextView txtProfileMessage,txtEditProfile,txtFlatNumber,editFlatNumber,changeRoleFlat,txtChangeRole,txtRoleType,txtIntercom,txtSocietyName;
    View txtProfile, editProfile,changeRole,viewRoles;
    String newMobile,newName,newLocation,newEmail,selectedCategory="";
    ProgressBar prgBar;

    String profileLocation,strImage, regID;

    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_IMAGE_CROP = 2;

    ArrayList<SocietyUser> societyUserList = new ArrayList<>();

    private static ProfileEditListener profileEditListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            Toolbar toolbar =  findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Profile");
            actionBar.show();

            changeRole = findViewById(R.id.changeRole);
            changeRoleFlat = findViewById(R.id.changeRoleFlat);
            flatViewList = findViewById(R.id.flatViewList);
            txtRoleType = findViewById(R.id.txtRoleType);
            txtIntercom= findViewById(R.id.txtIntercom);
            txtSocietyName = findViewById(R.id.txtSocietyName);
            txtChangeRole = findViewById(R.id.txtChangeRole);
            viewRoles = findViewById(R.id.viewRoles);

            changeRoleFlat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //changeRole.setVisibility(View.VISIBLE);
                    viewRoles.setVisibility(View.VISIBLE);
                }
            });
            prgBar =  findViewById(R.id.progressBar);
            prgBar.setVisibility(View.GONE);
            profileImage =  findViewById(R.id.profile_image);
            Mobile =  findViewById(R.id.txtmobile);
            txtName =  findViewById(R.id.txtName);
            txtFlatNumber = findViewById(R.id.txtFlatNumber);
            txtLocation =  findViewById(R.id.Location);
            txtEmail =  findViewById(R.id.txtEmail);
            txtEditProfile =  findViewById(R.id.txtEditProfile);
            txtEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileEdit();
               }
            });

            txtProfile = findViewById(R.id.ShowProfileContent);
            editProfile = findViewById(R.id.EditProfileContent);

            btnImageUpload =  findViewById(R.id.btnImageUpdate);
            socUser = Session.GetCurrentSocietyUser(this);
            myProfile = Session.GetUser(this);
            String url1 = "http://www.Nestin.online/ImageServer/User/" + myProfile.UserID +".png";
            Picasso.with(getApplicationContext()).load(url1).error(R.drawable.user_image).into(profileImage);


            Mobile.setText(myProfile.MOB_NUMBER);
            txtFlatNumber.setText(socUser.FlatNumber);
            txtSocietyName.setText(socUser.SocietyName);
            txtRoleType.setText(socUser.RoleType);
            txtIntercom.setText(socUser.intercomNumber);
            txtName.setText(myProfile.NAME);
            txtEmail.setText(myProfile.E_MAIL);
            txtLocation.setText(myProfile.LOCATION);


            DataAccess da = new DataAccess(getApplicationContext());
            da.open();
            societyUserList = da.getAllSocietyUser();

            if(societyUserList.size()<= 1){
                changeRoleFlat.setVisibility(View.GONE);
            }




            UserAdapter socUserAdapter = new UserAdapter(ProfileActivity.this, 0, societyUserList);
            flatViewList.setAdapter(socUserAdapter);

            flatViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SocietyUser currentResident = societyUserList.get(position);
                    txtFlatNumber.setText(currentResident.FlatNumber);
                    txtRoleType.setText(currentResident.RoleType);
                    txtSocietyName.setText(currentResident.SocietyName);
                    txtIntercom.setText(currentResident.intercomNumber);
                    Session.AddCurrentSocietyUser(getApplicationContext(), currentResident);
                    viewRoles.setVisibility(View.GONE);

                    if(profileEditListener!=null)
                    {
                        profileEditListener.OnChangeCurrentUser();
                    }
                }
            });


        }

        catch (Exception ex)
        {
            int a=1;
        }
    }


    public void UpdateProfile( View view)
    {
        try {

           if (myProfile.strImage == null  ) {
               txtProfileMessage.setVisibility(View.VISIBLE);
               txtProfileMessage.setText("Please Select Image");
               return;
            }
            else if(myProfile.strImage == "") {

             newName = editName.getText().toString().trim();
             newLocation = editLocation.getText().toString().trim();
             newEmail = editEmail.getText().toString().trim();
             newMobile = editMobile.getText().toString().trim();

               if (newName.matches(myProfile.NAME.trim()) && newLocation.matches(myProfile.LOCATION.trim()) && newEmail.matches(myProfile.E_MAIL)) {
                   txtProfileMessage.setVisibility(View.VISIBLE);
                   txtProfileMessage.setText("No Change in Data");
                   // Toast.makeText(this, "No Change in Data", Toast.LENGTH_LONG).show();
                   return;
               } else {

                   UpdateProfile();
               }
           }
        }
        catch (Exception ex)
        {
            Toast.makeText(this,"Failed to Update Profile", Toast.LENGTH_LONG).show();
        }
    }

    public void ProfileEdit()
    {

            if (txtProfile.getVisibility()==View.VISIBLE) {
                txtProfile.setVisibility(View.GONE);
            }
            if(editProfile.getVisibility() == View.GONE)
            {
                editName =  findViewById(R.id.editName);
                editEmail = (EditText) findViewById(R.id.editEmail);
                editLocation = (EditText) findViewById(R.id.editLocation);
                btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
                editFlatNumber = (TextView)findViewById(R.id.myFlatNumber);
                editMobile = (EditText)findViewById(R.id.editMobile);

                editName.setText(myProfile.NAME);
                editEmail.setText(myProfile.E_MAIL);
                editLocation.setText(myProfile.LOCATION);
                editFlatNumber.setText(socUser.FlatNumber);
                editMobile.setText(myProfile.MOB_NUMBER);
                txtProfileMessage = (TextView) findViewById(R.id.txtProfileMessage);
                editProfile.setVisibility(View.VISIBLE);
            }
            txtEditProfile.setVisibility(View.GONE);

    }

    private void UpdateProfile()
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +"/api/Resident/Update" ;
        String reqBody = "{\"MobileNumber\":\""+ newMobile + "\",\"UserID\":\""+ myProfile.UserID + "\",\"userName\":\""+ newName + "\",\"Email\":\""+ newEmail + "\",\"Location\":\""+ newLocation + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getApplicationContext(), "Updated Successfully.",
                            Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);

                    Profile myProfile = new Profile();
                    myProfile.NAME = newName;
                    myProfile.strImage = strImage;
                    myProfile.MOB_NUMBER = newMobile;
                    myProfile.LOCATION = profileLocation;
                    myProfile.E_MAIL = newEmail;
                    myProfile.REG_ID = regID;
                    ImageServer.SaveBitmapImage(newBitmap, myProfile.MOB_NUMBER, getApplicationContext());
                    //Session.AddUser(getApplicationContext(), myProfile);
                    Intent mainIntent = new Intent(ProfileActivity.this,DashboardActivity.class);
                    mainIntent.putExtra("parent","Profile");
                    startActivity(mainIntent);
                    ProfileActivity.this.finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
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

    public void EditImage(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_GET) {

                if (data != null) {
                    Uri uri = data.getData();
                    InputStream image_stream = getContentResolver().openInputStream(uri);
                    byte[] imgByte= ImageServer.getBytes(image_stream);
                    ImageServer.SaveFileToExternal(imgByte,"crop.jpg",getApplicationContext());
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File myDir = new File(root + "/SCM/crop.jpg");
                    myDir.mkdirs();
                    Uri contentUri = Uri.fromFile(myDir);
                    ImageCropFunction(contentUri);
                }
            } else if (requestCode == REQUEST_IMAGE_CROP) {

                if (data != null) {

                    Bundle bundle = data.getExtras();
                    if(bundle!= null) {
                        newBitmap = bundle.getParcelable("data");
                        profileImage.setImageBitmap(newBitmap);
                        btnImageUpload.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Uri cropUri =  data.getData();
                        InputStream image_stream = getContentResolver().openInputStream(cropUri);
                        newBitmap= BitmapFactory.decodeStream(image_stream);
                        profileImage.setImageBitmap(newBitmap);
                        btnImageUpload.setVisibility(View.VISIBLE);
                    }
                    strImage = ImageServer.getStringFromBitmap(newBitmap);
                    profileImage.invalidate();
                }
            }
        }
        catch (Exception ex)
        {
            int a=1;
        }
    }

    public void Image_Update(View v)
    {
        btnImageUpload.setVisibility(View.INVISIBLE);

        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL +  "/api/Image";
        String reqBody = "{\"UserID\":\""+ myProfile.UserID  + "\",\"ResID\":\""+ socUser.ResID + "\",\"ImageString\":\""+ strImage + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String Response = jObj.getString("Response");

                        if(Response.matches("OK"))
                        {
                            ImageServer.SaveBitmapImage(newBitmap,myProfile.MOB_NUMBER,getApplicationContext());

                        }
                        else if(Response.matches("Fail"))
                        {
                           String msg = jObj.getString("Message");
                            btnImageUpload.setVisibility(View.VISIBLE);
                            Log.i("Image Error:", msg);
                            Toast.makeText(getApplicationContext(),"Failed to Upload Image: Contact Admin ", Toast.LENGTH_LONG).show();

                        }
                    }
                    catch (JSONException jex)
                    {}

                    prgBar.setVisibility(View.GONE);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    btnImageUpload.setVisibility(View.VISIBLE);
                    prgBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Image Upload failed : Try Later", Toast.LENGTH_LONG).show();

                }
            });

            RetryPolicy policy = new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 8000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 3;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            };

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            btnImageUpload.setVisibility(View.VISIBLE);
            prgBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Image Upload failed : Try Later", Toast.LENGTH_LONG).show();
        }

        finally {

        }
    }

    public void ImageCropFunction(Uri uri) {

        // Image Crop Code
        try {
           // File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
           // File myDir = new File(root + "/SCM/");
           // myDir.mkdirs();

           // Uri contentUri = Uri.fromFile(myDir);

            Intent CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 100);
            CropIntent.putExtra("outputY", 100);
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
           // CropIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
           // CropIntent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(CropIntent, REQUEST_IMAGE_CROP);
        }
        catch (Exception e)
        {
         int a =1;
        }
    }


    private class FlatViewHolder
    {
        TextView txtUserFlat, txtUserRole, txtUserSocietyName, txtUserIntercom ;
        View statusBar;
    }

    // Custom List Adapter Class
    class UserAdapter extends ArrayAdapter<SocietyUser>
    {
        LayoutInflater inflat;
        FlatViewHolder flatHolder;
        public UserAdapter(Context context, int textViewResourceId,
                         List<SocietyUser> objects)
        {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            inflat=LayoutInflater.from(context);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                if (convertView == null) {
                    convertView = inflat.inflate(R.layout.row_item_profile_user_list, null);
                    flatHolder = new FlatViewHolder();
                    flatHolder.txtUserFlat =  convertView.findViewById(R.id.txtUserFlat);
                    flatHolder.txtUserSocietyName =  convertView.findViewById(R.id.txtUserSocietyName);
                    flatHolder.txtUserRole =  convertView.findViewById(R.id.txtUserRole);
                    flatHolder.txtUserIntercom = convertView.findViewById(R.id.txtUserIntercom);

                    convertView.setTag(flatHolder);
                }
                flatHolder = (FlatViewHolder) convertView.getTag();

                SocietyUser row = getItem(position);
                flatHolder.txtUserFlat.setText(row.FlatNumber);
                flatHolder.txtUserSocietyName.setText(row.SocietyName);
                flatHolder.txtUserRole.setText(row.RoleType);
                flatHolder.txtUserIntercom.setText(row.intercomNumber);


            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in creating List View", Toast.LENGTH_LONG).show();
            }
            return convertView;

        }

        @Override
        public SocietyUser getItem(int position) {
            // TODO Auto-generated method stub
            return societyUserList.get(position);
        }

        @Override
        public int getCount() {
            return societyUserList.size();
        }

    }


    interface ProfileEditListener{
            public void OnChangeCurrentUser();
    }

    public static  void RegisterChatListener( ProfileEditListener listener)
    {

        profileEditListener = listener;
    }


}
