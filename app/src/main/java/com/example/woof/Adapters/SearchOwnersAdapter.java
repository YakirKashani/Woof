package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SearchOwnersAdapter extends RecyclerView.Adapter<SearchOwnersAdapter.SearchResultViewHolder>{
    private List<Owner> ownersFound;
    private OnOwnerSelectedListener listener;

    public interface OnOwnerSelectedListener{
        void OnOwnerSelected(Owner selectedOwner);
    }

    public SearchOwnersAdapter(List<Owner> ownersFound, OnOwnerSelectedListener listener){
        this.ownersFound = ownersFound;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_owner_profile_card_in_search,parent,false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        Owner owner = ownersFound.get(position);
        holder.POS_MTV_OwnerMail.setText(owner.getMail());
        if(owner.getPhotoURL() == null)
            holder.POS_IV_OwnerPicture.setImageResource(R.drawable.default_owner_picture);
        else
            Glide.with(holder.itemView.getContext()).load(owner.getPhotoURL()).error(R.drawable.default_owner_picture).into(holder.POS_IV_OwnerPicture);

        holder.POS_RL_Container.setOnClickListener(v -> {
            if(listener!=null)
                listener.OnOwnerSelected(owner);
        });
    }

    @Override
    public int getItemCount() {
        return ownersFound.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView POS_IV_OwnerPicture;
        private MaterialTextView POS_MTV_OwnerMail;
        private RelativeLayout POS_RL_Container;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            POS_IV_OwnerPicture = itemView.findViewById(R.id.POS_IV_OwnerPicture);
            POS_MTV_OwnerMail = itemView.findViewById(R.id.POS_MTV_OwnerMail);
            POS_RL_Container = itemView.findViewById(R.id.POS_RL_Container);
        }
    }
}
