package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.Model.Post;
import com.example.woof.R;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeddViewHolder>{
    private Context context;
    private List<Post> posts;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    public FeedAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public FeddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_in_feed,parent,false);
        return new FeddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeddViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.post_MTV_dogName.setText(post.getDogName());

        holder.post_description.setText(post.getDescription());

        if(post.getPictureUrl() == null){
            holder.post_image.setImageResource(R.drawable.exclamation);
        } else{
            Glide.with(holder.itemView.getContext()).load(post.getPictureUrl()).error(R.drawable.exclamation).into(holder.post_image);
        }

        Call<Dog> dogCall = dogApiService.findDog(post.getDogOwner(), post.getDogName());
        dogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                Dog dog = response.body();
                if(dog!=null){
                    if(dog.getPhotoURL()==null){
                        holder.post_SIV_dogPhoto.setImageResource(R.drawable.default_dog_picture);
                    } else{
                        Glide.with(holder.itemView.getContext()).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(holder.post_SIV_dogPhoto);
                    }
                }
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });

        Call<Owner> ownerCall = ownerApiService.findOwner(post.getDogOwner());
        ownerCall.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner owner = response.body();
                if(owner!=null){
                    if(owner.getPhotoURL()==null){
                        holder.post_SIV_ownerPhoto.setImageResource(R.drawable.default_owner_picture);;
                    } else{
                        Glide.with(holder.itemView.getContext()).load(owner.getPhotoURL()).error(R.drawable.default_owner_picture).into(holder.post_SIV_ownerPhoto);
                    }
                }
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class FeddViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView post_SIV_dogPhoto;
        private TextView post_MTV_dogName;
        private ImageView post_SIV_ownerPhoto;
        private ShapeableImageView post_image;
        private MaterialTextView post_description;

        public FeddViewHolder(@NonNull View itemView) {
            super(itemView);
            post_SIV_dogPhoto = itemView.findViewById(R.id.post_SIV_dogPhoto);
            post_MTV_dogName = itemView.findViewById(R.id.post_MTV_dogName);
            post_SIV_ownerPhoto = itemView.findViewById(R.id.post_SIV_ownerPhoto);
            post_image = itemView.findViewById(R.id.post_image);
            post_description = itemView.findViewById(R.id.post_description);
        }
    }
}
