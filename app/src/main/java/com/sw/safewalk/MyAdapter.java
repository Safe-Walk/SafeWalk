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
        Log.i("LOG", "onBindViewHolder");
//        myViewHolder.ivCar.setImageResource(mList.get(position).getPhoto());
        myViewHolder.tvModel.setText("Tipo do Crime: " + mList.get(position).getCrimeSelecionado());
        myViewHolder.tvBrand.setText("Descrição: " + mList.get(position).getDescricao());;
        myViewHolder.tvLevel.setText("Nível: " + mList.get(position).getNivel().toString());
    }

    //tamanho da lista
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvModel;
        public TextView tvBrand;
        public TextView tvLevel;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvModel = (TextView) itemView.findViewById(R.id.tv_model);
            tvBrand = (TextView) itemView.findViewById(R.id.tv_brand);
            tvLevel = (TextView) itemView.findViewById(R.id.tv_level);
        }
    }
}