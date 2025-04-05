package com.example.woof.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VaccinesViewAdapter extends RecyclerView.Adapter<VaccinesViewAdapter.VaccinesViewHolder> {


    @NonNull
    @Override
    public VaccinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VaccinesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class VaccinesViewHolder extends RecyclerView.ViewHolder {
        public VaccinesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
