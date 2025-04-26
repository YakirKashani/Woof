package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Model.Medicine;
import com.example.woof.R;

import java.util.List;

public class MedicinesViewAdapter extends RecyclerView.Adapter<MedicinesViewAdapter.MedicinesViewHolder>{
    private Context context;
    private List<Medicine> medicines;

    public MedicinesViewAdapter(Context context, List<Medicine> medicines) {
        this.context = context;
        this.medicines = medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicinesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.IM_TV_MedicineName.setText(medicine.getName());
        holder.IM_TV_MedicineAmount.setText(String.valueOf(medicine.getAmount()));
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public static class MedicinesViewHolder extends RecyclerView.ViewHolder{
        TextView IM_TV_MedicineName;
        TextView IM_TV_MedicineAmount;
        public MedicinesViewHolder(@NonNull View itemView) {
            super(itemView);
            IM_TV_MedicineName = itemView.findViewById(R.id.IM_TV_MedicineName);
            IM_TV_MedicineAmount = itemView.findViewById(R.id.IM_TV_MedicineAmount);
        }
    }
}
