package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.Model.NewDog;
import com.example.woof.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserDogsAdapter extends RecyclerView.Adapter<UserDogsAdapter.ItemViewHolder> {

    private Context context;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private List<Dog> ownerDogs;
    private OnDogSelectedListener listener;

    public interface OnDogSelectedListener{
        void OnDogSelected(Dog selectedDog);
    }

    public UserDogsAdapter(Context context, List<Dog> ownerDogs, OnDogSelectedListener listener) {
        this.context = context;
        this.ownerDogs = ownerDogs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_user_dogs,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Dog ownerDog = ownerDogs.get(position);
        holder.ICL_MTV_name.setText(ownerDog.getName());;
        holder.ICL_MTV_age.setText(ownerDog.getDob()); // TODO: calculate age
        holder.ICL_MTV_gender.setText(ownerDog.getGender());
        if(ownerDog.getPhotoURL() == null){
            holder.ICL_IV_image.setImageResource(R.drawable.default_dog_picture);
        }
        else {
            Glide.with(context).load(ownerDog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.ICL_IV_image);
        }

        holder.itemView.setBackgroundColor(selectedPosition == position ?
                holder.itemView.getContext().getColor(R.color.transparent_green) :
                holder.itemView.getContext().getColor(android.R.color.transparent));

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.OnDogSelected(ownerDog);
        });

    }

    @Override
    public int getItemCount() {
        return ownerDogs.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView ICL_MTV_name;
        ImageView ICL_IV_image;
        MaterialTextView ICL_MTV_age;
        MaterialTextView ICL_MTV_gender;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ICL_MTV_name = itemView.findViewById(R.id.ICL_MTV_name);
            ICL_IV_image = itemView.findViewById(R.id.ICL_IV_image);
            ICL_MTV_age = itemView.findViewById(R.id.ICL_MTV_age);
            ICL_MTV_gender = itemView.findViewById(R.id.ICL_MTV_gender);

        }
    }
}