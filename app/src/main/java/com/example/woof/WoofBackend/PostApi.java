package com.example.woof.WoofBackend;

import com.example.woof.Model.Comment;
import com.example.woof.Model.NewPost;
import com.example.woof.Model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostApi {

    @POST("post")
    Call<Post> createPost(@Body NewPost newPost);

    @GET("post/{ownerEmail}/{dogName}")
    Call<List<Post>> getPostsByDog(@Path("ownerEmail") String ownerEmail, @Path("dogName") String dogName);

    @GET("post/feed/{ownerEmail}/{dogName}")
    Call<List<Post>> getPostsFromFollowedDogs(@Path("ownerEmail") String ownerEmail, @Path("dogName") String dogName);

    @POST("post/{postId}/like/{ownerEmail}/{dogName}")
    Call<Boolean> addLike(@Path("postId") Long postId, @Path("ownerEmail") String ownerEmail, @Path("dogName") String dogName);

    @POST("post/{postId}/unlike/{ownerEmail}/{dogName}")
    Call<Boolean> unlike(@Path("postId") Long postId, @Path("ownerEmail") String ownerEmail, @Path("dogName") String dogName);

    @POST("post/{postId}/comment")
    Call<Post> addComment(@Path("postId") Long postId, @Body Comment comment);
}
