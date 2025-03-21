package com.example.woof.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.woof.Model.NewOwner;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.example.woof.Singleton.CloudinaryManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE =  1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private EditText SignUp_ET_inputEmail;
    private ShapeableImageView SignUp_IV_OwnerProfilePhoto;
    private MaterialButton SignUp_MB_buttonSignUp;
    OwnerApi apiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private Uri imageUri;
    private String imageURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        initPictureTaker();
        initViews();
    }

    private void initViews() {
        SignUp_MB_buttonSignUp.setOnClickListener(View -> signUpClicked());
        SignUp_IV_OwnerProfilePhoto.setOnClickListener(v->openImagePicker());
    }

    private void findViews() {
        SignUp_ET_inputEmail = findViewById(R.id.SignUp_ET_inputEmail);
        SignUp_MB_buttonSignUp = findViewById(R.id.SignUp_MB_buttonSignUp);
        SignUp_IV_OwnerProfilePhoto = findViewById(R.id.SignUp_IV_OwnerProfilePhoto);
    }

    private void uploadImageToCloudinary(Uri imageUri){
        CloudinaryManager.getInstance(this).uploadImage(imageUri, new UploadCallback() {
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
                imageURL = (String) resultData.get("secure_url"); // image url have valid value

                if(imageURL != null){
            //        SignUp_IV_OwnerProfilePhoto.setImageTintList(null);
                    runOnUiThread(() -> Glide.with(SignUpActivity.this).load(imageURL).error(R.drawable.default_owner_picture).into(SignUp_IV_OwnerProfilePhoto));
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Upload failed", Toast.LENGTH_LONG).show();
                    SignUp_IV_OwnerProfilePhoto.setImageResource(R.drawable.default_owner_picture);
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(SignUpActivity.this,"Upload failed", Toast.LENGTH_LONG).show();
                Log.e("cloudinary","Upload failed: " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        });
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);
    }

    private void initPictureTaker(){
        // initialize the pick image launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        uploadImageToCloudinary(imageUri);
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        Bundle extras = result.getData().getExtras();
                        if(extras!=null){
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageUri = getImageUriFromBitmap(imageBitmap);
                            uploadImageToCloudinary(imageUri);
                            SignUp_IV_OwnerProfilePhoto.setImageBitmap(imageBitmap);
                        }
                    }
                });
    }

    void signUpClicked(){
        String mail = SignUp_ET_inputEmail.getText().toString();
        NewOwner newOwner = new NewOwner();
        newOwner.setMail(mail);
        newOwner.setPhotoURL(imageURL);

        Call<Owner> call = apiService.createOwner(newOwner);
        call.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner createdNewOwner = response.body();
                if (response.isSuccessful() && createdNewOwner != null) {
                    Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_LONG).show();
                    CurrentUserManager.getInstance().setOwner(createdNewOwner);
                    Intent intent = new Intent(SignUpActivity.this, ChooseDogBreedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    handleError(response);
                }
            }
            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {
                Toast.makeText(SignUpActivity.this,"No internet connection", Toast.LENGTH_LONG).show();
                Log.e("Request failed: ", throwable.getMessage());
            }
        });
    }

    private void openImagePicker(){
        new AlertDialog.Builder(SignUpActivity.this)
                .setTitle("Choose an option")
                .setItems(new String[]{"Choose from gallery","Open camera"},(dialog, which) -> {
                    if(which == 0) openGallery();
                    else if(which == 1) requestCameraPermission();

                }).show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePhotoLauncher.launch(takePictureIntent);
        }else {
            Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleError(Response<Owner> response){
        if(response.code() == 400){
            Toast.makeText(SignUpActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
        }
        else if(response.code() == 409){
            Toast.makeText(SignUpActivity.this, "User already exist", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_LONG).show();
        }
        Log.e("Request failed: ", response.message());
    }
}