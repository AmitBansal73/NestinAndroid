package net.anvisys.NestIn.Register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import net.anvisys.NestIn.R;

public class demoActivity extends AppCompatActivity {

    Spinner spPurpose,spFlats;
    Button btnSubmit;
    View rowflats;
    String selectedPuprose="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);



        btnSubmit = findViewById(R.id.btnSubmit);
        spPurpose = findViewById(R.id.spPurpose);
       // spFlats = findViewById(R.id.spFlats);
       // rowflats = findViewById(R.id.rowflats);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LoginPurpose, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spPurpose.setAdapter(adapter);

        spPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedPuprose = spPurpose.getSelectedItem().toString();
                rowflats.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

                rowflats.setVisibility(View.GONE);
            }
        });

    }
}
