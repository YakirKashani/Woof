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

import com.bumptech.glide.Glide;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.databinding.FragmentNotificationsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ImageView FN_IV_DogPicture;
    private TextView FN_MTV_OwnerEmail;
    private ImageView FN_IV_OwnerPicture;
    private MaterialTextView textName;


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
        FN_IV_DogPicture = binding.FNIVDogPicture;
        FN_MTV_OwnerEmail = binding.textOwner;
        textName = binding.textName;
        FN_IV_OwnerPicture = binding.FNIVOwnerPicture;
    }

    private void initViews(){
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

        FN_MTV_OwnerEmail.setText(CurrentUserManager.getInstance().getOwner().getMail());
        textName.setText(CurrentDogManager.getInstance().getDog().getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}