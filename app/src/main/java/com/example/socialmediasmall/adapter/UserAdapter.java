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
import com.example.socialmediasmall.interfaceListener.IOnUserClick;
import com.example.socialmediasmall.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> mListUser;
    private IOnUserClick iOnUserClick;

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

         /*
       nghĩa là người dùng đang xem là chính người dùng hiện tại, và trong trường hợp này,
       holder.relativeLayout sẽ được đặt là View.GONE, làm ẩn đi một phần tử giao diện người dùng nào đó
        */
        if (mListUser.get(position).getUid().equals(mUser.getUid())) {
            holder.relativeLayout.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.relativeLayout.setVisibility(View.VISIBLE);

        }
        holder.nameTv.setText(mListUser.get(position).getName());
        holder.statusTv.setText(mListUser.get(position).getStatus());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(mListUser.get(position).getProfileImage())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.profileImage);


       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               iOnUserClick.onClicked(mListUser.get(position).getUid());
           }
       });
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

    public void IOnUserClicked(IOnUserClick iOnUserClick) {
       this.iOnUserClick = iOnUserClick;
    }
}
