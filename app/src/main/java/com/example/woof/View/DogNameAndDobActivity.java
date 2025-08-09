package com.example.woof.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.woof.Model.Dog;
import com.example.woof.Model.NewDog;
import com.example.woof.R;
import com.example.woof.Singleton.CloudinaryManager;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.Singleton.SharedPreferencesHelper;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogNameAndDobActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;

    private ImageView NameAndDOB_IV_DogImage;
    private EditText NameAndDOB_ET_inputDogName;
    private EditText NameAndDOB_ET_inputDOB;

    private EditText NameAndDOB_ET_inputWeight;
    private Chip NameAndDOB_Chip_Male;
    private Chip NameAndDOB_Chip_Female;
    private int year,month,day;
    private String selectedGender;
    private MaterialButton NameAndDOB_MB_Next;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private Uri imageUri;
    private String imageURL;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_name_and_dob);
        findViews();
        initPictureTaker();
        initViews();
        setupTextWatchers();
        validateFields();
    }

    private void findViews(){
        NameAndDOB_IV_DogImage = findViewById(R.id.NameAndDOB_IV_DogImage);
        NameAndDOB_ET_inputDogName = findViewById(R.id.NameAndDOB_ET_inputDogName);
        NameAndDOB_ET_inputDOB = findViewById(R.id.NameAndDOB_ET_inputDOB);
        NameAndDOB_MB_Next = findViewById(R.id.NameAndDOB_MB_Next);
        NameAndDOB_Chip_Male = findViewById(R.id.NameAndDOB_Chip_Male);
        NameAndDOB_Chip_Female = findViewById(R.id.NameAndDOB_Chip_Female);
        NameAndDOB_ET_inputWeight = findViewById(R.id.NameAndDOB_ET_inputWeight);
    }

    private void initViews() {
        NameAndDOB_ET_inputDOB.setOnClickListener(v -> showDatePicker());

        NameAndDOB_Chip_Male.setOnClickListener(v -> {
            NameAndDOB_Chip_Male.setChecked(true);
            NameAndDOB_Chip_Female.setChecked(false);
            selectedGender = "Male";
        });
        NameAndDOB_Chip_Female.setOnClickListener(v -> {
            NameAndDOB_Chip_Male.setChecked(false);
            NameAndDOB_Chip_Female.setChecked(true);
            selectedGender = "Female";
        });

        NameAndDOB_MB_Next.setOnClickListener(v -> {
            if(selectedGender == null){
                Toast.makeText(this,"Please select a gender", Toast.LENGTH_LONG).show();
                return;
            }

            String ownerEmail = CurrentUserManager.getInstance().getOwner().getMail();
            String dogName = NameAndDOB_ET_inputDogName.getText().toString();
            Call<Dog> call = dogApiService.findDog(ownerEmail, dogName);
            call.enqueue(new Callback<Dog>() {
                @Override
                public void onResponse(Call<Dog> call, Response<Dog> response) {
                    // if response empty - Next intent
                    if (!response.isSuccessful() && response.body() == null) {
                        SaveDogInDBAndNextIntent();
                    } else {
                        Toast.makeText(DogNameAndDobActivity.this, "Dog already exist for this user", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Dog> call, Throwable throwable) {
                    Log.e("findDog", throwable.getMessage().toString());

                }
            });
        });
        NameAndDOB_IV_DogImage.setOnClickListener(v -> openImagePicker());
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
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK && result.getData() != null){
                            imageUri = result.getData().getData();
                            NameAndDOB_IV_DogImage.setImageURI(imageUri);
                            uploadImageToCloudinary(imageUri);
                        }
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
                            NameAndDOB_IV_DogImage.setImageBitmap(imageBitmap);
                        }
                    }
                });
    }

    private void showDatePicker(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth+1) + "/" + selectedYear;
                    NameAndDOB_ET_inputDOB.setText(selectedDate);
        },year,month,day);
        datePickerDialog.show();
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        NameAndDOB_ET_inputDogName.addTextChangedListener(textWatcher);
        NameAndDOB_ET_inputDOB.addTextChangedListener(textWatcher);
    }

    private void validateFields(){
        boolean isNameFilled = !NameAndDOB_ET_inputDogName.getText().toString().trim().isEmpty();
        boolean isDobFilled = !NameAndDOB_ET_inputDOB.getText().toString().trim().isEmpty();
        NameAndDOB_MB_Next.setEnabled(isNameFilled && isDobFilled);
    }

    private void openImagePicker(){
        new AlertDialog.Builder(DogNameAndDobActivity.this)
                .setTitle("Choose an option")
                .setItems(new String[]{"Choose from gallery","Open camera"},(dialog, which) -> {
                    if(which == 0) openGallery();
                    else if(which == 1) requestCameraPermission();

                }).show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
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
                imageURL = (String) resultData.get("secure_url");
                if(imageURL!=null){
                    NameAndDOB_IV_DogImage.setImageTintList(null);
                    runOnUiThread(()-> Glide.with(DogNameAndDobActivity.this).load(imageURL).into(NameAndDOB_IV_DogImage));
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e("cloudinary","Upload failed: " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        });
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

    private void SaveDogInDBAndNextIntent(){

        String DogBreed = getIntent().getStringExtra("selected breed");
        String DogName = NameAndDOB_ET_inputDogName.getText().toString();
    //    String DogDob = (day + "." + month + "." + year);
        String DogDob = String.format("%02d.%02d.%d", day, month, year);
        String dogWeightStr = NameAndDOB_ET_inputWeight.getText().toString();
        float DogWeight = Float.parseFloat(dogWeightStr); // CRUSH

        NewDog dog = new NewDog();
        dog.setBreed(DogBreed);
        dog.setOwnerEmail(CurrentUserManager.getInstance().getOwner().getMail());
        dog.setPhotoURL(imageURL);
        dog.setName(DogName);
        dog.setDob(DogDob);
        dog.setGender(selectedGender);
        dog.setWeight(DogWeight);

        Call<Dog> call = dogApiService.createDog(dog);
        call.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                Dog createdDog = response.body();
                if(response.isSuccessful() && createdDog!=null) {
                    Toast.makeText(DogNameAndDobActivity.this, "Dog created successfully!", Toast.LENGTH_LONG).show();
                    Call<Void> addDogToListCall = ownerApiService.addDog(createdDog.getOwnerEmail(),createdDog.getName());
                    addDogToListCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(DogNameAndDobActivity.this,"Dog added to owner list successfully!",Toast.LENGTH_LONG).show();
                            CurrentDogManager.getInstance().setDog(createdDog,getApplicationContext());
                            SharedPreferencesHelper prefs = new SharedPreferencesHelper(DogNameAndDobActivity.this);
                            prefs.saveDog(createdDog);
                            switchIntent();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable throwable) {
                            Log.e("Dog upload failed" , throwable.getMessage().toString());

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });
    }

    private void switchIntent(){
        Intent intent = new Intent(DogNameAndDobActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}