package com.example.woof.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MedicinesViewAdapter extends RecyclerView.Adapter<MedicinesViewAdapter.MedicinesViewHolder>{

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MedicinesViewHolder extends RecyclerView.ViewHolder{

        public MedicinesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
