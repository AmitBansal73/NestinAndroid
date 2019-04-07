package net.anvisys.NestIn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;

public class CarPoolActivity extends AppCompatActivity {
    ListView carListView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool);

        progressBar = findViewById(R.id.progressBar);
        carListView = findViewById(R.id.carListView);

    }
}
