package com.example.woof.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DogProfileInOwnerAdapter extends RecyclerView.Adapter<DogProfileInOwnerAdapter.DogProfileInOwnerViewHolder>{
    private Context context;
    private List<Dog> dogs;
    private OnDogProfileInOwnerChooseListener listener;
    private int expandedPosition = -1;
    private int previousExpandedPosition = -1;

    public interface OnDogProfileInOwnerChooseListener{
        void onDogSelected(Dog selectedDog);
    }

    public DogProfileInOwnerAdapter(Context context, List<Dog> dogs, OnDogProfileInOwnerChooseListener listener){
        this.context = context;
        this.dogs = dogs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DogProfileInOwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_profile_card_for_animation,parent,false);
        return new DogProfileInOwnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogProfileInOwnerViewHolder holder, int position) {
        Dog dog = dogs.get(position);

        holder.DPCA_TV_DogName.setText(dog.getName());
        holder.DPCA_TV_Age.setText(dog.getDob());
        holder.DPCA_TV_DogBreed.setText(dog.getBreed());
        if(dog.getPhotoURL()==null)
            holder.DPCA_IV_DogPicture.setImageResource(R.drawable.default_dog_picture);
        else
            Glide.with(holder.itemView.getContext()).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.DPCA_IV_DogPicture);

        holder.DPCA_Group_Info.setVisibility(View.GONE);
        holder.DPCA_V_Background.setAlpha(0f);

        holder.DPCA_IV_DogPicture.setOnClickListener(v -> {
            if(expandedPosition != position) {
                if (previousExpandedPosition == position) {
                    toggleInfoInvisible(holder);
                    return;
                }
                if (previousExpandedPosition != -1) {
                    notifyItemChanged(previousExpandedPosition);
                }
                toggleInfoVisibility(holder);
                expandedPosition = position;
                previousExpandedPosition = position;
            }
        });

        holder.DPCA_MB_GoToProfile.setOnClickListener(v -> {
            if(listener != null)
                listener.onDogSelected(dog);
        });
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }

    public static class DogProfileInOwnerViewHolder extends RecyclerView.ViewHolder{
        private ImageView DPCA_IV_DogPicture;
        private TextView DPCA_TV_DogName;
        private TextView DPCA_TV_DogBreed;
        private TextView DPCA_TV_Age;
        private MaterialButton DPCA_MB_GoToProfile;
        private Group DPCA_Group_Info;
        private View DPCA_V_Background;

        public DogProfileInOwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            DPCA_IV_DogPicture = itemView.findViewById(R.id.DPCA_IV_DogPicture);
            DPCA_TV_DogName = itemView.findViewById(R.id.DPCA_TV_DogName);
            DPCA_TV_DogBreed = itemView.findViewById(R.id.DPCA_TV_DogBreed);
            DPCA_TV_Age = itemView.findViewById(R.id.DPCA_TV_Age);
            DPCA_MB_GoToProfile = itemView.findViewById(R.id.DPCA_MB_GoToProfile);
            DPCA_Group_Info = itemView.findViewById(R.id.DPCA_Group_Info);
            DPCA_V_Background = itemView.findViewById(R.id.DPCA_V_Background);
        }
    }

    private void toggleInfoVisibility(DogProfileInOwnerViewHolder holder){
        if(holder.DPCA_Group_Info.getVisibility() == View.GONE){
            holder.DPCA_Group_Info.setVisibility(View.VISIBLE);
            ObjectAnimator fadeInBackground = ObjectAnimator.ofFloat(holder.DPCA_V_Background,"alpha",0f,1f);
            fadeInBackground.setDuration(300);

            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",0.8f,1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",0.8f,1f);
            ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(holder.DPCA_Group_Info,scaleX,scaleY);
            scaleUp.setDuration(300);

            fadeInBackground.start();
            scaleUp.start();
        }
    }

    private void toggleInfoInvisible(DogProfileInOwnerViewHolder holder){
        ObjectAnimator fadeOutBackground = ObjectAnimator.ofFloat(holder.DPCA_Group_Info,"alpha",1f,0f);
        fadeOutBackground.setDuration(300);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f,0.8f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f,0.8f);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(holder.DPCA_Group_Info,scaleX,scaleY);
        scaleDown.setDuration(300);

        fadeOutBackground.start();
        scaleDown.start();

        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.DPCA_Group_Info.setVisibility(View.GONE);
            }
        });

    }

}
