package com.example.woof.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class PostBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_POST = "post";
    private Post post;
    private String dogImage;
    private String ownerImage;

    public static PostBottomSheet newInstance(Post post, String dogImage, String ownerImage){ // TODO: URL dog, URL owner
        PostBottomSheet fragment = new PostBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        args.putString("dogImageUrl", dogImage);
        args.putString("ownerImageUrl", ownerImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_bottom_sheet_layout, container, false);

        if(getArguments() != null){
            post = (Post) getArguments().getSerializable(ARG_POST);
            dogImage = getArguments().getString("dogImageUrl");
            ownerImage = getArguments().getString("ownerImageUrl");
        }
        ShapeableImageView PBSL_SIV_dogPhoto = view.findViewById(R.id.PBSL_SIV_dogPhoto);
        TextView PBSL_TV_dogName = view.findViewById(R.id.PBSL_TV_dogName);
        ShapeableImageView PBSL_SIV_ownerPhoto = view.findViewById(R.id.PBSL_SIV_ownerPhoto);
        ShapeableImageView PBSL_SIV_postImage = view.findViewById(R.id.PBSL_SIV_postImage);
        ShapeableImageView PBSL_SIV_like = view.findViewById(R.id.PBSL_SIV_like);
        MaterialTextView PBSL_MTV_PostDescription = view.findViewById(R.id.PBSL_MTV_PostDescription);

        if(post!=null){
            Glide.with(this).load(post.getPictureUrl()).error(R.drawable.exclamation).into(PBSL_SIV_postImage);
            PBSL_MTV_PostDescription.setText(post.getDescription());
            PBSL_TV_dogName.setText(post.getDogName());
        }
        if(dogImage != null){
            Glide.with(this).load(dogImage).error(R.drawable.default_dog_picture).into(PBSL_SIV_dogPhoto);
        } else{
            PBSL_SIV_dogPhoto.setImageResource(R.drawable.default_dog_picture);
        }

        if(ownerImage!=null){
            Glide.with(this).load(ownerImage).error(R.drawable.default_owner_picture).into(PBSL_SIV_ownerPhoto);
        } else{
            PBSL_SIV_ownerPhoto.setImageResource(R.drawable.default_owner_picture);
        }

        return view;
    }
}
