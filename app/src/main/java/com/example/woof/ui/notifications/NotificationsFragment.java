package com.example.woof.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Adapters.PostsAdapter;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.View.PostBottomSheet;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.PostApi;
import com.example.woof.databinding.FragmentNotificationsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ImageView FN_IV_DogPicture;
    private TextView textOwner;
    private ImageView FN_IV_OwnerPicture;
    private MaterialTextView textName;
    private TextView textFollowers;
    private TextView textFollowing;
    private TextView textPosts;
    private RecyclerView FN_RV_Posts;
    private List<Post> posts;
    private PostsAdapter postsAdapter;

    PostApi postApiService = ApiController.getRetrofitInstance().create(PostApi.class);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews();
        initViews();
        return root;
    }

    private void findViews(){
        FN_IV_DogPicture = binding.getRoot().findViewById(R.id.FN_IV_DogPicture);
        textOwner = binding.getRoot().findViewById(R.id.textOwner);
        textName = binding.getRoot().findViewById(R.id.textName);
        FN_IV_OwnerPicture = binding.getRoot().findViewById(R.id.FN_IV_OwnerPicture);
        textFollowers = binding.getRoot().findViewById(R.id.textFollowers);
        textFollowing = binding.getRoot().findViewById(R.id.textFollowing);
        FN_RV_Posts = binding.getRoot().findViewById(R.id.FN_RV_Posts);
        textPosts = binding.getRoot().findViewById(R.id.textPosts);
    }

    private void initViews(){
        posts = new ArrayList<>();
        Log.e("Current Dog manager", CurrentDogManager.getInstance().getDog().toString());
        Log.e("Current Owner manager", CurrentUserManager.getInstance().getOwner().toString());
        if(CurrentDogManager.getInstance().getDog().getPhotoURL() == null)
            FN_IV_DogPicture.setImageResource(R.drawable.default_dog_picture);
        else
            Glide.with(this).load(CurrentDogManager.getInstance().getDog().getPhotoURL()).error(R.drawable.default_dog_picture).into(FN_IV_DogPicture);

        if(CurrentUserManager.getInstance().getOwner().getPhotoURL() == null)
            FN_IV_OwnerPicture.setImageResource(R.drawable.default_owner_picture);
        else
            Glide.with(this).load(CurrentUserManager.getInstance().getOwner().getPhotoURL()).error(R.drawable.default_owner_picture).into(FN_IV_OwnerPicture);

        textOwner.setText(CurrentUserManager.getInstance().getOwner().getMail());
        textName.setText(CurrentDogManager.getInstance().getDog().getName());

        Log.e("CurrentDog: ", CurrentDogManager.getInstance().getDog().toString());
        Log.e("CurrentUser: ", CurrentUserManager.getInstance().getOwner().toString());
        textFollowers.setText(String.valueOf(CurrentDogManager.getInstance().getDog().getFollowers().size()));

        textFollowing.setText(String.valueOf(CurrentDogManager.getInstance().getDog().getFollowing().size()));

        FN_RV_Posts.setLayoutManager(new GridLayoutManager(getContext(),3));

        postsAdapter = new PostsAdapter(getContext(),posts, selectedPost -> {
            // TODO: open bottom sheet fragment with the post
            PostBottomSheet postBottomSheet = PostBottomSheet.newInstance(selectedPost);
            postBottomSheet.show(getParentFragmentManager(),"postBottomSheet");
        });
        FN_RV_Posts.setAdapter(postsAdapter);

        fetchPosts();
    }

    private void fetchPosts(){
        Call<List<Post>> call = postApiService.getPostsByDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.body()!=null) {
                    posts.clear();
                    posts.addAll(response.body());
                    postsAdapter.notifyDataSetChanged();
                    textPosts.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}