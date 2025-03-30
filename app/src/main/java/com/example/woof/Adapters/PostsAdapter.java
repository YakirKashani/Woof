package com.example.woof.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Post;
import com.example.woof.R;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>{
    private Context context;
    private List<Post> posts;
    private OnPostSelectedListener listener;

    public interface OnPostSelectedListener{
        void OnPostSelected(Post post);
    }

    public PostsAdapter(Context context, List<Post> posts,OnPostSelectedListener listener){
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        if(post.getPictureUrl() == null){
            holder.IP_IV_PostImage.setImageResource(R.drawable.exclamation);
        }else{
            Glide.with(holder.itemView.getContext()).load(post.getPictureUrl()).error(R.drawable.exclamation).into(holder.IP_IV_PostImage);
        }

        holder.itemView.setOnClickListener(v -> {
            listener.OnPostSelected(post);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private ImageView IP_IV_PostImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            IP_IV_PostImage = itemView.findViewById(R.id.IP_IV_PostImage);
        }
    }
}
