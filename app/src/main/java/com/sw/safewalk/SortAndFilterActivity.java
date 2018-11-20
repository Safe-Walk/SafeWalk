package com.sw.safewalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class SortAndFilterActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int distance;
    private boolean recent, danger;
    private ArrayList<Incident> sortArray = new ArrayList();
    private ArrayList<Incident> sortedArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_and_filter);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        LinearLayoutManager lln = new LinearLayoutManager(this);
        lln.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lln);

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
                sortArray();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void sortArray() {
        if(danger) {
            CountingSort s = new CountingSort();
            s.sort(sortArray);
            sortedArray = s.getSortedArray();
        } else if(recent) {
            QuickSort s = new QuickSort();
            s.sort(sortArray, 0, sortArray.size() - 1);
            sortedArray = s.getSortedArray();
        }

        MyAdapter adapter = new MyAdapter(this, sortedArray);
        mRecyclerView.setAdapter(adapter);
    }
}
