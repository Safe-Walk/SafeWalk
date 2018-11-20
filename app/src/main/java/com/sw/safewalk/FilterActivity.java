package com.sw.safewalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;

import static java.lang.Integer.parseInt;

public class FilterActivity extends AppCompatActivity {
    SortData sortData = new SortData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        final SeekBar distance = findViewById(R.id.distance);
        final TextView maxDistance = findViewById(R.id.maxDistance);

        Spinner spinner = (Spinner) findViewById(R.id.regionList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar distance, int progress, boolean b) {
                maxDistance.setText(String.valueOf(progress) + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar distance) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar distance) {

            }
        });

        final Button btnDanger = findViewById(R.id.btnDanger);
        btnDanger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortData.setRecent(false);
                sortData.setDanger(true);
            }
        });

        final Button btnDate = findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortData.setRecent(true);
                sortData.setDanger(false);
            }
        });

        final Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(maxDistance.getText() == "") {
                    // todo mudar para não filtrar
                    maxDistance.setText("0 km");
                }

                sortData.setDistance(parseInt(maxDistance.getText().toString().split(" ")[0]));
                Intent intent = new Intent(getApplicationContext(), SortAndFilterActivity.class);
                intent.putExtra("SortData", (Serializable) sortData);
                startActivity(intent);
            }
        });
    }
}