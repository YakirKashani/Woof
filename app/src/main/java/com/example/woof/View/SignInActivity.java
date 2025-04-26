package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.Singleton.SharedPreferencesHelper;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private EditText SignIn_ET_Email;
    private MaterialButton SignIn_MB_SignIn;
    private TextView SignIn_MTV_SignUp;
    OwnerApi apiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setContent();
        findViews();
        initViews();
    }

    private void setContent(){
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        Owner owner = prefs.getOwner();
        Dog dog = prefs.getDog();

        if(owner == null){
            setContentView(R.layout.activity_sign_in);
        } else{
            CurrentUserManager.getInstance().setOwner(owner);
            if(dog == null){
                Intent intent = new Intent(SignInActivity.this,ChooseDogActivity.class);
                startActivity(intent);
            } else{
                CurrentDogManager.getInstance().setDog(dog);
                Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    private void findViews() {
        SignIn_ET_Email = findViewById(R.id.inputEmail);
        SignIn_MB_SignIn = findViewById(R.id.buttonSignIn);
        SignIn_MTV_SignUp = findViewById(R.id.SignUp);
    }

    private void initViews() {
        SignIn_MB_SignIn.setOnClickListener(View -> SignInClicked());
        SignIn_MTV_SignUp.setOnClickListener(View -> SignUpClicked());
    }

    private void SignInClicked(){
        String mail = SignIn_ET_Email.getText().toString();
        Call<Owner> call = apiService.findOwner(mail);
        call.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner owner = response.body();
                if(response.isSuccessful() && owner!=null){
                    CurrentUserManager.getInstance().setOwner(owner);
                    SharedPreferencesHelper prefs = new SharedPreferencesHelper(SignInActivity.this);
                    prefs.saveOwner(owner);
                    Toast.makeText(SignInActivity.this,"Hello " + owner.getMail(),Toast.LENGTH_LONG).show();
                    SwitchToChooseDogActivity();
                }
                else{
                    Toast.makeText(SignInActivity.this,"Failed to log in ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {
                Log.e("Retrofit", "Network error or failure " + throwable.getMessage());
                Toast.makeText(SignInActivity.this,"Connection error occured",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SignUpClicked(){
        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void SwitchToChooseDogActivity(){
        Intent intent = new Intent(SignInActivity.this,ChooseDogActivity.class);
        startActivity(intent);
        finish();
    }
}