package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Notification;
import com.example.woof.R;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{
    private List<Notification> notifications;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

    public NotificationsAdapter(List<Notification> notifications){
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        if(notification.isNew()){
            holder.IN_RL_NotificationCard.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.NewNotificationBackgroundColor));
        } else{
            holder.IN_RL_NotificationCard.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        if (notification.getDogCreatedNotification() != null)
            holder.IN_MTV_Description.setText(notification.getDogCreatedNotification() + " " + notification.getMessage());
        else
            holder.IN_MTV_Description.setText(notification.getMessage());

        if (notification.getImgUrl() != null) {
            Call<Dog> dogCall = dogApiService.findDog(notification.getOwnerCreatedNotification(), notification.getDogCreatedNotification());
            dogCall.enqueue(new Callback<Dog>() {
                @Override
                public void onResponse(Call<Dog> call, Response<Dog> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        if (response.body().getPhotoURL() == null) {
                            holder.IN_SIV_CreatedByPicture.setImageResource(R.drawable.default_dog_picture);
                        } else {
                            Glide.with(holder.itemView.getContext()).load(response.body().getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.IN_SIV_CreatedByPicture);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Dog> call, Throwable throwable) {

                }
            });
        } else {
            holder.IN_SIV_NotificationPicture.setVisibility(View.INVISIBLE);
        }

        if(notification.getImgUrl() != null){
            if(notification.getImgUrl().equals("New follower"))
                holder.IN_SIV_NotificationPicture.setImageResource(R.drawable.paw);
            else
                Glide.with(holder.itemView.getContext()).load(notification.getImgUrl()).into(holder.IN_SIV_NotificationPicture);
        } else{
            holder.IN_SIV_NotificationPicture.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationsViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout IN_RL_NotificationCard;
        private ShapeableImageView IN_SIV_CreatedByPicture;
        private MaterialTextView IN_MTV_Description;
        private ShapeableImageView IN_SIV_NotificationPicture;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            IN_RL_NotificationCard = itemView.findViewById(R.id.IN_RL_NotificationCard);
            IN_SIV_CreatedByPicture = itemView.findViewById(R.id.IN_SIV_CreatedByPicture);
            IN_MTV_Description = itemView.findViewById(R.id.IN_MTV_Description);
            IN_SIV_NotificationPicture = itemView.findViewById(R.id.IN_SIV_NotificationPicture);
        }
    }
}
