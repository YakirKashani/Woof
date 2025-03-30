package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Comment;
import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.PostApi;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
    private List<Comment> comments;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    PostApi postApiService = ApiController.getRetrofitInstance().create(PostApi.class);


    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void updateComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.IC_MTV_DogName.setText(comment.getDogName());
        holder.IC_MTV_Comment.setText(comment.getComment());
        String ownerEmail = comment.getOwnerMail();
        String dogName = comment.getDogName();
        Call<Dog> dogCall = dogApiService.findDog(ownerEmail,dogName);
        dogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                if(response.isSuccessful()){
                    Dog dog = response.body();
                    if(dog.getPhotoURL() == null)
                        holder.IC_SIV_DogPhoto.setImageResource(R.drawable.default_dog_picture);
                    else
                        Glide.with(holder.itemView.getContext()).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.IC_SIV_DogPhoto);
                }
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView IC_SIV_DogPhoto;
        private MaterialTextView IC_MTV_DogName;
        private MaterialTextView IC_MTV_Comment;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            IC_SIV_DogPhoto = itemView.findViewById(R.id.IC_SIV_DogPhoto);
            IC_MTV_DogName = itemView.findViewById(R.id.IC_MTV_DogName);
            IC_MTV_Comment = itemView.findViewById(R.id.IC_MTV_Comment);
        }
    }
}