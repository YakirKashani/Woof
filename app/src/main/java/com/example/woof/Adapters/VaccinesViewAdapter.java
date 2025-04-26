package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Model.Vaccine;
import com.example.woof.R;

import java.util.List;

public class VaccinesViewAdapter extends RecyclerView.Adapter<VaccinesViewAdapter.VaccinesViewHolder> {
    private Context context;
    private List<Vaccine> vaccines;


    public VaccinesViewAdapter(Context context, List<Vaccine> vaccines) {
        this.context = context;
        this.vaccines = vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines){
        this.vaccines = vaccines;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VaccinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaccine, parent, false);
        return new VaccinesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccinesViewHolder holder, int position) {
        Vaccine vaccine = vaccines.get(position);
        holder.IV_TV_VaccineName.setText(vaccine.getName());
    }

    @Override
    public int getItemCount() {
        return vaccines.size();
    }

    public static class VaccinesViewHolder extends RecyclerView.ViewHolder {
        TextView IV_TV_VaccineName;
        public VaccinesViewHolder(@NonNull View itemView) {
            super(itemView);
            IV_TV_VaccineName = itemView.findViewById(R.id.IV_TV_VaccineName);
        }
    }
}
