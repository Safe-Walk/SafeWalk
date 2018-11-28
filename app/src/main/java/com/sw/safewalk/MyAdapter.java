package com.sw.safewalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Incident> mList;
    private LayoutInflater mLayoutInflater;

    public MyAdapter(Context c, ArrayList l) {
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // é chamado para criar uma nova view
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.i("LOG", "onCreateViewHolder");
        View v = mLayoutInflater.inflate(R.layout.item_incident, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    // vincula os dados da lista à view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Timestamp ts = new Timestamp(mList.get(position).getHorario());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY, HH:mm");

        myViewHolder.tvCrime.setText("Tipo do Crime: " + mList.get(position).getCrimeSelecionado());
        myViewHolder.tvDescription.setText("Descrição: " + mList.get(position).getDescricao());;
        myViewHolder.tvLevel.setText("Nível: " + mList.get(position).getNivel().toString());
        myViewHolder.tvTime.setText("Data e Hora: " + format.format(ts));
    }

    //tamanho da lista
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCrime;
        public TextView tvDescription;
        public TextView tvLevel;
        public TextView tvTime;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvCrime = (TextView) itemView.findViewById(R.id.tv_crime);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvLevel = (TextView) itemView.findViewById(R.id.tv_level);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}