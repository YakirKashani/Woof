package com.example.woof.Utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.woof.Model.Notification;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "dog_notification_channel";


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel();
    }

    @NonNull
    @Override
    public Result doWork() {
        long startTime = System.currentTimeMillis();
        Log.d("Worker", "Worker started at: " + startTime);
        try {
            if (CurrentDogManager.getInstance().getDog() != null &&
                    CurrentDogManager.getInstance().getDog().getName() != null &&
                    CurrentDogManager.getInstance().getDog().getOwnerEmail() != null) {
                String ownerEmail = CurrentDogManager.getInstance().getDog().getOwnerEmail();
                String dogName = CurrentDogManager.getInstance().getDog().getName();

                DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
                Call<List<Notification>> notificationsCall = dogApiService.getNewNotificationsByDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName());

                Response<List<Notification>> response = notificationsCall.execute(); // Synchronous call

                if(response.isSuccessful() && response.body() != null){
                    List<Notification> notifications = response.body();
                    if(!notifications.isEmpty()){
                        if(notifications.size() == 1){
                            sendNotification("New notification", notifications.get(0).getMessage());
                        } else{
                            sendNotification("New notifications", "You have " + notifications.size() + " new notifications");
                        }
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.paw)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            notificationManagerCompat.notify(1, builder.build());
    }

    private void createNotificationChannel(){
        CharSequence name = "Dog Notifications";
        String description = "Notifies about dog alerts";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
        }
    }
}
