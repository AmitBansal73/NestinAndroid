package net.anvisys.NestIn.Register;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.anvisys.NestIn.R;

public class RegisterDemoActivity extends AppCompatActivity {

    EditText txtMobile,txtEmail,txtFirstName,txtLastName,txtParentName,txtAddress;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_demo);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("NestIn");
        actionBar.show();

        txtMobile= findViewById(R.id.txtMobile);
        txtEmail= findViewById(R.id.txtEmail);
        txtFirstName= findViewById(R.id.txtFirstName);
        txtLastName= findViewById(R.id.txtLastName);
        txtParentName= findViewById(R.id.txtParentName);
        txtAddress= findViewById(R.id.txtAddress);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent purpose = new Intent(RegisterDemoActivity.this, demoActivity.class);
                startActivity(purpose);
            }
        });
    }
}
