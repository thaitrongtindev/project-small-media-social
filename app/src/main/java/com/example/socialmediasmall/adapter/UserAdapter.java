package com.example.socialmediasmall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> mListUser;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();


    public UserAdapter(List<User> mListUser) {
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (mListUser.get(position).getUid().equals(mUser.getUid())) {
            holder.relativeLayout.setVisibility(View.GONE);
        }

        holder.nameTv.setText(mListUser.get(position).getName());
        holder.statusTv.setText(mListUser.get(position).getStatus());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(mListUser.get(position).getProfileImage())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return mListUser.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView nameTv, statusTv;
        private RelativeLayout relativeLayout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameTv = itemView.findViewById(R.id.nameTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
