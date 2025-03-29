package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.Model.NewDog;
import com.example.woof.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SearchDogsAdapter extends RecyclerView.Adapter<SearchDogsAdapter.SearchResultViewHolder> {
    private List<Dog> dogsFound;
    private OnDogProfileChooseListener listener;

    public interface OnDogProfileChooseListener{
        void OnDogProfileSelected(Dog selectedDog);
    }

    public SearchDogsAdapter(List<Dog> dogsFound, OnDogProfileChooseListener listener){
        this.dogsFound = dogsFound;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_profile_card_in_search,parent,false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        Dog dog = dogsFound.get(position);
        holder.PCS_MTV_DogName.setText(dog.getName());
        holder.PCS_MTV_DogOwner.setText(dog.getOwnerEmail());
        if (dog.getPhotoURL() == null)
            holder.PCS_IV_DogPicture.setImageResource(R.drawable.default_dog_picture);
        else
            Glide.with(holder.itemView.getContext()).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.PCS_IV_DogPicture);

        // TODO: add on click listener PCS_RL_Container
        holder.PCS_RL_Container.setOnClickListener(v -> {
            if(listener!=null)
                listener.OnDogProfileSelected(dog);
        });

    }

    @Override
    public int getItemCount() {
        return dogsFound.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView PCS_IV_DogPicture;
        private MaterialTextView PCS_MTV_DogName;
        private MaterialTextView PCS_MTV_DogOwner;
        private RelativeLayout PCS_RL_Container;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            PCS_IV_DogPicture = itemView.findViewById(R.id.PCS_IV_DogPicture);
            PCS_MTV_DogName = itemView.findViewById(R.id.PCS_MTV_DogName);
            PCS_MTV_DogOwner = itemView.findViewById(R.id.PCS_MTV_DogOwner);
            PCS_RL_Container = itemView.findViewById(R.id.PCS_RL_Container);

        }
    }
}
