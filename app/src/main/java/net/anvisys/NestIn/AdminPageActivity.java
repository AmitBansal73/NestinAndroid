package net.anvisys.NestIn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Register.SettingActivity;

public class AdminPageActivity extends AppCompatActivity {

    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);


        txtMessage = findViewById(R.id.txtMessage);
        txtMessage.setText("The Admin Functionalities are available only on Web Apllication");
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

            AdminPageActivity.this.finish();
        }
        if(id == R.id.action_settings)
        {
            Intent settingIntent = new Intent(AdminPageActivity.this, SettingActivity.class);
            startActivity(settingIntent);

        }
        if (id == R.id.action_LogOff) {

            AlertDialog.Builder builder= new AlertDialog.Builder(
                    AdminPageActivity.this);
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

                        }
                    });


            AlertDialog Alert = builder.create();

            Alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public  void Logoff(View v)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(
                AdminPageActivity.this);
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
                        AdminPageActivity.this.finish();
                    }
                });


        AlertDialog Alert = builder.create();

        Alert.show();
    }
}
