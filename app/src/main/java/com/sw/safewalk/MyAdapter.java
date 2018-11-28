package com.sw.safewalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

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
        Long ts = mList.get(position).getHorario();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts);

        myViewHolder.tvCrime.setText("Tipo do Crime: " + mList.get(position).getCrimeSelecionado());
        myViewHolder.tvDescription.setText("Descrição: " + mList.get(position).getDescricao());;
        myViewHolder.tvLevel.setText("Nível: " + mList.get(position).getNivel().toString());
        myViewHolder.tvTime.setText("Data e Hora: " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + ", " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
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