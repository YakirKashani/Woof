package com.example.woof.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    public NotificationsBottomSheet(List<Notification> notifications){
        this.notifications = notifications;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_bottom_sheet, container, false);
        NBS_RV_NotificationRecyclerView = view.findViewById(R.id.NBS_RV_NotificationRecyclerView);
        NBS_RV_NotificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationsAdapter(notifications);
        NBS_RV_NotificationRecyclerView.setAdapter(notificationAdapter);
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
