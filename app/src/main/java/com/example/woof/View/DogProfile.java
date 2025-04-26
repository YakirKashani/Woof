package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Adapters.PostsAdapter;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Notification;
import com.example.woof.Model.Owner;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.example.woof.WoofBackend.PostApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogProfile extends AppCompatActivity {
    private ImageView ADP_IV_OwnerPicture;
    private MaterialTextView ADP_MTV_textName;
    private TextView ADP_TV_textOwner;
    private TextView ADP_TV_textPosts;
    private TextView ADP_TV_textFollowers;
    private TextView ADP_TV_textFollowing;
    private ImageView ADP_IV_DogPicture;
    private RecyclerView ADP_RV_Posts;
    private MaterialButton ADP_MB_FollowMB;
    private String ownerEmail;
    private String dogName;
    private List<Post> posts;
    private PostsAdapter postsAdapter;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);
    PostApi postApiService = ApiController.getRetrofitInstance().create(PostApi.class);
    private PostBottomSheet postBottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);
        findViews();
        readDataFromPreviousIntent();
        initViews();
    }

    private void findViews() {
        ADP_IV_OwnerPicture = findViewById(R.id.ADP_IV_OwnerPicture);
        ADP_MTV_textName = findViewById(R.id.ADP_MTV_textName);
        ADP_TV_textOwner = findViewById(R.id.ADP_TV_textOwner);
        ADP_TV_textPosts = findViewById(R.id.ADP_TV_textPosts);
        ADP_TV_textFollowers = findViewById(R.id.ADP_TV_textFollowers);
        ADP_TV_textFollowing = findViewById(R.id.ADP_TV_textFollowing);
        ADP_IV_DogPicture = findViewById(R.id.ADP_IV_DogPicture);
        ADP_RV_Posts = findViewById(R.id.ADP_RV_Posts);
        ADP_MB_FollowMB = findViewById(R.id.ADP_MB_FollowMB);
    }

    private void readDataFromPreviousIntent(){
        Intent intent = getIntent();
        ownerEmail = intent.getStringExtra("DogOwner");
        dogName = intent.getStringExtra("DogName");
    }

    private void initViews(){
        posts = new ArrayList<>();
        ADP_RV_Posts.setLayoutManager(new GridLayoutManager(this,3));
        postsAdapter = new PostsAdapter(this,posts,selectedPost -> {
            // TODO: PostBottomSheet
            postBottomSheet = PostBottomSheet.newInstance(selectedPost,true);
            postBottomSheet.show(getSupportFragmentManager(),"postBottomSheet");
        });
        ADP_RV_Posts.setAdapter(postsAdapter);

        ADP_MTV_textName.setText(dogName);
        ADP_TV_textOwner.setText(ownerEmail);

        Call<Dog> DogCall = dogApiService.findDog(ownerEmail,dogName);
        DogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                Dog dog = response.body();
                if(dog.getPhotoURL() == null)
                    ADP_IV_DogPicture.setImageResource(R.drawable.default_dog_picture);
                else {
                    Glide.with(DogProfile.this).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(ADP_IV_DogPicture);
     //               postBottomSheet.setDogImage(dog.getPhotoURL());
                }
                ADP_TV_textFollowers.setText(String.valueOf(dog.getFollowers().size()));
                ADP_TV_textFollowing.setText(String.valueOf(dog.getFollowing().size()));
                if(isCurrentDogInList(dog.getFollowers())) {
                    ADP_MB_FollowMB.setText("Following");
                    ADP_MB_FollowMB.setIconResource(R.drawable.check);
                }
                Call<List<Post>> PostsCall = postApiService.getPostsByDog(dog.getOwnerEmail(),dog.getName());
                PostsCall.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if(response.body()!=null) {
                            posts.clear();
                            posts.addAll(response.body());
                            postsAdapter.notifyDataSetChanged();
                            ADP_TV_textPosts.setText(String.valueOf(response.body().size()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable throwable) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });

        Call<Owner> OwnerCall = ownerApiService.findOwner(ownerEmail);
        OwnerCall.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner owner = response.body();
                if(owner.getPhotoURL() == null)
                    ADP_IV_OwnerPicture.setImageResource(R.drawable.default_owner_picture);
                else {
                    Glide.with(DogProfile.this).load(owner.getPhotoURL()).error(R.drawable.default_owner_picture).into(ADP_IV_OwnerPicture);
     //               postBottomSheet.setOwnerImage(owner.getPhotoURL());
                }
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {

            }
        });

        ADP_MB_FollowMB.setOnClickListener(v -> {
            if(ADP_MB_FollowMB.getText().toString().equals("Follow")){
                Call<Boolean> followDogCall = dogApiService.followDog(ownerEmail,dogName,CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
                followDogCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body()){
                            ADP_MB_FollowMB.setText("Following");
                            ADP_MB_FollowMB.setIconResource(R.drawable.check);
                            refreshCounters();
                            Log.e("notification",CurrentDogManager.getInstance().getDog().getOwnerEmail() + "#" + CurrentDogManager.getInstance().getDog().getName());
                            Notification notification = new Notification(CurrentDogManager.getInstance().getDog().getOwnerEmail(),
                                    CurrentDogManager.getInstance().getDog().getName(),
                                    "started following you",
                                    "New follower",
                                    true);
                            Log.e("notification",notification.toString());
                            Call<Void> addNotificationCall = dogApiService.addNewNotification(ownerEmail, dogName,notification);
                            addNotificationCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable throwable) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable throwable) {

                    }
                });
            }
            else{
                Call<Boolean> unfollowDogCall = dogApiService.unfollowDog(ownerEmail,dogName,CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
                unfollowDogCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body()){
                            ADP_MB_FollowMB.setText("Follow");
                            ADP_MB_FollowMB.setIconResource(R.drawable.follow_icon);
                            refreshCounters();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable throwable) {

                    }
                });
            }
        });
    }

    private boolean isCurrentDogInList(List<String> followers){
        String currentDogId = CurrentDogManager.getInstance().getDog().getOwnerEmail() + "#" + CurrentDogManager.getInstance().getDog().getName();
        return followers.contains(currentDogId);
    }

    private void refreshCounters(){
        Call<Dog> refreshDogCall = dogApiService.findDog(ownerEmail,dogName);
        refreshDogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                Dog dog = response.body();
                ADP_TV_textFollowers.setText(String.valueOf(dog.getFollowers().size()));
                ADP_TV_textFollowing.setText(String.valueOf(dog.getFollowing().size()));
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });

        Call<Dog> refreshCurrentDog = dogApiService.findDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        refreshCurrentDog.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                CurrentDogManager.getInstance().setDog(response.body());
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });

    }

}