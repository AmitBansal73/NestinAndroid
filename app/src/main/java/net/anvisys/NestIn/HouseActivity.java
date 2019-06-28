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
import net.anvisys.NestIn.Register.RoleHomeActivity;
import net.anvisys.NestIn.Register.SelectRoleActivity;
import net.anvisys.NestIn.Register.SettingActivity;

public class HouseActivity extends AppCompatActivity {

    TextView txtHouseMessage, txtMyRoles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        txtHouseMessage = findViewById(R.id.txtHouseMessage);
        txtMyRoles = findViewById(R.id.txtMyRoles);

        txtHouseMessage.setText("For User With Individual House, Please use WebApplication. To add Flat click on below link");

        txtMyRoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roleIntent = new Intent(HouseActivity.this, SelectRoleActivity.class);
                startActivity(roleIntent);
                HouseActivity.this.finish();
            }
        });
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

            HouseActivity.this.finish();
        }
        if(id == R.id.action_settings)
        {
            Intent settingIntent = new Intent(HouseActivity.this, SettingActivity.class);
            startActivity(settingIntent);

        }
        if (id == R.id.action_LogOff) {

            AlertDialog.Builder builder= new AlertDialog.Builder(
                    HouseActivity.this);
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


}
