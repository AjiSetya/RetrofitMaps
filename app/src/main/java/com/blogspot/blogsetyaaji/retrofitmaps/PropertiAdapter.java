package com.blogspot.blogsetyaaji.retrofitmaps;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.Propertus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AJISETYA on 8/20/2017.
 */

class PropertiAdapter extends RecyclerView.Adapter<PropertiAdapter.ViewHolder> {
    List<Propertus> data_properti;
    Context context;
    LayoutInflater layoutInflater;

    public PropertiAdapter(MainActivity mainActivity, List<Propertus> list_properti) {
        data_properti = list_properti;
        context = mainActivity;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.list_properti, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtnama.setText(data_properti.get(position).getNama());
        holder.txtjenis.setText(data_properti.get(position).getJenis());
    }

    @Override
    public int getItemCount() {
        return data_properti.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtnama)
        TextView txtnama;
        @BindView(R.id.txtjenis)
        TextView txtjenis;
        @BindView(R.id.card_properti)
        CardView cardProperti;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
