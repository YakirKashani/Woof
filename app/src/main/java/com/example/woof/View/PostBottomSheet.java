package com.example.woof.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Adapters.CommentsAdapter;
import com.example.woof.Model.Comment;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.example.woof.WoofBackend.PostApi;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_POST = "post";
    private Post post;
    private String dogImage;
    private String ownerImage;
    private boolean isLiked;
    PostApi postApiService = ApiController.getRetrofitInstance().create(PostApi.class);
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);
    private ShapeableImageView PBSL_SIV_dogPhoto;
    private TextView PBSL_TV_dogName;
    private ShapeableImageView PBSL_SIV_ownerPhoto;
    private ShapeableImageView PBSL_SIV_postImage;
    private ShapeableImageView PBSL_SIV_like;
    private MaterialTextView PBSL_MTV_PostDescription;
    private EditText PBSL_ET_Comment;
    private ShapeableImageView PBSL_SIV_PostComment;
    private RecyclerView PBSL_RV_Comments;
    private boolean likesApeerance;


    public static PostBottomSheet newInstance(Post post, boolean likesApeerance) {
        PostBottomSheet fragment = new PostBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        args.putBoolean("likesApeerance", likesApeerance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getView().getParent() instanceof View ? (View) getView().getParent() : null;
        if(bottomSheet!=null){
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_bottom_sheet_layout, container, false);

        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(ARG_POST);
            likesApeerance = getArguments().getBoolean("likesApeerance");
        }
        PBSL_SIV_dogPhoto = view.findViewById(R.id.PBSL_SIV_dogPhoto);
        PBSL_TV_dogName = view.findViewById(R.id.PBSL_TV_dogName);
        PBSL_SIV_ownerPhoto = view.findViewById(R.id.PBSL_SIV_ownerPhoto);
        PBSL_SIV_postImage = view.findViewById(R.id.PBSL_SIV_postImage);
        PBSL_SIV_like = view.findViewById(R.id.PBSL_SIV_like);
        PBSL_MTV_PostDescription = view.findViewById(R.id.PBSL_MTV_PostDescription);
        PBSL_ET_Comment = view.findViewById(R.id.PBSL_ET_Comment);
        PBSL_SIV_PostComment = view.findViewById(R.id.PBSL_SIV_PostComment);
        PBSL_RV_Comments = view.findViewById(R.id.PBSL_RV_Comments);

        CommentsAdapter commentsAdapter = new CommentsAdapter(post.getComments());
        PBSL_RV_Comments.setAdapter(commentsAdapter);
        PBSL_RV_Comments.setLayoutManager(new LinearLayoutManager(getContext()));

        if(likesApeerance){
            PBSL_SIV_like.setVisibility(View.VISIBLE);
        } else {
            PBSL_SIV_like.setVisibility(View.GONE);
        }

        PBSL_SIV_PostComment.setOnClickListener(v -> {
            String commentText = PBSL_ET_Comment.getText().toString().trim();
            if(!commentText.isEmpty()){
                Comment comment = new Comment();
                comment.setComment(commentText);
                comment.setOwnerMail(CurrentDogManager.getInstance().getDog().getOwnerEmail());
                comment.setDogName(CurrentDogManager.getInstance().getDog().getName());
                Call<Post> commentCall = postApiService.addComment(post.getId(), comment);
                commentCall.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if(response.isSuccessful()){
                            // TODO: refresh the post
                            refreshPost(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable throwable) {

                    }
                });


            }
        });



        Call<Owner> OwnerCall = ownerApiService.findOwner(post.getDogOwner());
        OwnerCall.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                if (response.body().getPhotoURL() != null)
                    Glide.with(getContext()).load(response.body().getPhotoURL()).error(R.drawable.default_owner_picture).into(PBSL_SIV_ownerPhoto);
                else
                    PBSL_SIV_ownerPhoto.setImageResource(R.drawable.default_owner_picture);
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {

            }
        });

        Call<Dog> DogCall = dogApiService.findDog(post.getDogOwner(), post.getDogName());
        DogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                if (response.body().getPhotoURL() != null)
                    Glide.with(getContext()).load(response.body().getPhotoURL()).error(R.drawable.default_dog_picture).into(PBSL_SIV_dogPhoto);
                else
                    PBSL_SIV_dogPhoto.setImageResource(R.drawable.default_dog_picture);
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });
        if (post != null) {
            Glide.with(this).load(post.getPictureUrl()).error(R.drawable.exclamation).into(PBSL_SIV_postImage);
            PBSL_MTV_PostDescription.setText(post.getDescription());
            PBSL_TV_dogName.setText(post.getDogName());
            /* Like / Unlike */
            if (post.getDogsLiked().contains(CurrentDogManager.getInstance().getDog().getOwnerEmail() + "#" + CurrentDogManager.getInstance().getDog().getName())) {
                PBSL_SIV_like.setImageResource(R.drawable.paw_heart_full);
                isLiked = true;
            } else {
                PBSL_SIV_like.setImageResource(R.drawable.paw_heart);
                isLiked = false;
            }

            PBSL_SIV_like.setOnClickListener(v -> {
                if (isLiked) {
                    Log.e("like_call", CurrentDogManager.getInstance().getDog().toString());
                    Call<Boolean> unlikeCall = postApiService.unlike(post.getId(), CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName());
                    unlikeCall.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Log.e("like_call", response.body().toString());
                            if (response.body()) {
                                Glide.with(PostBottomSheet.this).load(R.drawable.paw_heart).into(PBSL_SIV_like);
                                isLiked = false;
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable throwable) {
                            Log.e("like_call", throwable.getMessage().toString());
                        }
                    });

                } else {
                    Log.e("like_call", CurrentDogManager.getInstance().getDog().toString());
                    Call<Boolean> likeCall = postApiService.addLike(post.getId(), CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName());
                    likeCall.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.body()) {
                                Glide.with(PostBottomSheet.this).load(R.drawable.paw_heart_full).into(PBSL_SIV_like);
                                isLiked = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable throwable) {
                            Log.e("like_call", throwable.getMessage().toString());
                        }
                    });

                }


            });


        }
        if (dogImage != null) {
            Glide.with(this).load(dogImage).error(R.drawable.default_dog_picture).into(PBSL_SIV_dogPhoto);
        } else {
            PBSL_SIV_dogPhoto.setImageResource(R.drawable.default_dog_picture);
        }

        if (ownerImage != null) {
            Glide.with(this).load(ownerImage).error(R.drawable.default_owner_picture).into(PBSL_SIV_ownerPhoto);
        } else {
            PBSL_SIV_ownerPhoto.setImageResource(R.drawable.default_owner_picture);
        }

        return view;
    }

    private void refreshPost(Post updatedPost) {
        this.post = updatedPost;
        PBSL_ET_Comment.setText("");
        PBSL_ET_Comment.clearFocus();
        PBSL_ET_Comment.clearComposingText();
        if(PBSL_RV_Comments.getAdapter() instanceof CommentsAdapter)
            ((CommentsAdapter) PBSL_RV_Comments.getAdapter()).updateComments(post.getComments());
    }
}
