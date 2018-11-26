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
    private final String[] items = new String[]{"Assalto", "Roubo", "Furto", "Assédio", "Homicídio", "Preconceito", "Outro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        final SeekBar distance = findViewById(R.id.distance);
        final TextView maxDistance = findViewById(R.id.maxDistance);
        final Button btnDate = findViewById(R.id.btnDate);
        final Button btnDanger = findViewById(R.id.btnDanger);
        final Button btnFilter = findViewById(R.id.btnFilter);

        Spinner dropdown = findViewById(R.id.regionList);
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