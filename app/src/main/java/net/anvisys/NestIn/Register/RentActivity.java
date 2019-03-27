package net.anvisys.NestIn.Register;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import net.anvisys.NestIn.R;

public class RentActivity extends AppCompatActivity {

    SearchView searchView;
    Spinner spRentType,spRoomType;
    EditText txtCity,txtRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(" NestIn ");
        actionBar.show();

        spRentType = findViewById(R.id.spRentType);
        spRoomType = findViewById(R.id.spRoomType);
        txtCity = findViewById(R.id.txtCity);
        txtRent = findViewById(R.id.txtCity);
        searchView = findViewById(R.id.searchView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rentType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRentType.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.roomType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomType.setAdapter(adapter1);
    }
}
