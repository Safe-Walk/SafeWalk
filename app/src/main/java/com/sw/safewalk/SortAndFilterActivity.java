package com.sw.safewalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SortAndFilterActivity extends AppCompatActivity {
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        distance = getIntent().getSerializableExtra("SortData").toString().split(" ")[1];
        setContentView(R.layout.activity_sort_and_filter);

        Toast.makeText(this, getIntent().getSerializableExtra("SortData").toString(), Toast.LENGTH_LONG);
        Log.d("Distancia", distance);
    }
}
