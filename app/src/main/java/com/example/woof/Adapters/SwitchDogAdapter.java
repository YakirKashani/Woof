package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SwitchDogAdapter extends RecyclerView.Adapter<SwitchDogAdapter.SwitchDogViewHolder>{

    public interface OnDogClickListener {
        void onDogClick(Dog dog);
    }

    private List<Dog> dogs;
    private OnDogClickListener onDogClickListener;

    public SwitchDogAdapter(List<Dog> dogs, OnDogClickListener onDogClickListener) {
        this.dogs = dogs;
        this.onDogClickListener = onDogClickListener;
    }

    @NonNull
    @Override
    public SwitchDogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_in_switch_profile, parent, false);
        return new SwitchDogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwitchDogViewHolder holder, int position) {
        Dog dog = dogs.get(position);
        holder.IDISP_MTV_DogName.setText(dog.getName());
        if(dog.getPhotoURL() != null)
            Glide.with(holder.itemView.getContext()).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.IDISP_SIV_DogPicture);
        else
            holder.IDISP_SIV_DogPicture.setImageResource(R.drawable.default_dog_picture);

        holder.itemView.setOnClickListener(v -> {
           onDogClickListener.onDogClick(dog);
        });
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }

    public static class SwitchDogViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView IDISP_SIV_DogPicture;
        MaterialTextView IDISP_MTV_DogName;

        public SwitchDogViewHolder(@NonNull View itemView) {
            super(itemView);
            IDISP_SIV_DogPicture = itemView.findViewById(R.id.IDISP_SIV_DogPicture);
            IDISP_MTV_DogName = itemView.findViewById(R.id.IDISP_MTV_DogName);
        }
    }
}
