package com.sw.safewalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class SortAndFilterActivity extends AppCompatActivity {
    private int distance;
    private boolean recent, danger;
    private ArrayList<Incident> sortArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("entrou","");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_and_filter);

        danger = Boolean.parseBoolean(getIntent().getSerializableExtra("SortData").toString().split(" ")[0]);
        distance = parseInt(getIntent().getSerializableExtra("SortData").toString().split(" ")[1]);
        recent = Boolean.parseBoolean(getIntent().getSerializableExtra("SortData").toString().split(" ")[2]);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("incidentes/listaOcorrencia");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Incident in = snap.getValue(Incident.class);
                    sortArray.add(in);
                }
                typeOfSort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }

    public void typeOfSort() {
        Log.d("entrou","");
        if (danger) {

            CountingSort s = new CountingSort();
            s.sort(sortArray);

        } else {
            QuickSort s = new QuickSort();
        }
    }
}
