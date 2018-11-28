package com.sw.safewalk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
    private final String[] items = new String[]{"Assalto","Assédio", "Furto", "Homicídio", "Outro", "Preconceito", "Roubo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        final SeekBar distance = findViewById(R.id.distance);
        final SeekBar time = findViewById(R.id.time);
        final TextView maxDistance = findViewById(R.id.maxDistance);
        final TextView maxTime = findViewById(R.id.maxTime);

        final Button btnDate = findViewById(R.id.btnDate);
        final Button btnDanger = findViewById(R.id.btnDanger);
        final Button btnFilter = findViewById(R.id.btnFilter);

        Spinner dropdown = findViewById(R.id.typeOfCrime);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

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

        time.setMax(30);
        time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar time, int progress, boolean b) {
                if(progress == 0 || progress == 1) {
                    maxTime.setText(String.valueOf(progress) + " dia");
                } else {
                    maxTime.setText(String.valueOf(progress) + " dias");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar distance) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar distance) {

            }
        });

        btnDanger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortData.setRecent(false);
                sortData.setDanger(true);

                if(sortData.getDanger()) {
                    btnDanger.getBackground().setColorFilter(Color.parseColor("#B4EEF7"), PorterDuff.Mode.MULTIPLY);
                    btnDate.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
                }

            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortData.setRecent(true);
                sortData.setDanger(false);

                if(sortData.getRecent()) {
                    btnDate.getBackground().setColorFilter(Color.parseColor("#B4EEF7"), PorterDuff.Mode.MULTIPLY);
                    btnDanger.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
                }
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Spinner typeOfCrime = findViewById(R.id.typeOfCrime);

                if(maxDistance.getText() == "") {
                    maxDistance.setText("0 km");
                }

                if(maxTime.getText() == "") {
                    maxTime.setText("0 dias");
                }

                sortData.setTime(Long.valueOf(maxTime.getText().toString().split(" ")[0]));
                sortData.setTypeOfCrime(typeOfCrime.getSelectedItem().toString().trim());
                sortData.setDistance(parseInt(maxDistance.getText().toString().split(" ")[0]));
                Intent intent = new Intent(getApplicationContext(), SortAndFilterActivity.class);
                intent.putExtra("SortData", (Serializable) sortData);
                startActivity(intent);
            }
        });
    }
}