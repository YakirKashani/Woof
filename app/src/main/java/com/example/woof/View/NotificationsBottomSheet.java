package com.example.woof.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Adapters.NotificationsAdapter;
import com.example.woof.Model.Notification;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsBottomSheet extends BottomSheetDialogFragment {
    private RecyclerView NBS_RV_NotificationRecyclerView;
    private NotificationsAdapter notificationAdapter;
    private List<Notification> notifications;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

    public NotificationsBottomSheet(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_bottom_sheet, container, false);
        NBS_RV_NotificationRecyclerView = view.findViewById(R.id.NBS_RV_NotificationRecyclerView);
        notifications = new ArrayList<>();
        notificationAdapter = new NotificationsAdapter(notifications);
        NBS_RV_NotificationRecyclerView.setAdapter(notificationAdapter);
        Call<List<Notification>> notificationCall = dogApiService.getAllNotificationsByDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        notificationCall.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if(response.isSuccessful() && !response.body().isEmpty()) {
                    notifications.addAll(response.body());
                    notificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable throwable) {

            }
        });
        markAllNotificationAsRead();
        return view;
    }

    private void markAllNotificationAsRead(){
        Call<Void> ReadAllCall = dogApiService.markNotificationsAsRead(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        ReadAllCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

            }
        });
    }
}
