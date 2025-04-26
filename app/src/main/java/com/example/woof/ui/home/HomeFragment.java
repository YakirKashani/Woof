package com.example.woof.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.woof.Adapters.FeedAdapter;
import com.example.woof.Model.NewPost;
import com.example.woof.Model.Notification;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.example.woof.Singleton.CloudinaryManager;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.View.MainActivity;
import com.example.woof.View.NotificationsBottomSheet;
import com.example.woof.View.PostBottomSheet;
import com.example.woof.View.SignUpActivity;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.example.woof.WoofBackend.PostApi;
import com.example.woof.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private EditText FH_ET_PostDescription;
    private ImageView FH_IV_UploadedImage;
    private ShapeableImageView FH_SIV_addEvent;
    private ShapeableImageView FH_SIV_AddPhoto;
    private MaterialButton FH_MB_PostButton;
    private RecyclerView FH_RV_Posts;
    private ShapeableImageView FH_SIV_Notifications;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);
    PostApi postApiService = ApiController.getRetrofitInstance().create(PostApi.class);
    private FeedAdapter feedAdapter;
    private List<Post> posts;
    private Uri newPostImageURI;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews();
        initViews();
        return root;
    }

    private void findViews(){
        FH_ET_PostDescription = binding.getRoot().findViewById(R.id.FH_ET_PostDescription);
        FH_IV_UploadedImage = binding.getRoot().findViewById(R.id.FH_IV_UploadedImage);
        FH_SIV_addEvent = binding.getRoot().findViewById(R.id.FH_SIV_addEvent);
        FH_SIV_AddPhoto = binding.getRoot().findViewById(R.id.FH_SIV_AddPhoto);
        FH_MB_PostButton = binding.getRoot().findViewById(R.id.FH_MB_PostButton);
        FH_RV_Posts = binding.getRoot().findViewById(R.id.FH_RV_Posts);
        FH_SIV_Notifications = binding.getRoot().findViewById(R.id.FH_SIV_Notifications);
    }

    private void initViews(){
        posts = new ArrayList<>();
        FH_IV_UploadedImage.setVisibility(View.GONE);
        FH_MB_PostButton.setEnabled(false);
        //TODO: 1. allow post button only if theres text in the edit text
        FH_ET_PostDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FH_MB_PostButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //TODO: 2. allow the user pick image when (gallery / camera) and put it in the Image view
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Intent data = result.getData();
                        if(data.getData() != null){
                            newPostImageURI = data.getData();
                            FH_IV_UploadedImage.setImageURI(newPostImageURI);
                            FH_IV_UploadedImage.setVisibility(View.VISIBLE);
                        } else if (data.getExtras() != null){
                            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                            FH_IV_UploadedImage.setImageBitmap(capturedImage);
                            newPostImageURI = getImageUriFromBitmap(capturedImage);
                            FH_IV_UploadedImage.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        FH_SIV_AddPhoto.setOnClickListener(v -> openImagePicker());

        //TODO: 3. save the post in DB when pressing POST
        FH_MB_PostButton.setOnClickListener(v -> {
            NewPost post = new NewPost();
            String postDescription = FH_ET_PostDescription.getText().toString().trim();
            if(postDescription.isEmpty()){
                Toast.makeText(getContext(),"Please enter description", Toast.LENGTH_LONG).show();
                return;
            }
            post.setDescription(postDescription);
            post.setOwnerEmail(CurrentDogManager.getInstance().getDog().getOwnerEmail());
            post.setDogName(CurrentDogManager.getInstance().getDog().getName());

            // TODO: 4. upload the picture to cloudinary
            if(newPostImageURI != null){
                getActivity().runOnUiThread(() -> uploadImageToCloudinaryAndSaveInDB(post,newPostImageURI));
            }
            else {
                post.setPictureUrl("none");
                SavePostInDB(post);
            }
        });

        // TODO: 4. fetch posts
        FH_RV_Posts.setLayoutManager(new LinearLayoutManager(getContext()));
        feedAdapter = new FeedAdapter(getContext(),posts, selectedPost -> {
            // TODO: open bottom sheet fragment with the post
            PostBottomSheet postBottomSheet = PostBottomSheet.newInstance(selectedPost,false);
            postBottomSheet.show(getParentFragmentManager(),"postBottomSheet");
        });
        FH_RV_Posts.setAdapter(feedAdapter);
        fetchPosts();

        Call<List<Notification>> notificationsCall = dogApiService.getNewNotificationsByDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        notificationsCall.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if(response.body() != null && response.isSuccessful()) {
                    if(response.body().isEmpty())
                        FH_SIV_Notifications.setImageResource(R.drawable.bell);
                    else
                        FH_SIV_Notifications.setImageResource(R.drawable.bell_notification);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable throwable) {

            }
        });


        FH_SIV_Notifications.setOnClickListener(v -> {
            //TODO: Create notifications bottom sheet
            NotificationsBottomSheet notificationsBottomSheet = new NotificationsBottomSheet();
            notificationsBottomSheet.show(getParentFragmentManager(),"notificationsBottomSheet");
        });
    }

    private void fetchPosts(){
        Call<List<Post>> postsCall = postApiService.getPostsFromFollowedDogs(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName());
        postsCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.body() != null){
                    Log.e("postsCall", response.body().toString());
                    posts.clear();
                    posts.addAll(response.body());
                    feedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {
                Log.e("postsCall", throwable.getMessage().toString());

            }
        });
    }

    private void SavePostInDB(NewPost post){
        Log.e("post save",post.toString());
        Call<Post> call = postApiService.createPost(post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Toast.makeText(getContext(),"Post saved successfully", Toast.LENGTH_LONG).show();
                Log.e("post save",response.body().toString());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable throwable) {
                Toast.makeText(getContext(),"Post upload failed", Toast.LENGTH_LONG).show();
                Log.e("post save",throwable.getMessage().toString());
            }
        });
    }

    private void uploadImageToCloudinaryAndSaveInDB(NewPost post, Uri imageUri){
        CloudinaryManager.getInstance(getContext()).uploadImage(imageUri, new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.e("cloudinary","Uploading image");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                Log.e("cloudinary","Upload successful");
                String imageURL = (String) resultData.get("secure_url"); // image url have valid value

                if(imageURL != null){
                 //   runOnUiThread(() -> Glide.with(SignUpActivity.this).load(imageURL).error(R.drawable.default_owner_picture).into(SignUp_IV_OwnerProfilePhoto));
                    post.setPictureUrl(imageURL);
                    SavePostInDB(post);
                    FH_ET_PostDescription.setText("");
                    FH_IV_UploadedImage.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(getContext(),"Upload failed", Toast.LENGTH_LONG).show();
                  //  SignUp_IV_OwnerProfilePhoto.setImageResource(R.drawable.default_owner_picture);
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(getContext(),"Upload failed", Toast.LENGTH_LONG).show();
                Log.e("cloudinary","Upload failed: " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        });
    }

    private void openImagePicker(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = Intent.createChooser(galleryIntent, "Select Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        imagePickerLauncher.launch(chooser);

    }

    private Uri getImageUriFromBitmap(Bitmap bitmap){
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}