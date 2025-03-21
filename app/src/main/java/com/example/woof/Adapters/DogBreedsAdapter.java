package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogbreedslib.BreedAndUrlModel;
import com.example.woof.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class DogBreedsAdapter extends RecyclerView.Adapter<DogBreedsAdapter.ViewHolder> {
    private final List<BreedAndUrlModel> breedAndUrlModels;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnBreedSelectedListener listener;

    public interface OnBreedSelectedListener{
        void OnBreedSelected(BreedAndUrlModel selectedBreed);
    }

    public DogBreedsAdapter(List<BreedAndUrlModel> breedAndUrlModels,OnBreedSelectedListener listener) {
        this.breedAndUrlModels = breedAndUrlModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_breed,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BreedAndUrlModel breedAndUrlModel = breedAndUrlModels.get(position);
        holder.ICG_MTV_name.setText(breedAndUrlModel.getBreed_name());
        Glide.with(holder.itemView.getContext()).load(breedAndUrlModel.getPic_url()).into(holder.ICG_IV_image);

        holder.itemView.setBackgroundColor(selectedPosition == position ?
                holder.itemView.getContext().getColor(R.color.transparent_green) :
                holder.itemView.getContext().getColor(android.R.color.transparent));

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.OnBreedSelected(breedAndUrlModel);
        });
    }

    @Override
    public int getItemCount() {
        return breedAndUrlModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ICG_IV_image;
        MaterialTextView ICG_MTV_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ICG_IV_image = itemView.findViewById(R.id.ICG_IV_image);
            ICG_MTV_name = itemView.findViewById(R.id.ICG_MTV_name);
        }
    }
}
