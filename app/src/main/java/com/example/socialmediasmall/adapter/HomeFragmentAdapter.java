package com.example.socialmediasmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.hardware.lights.LightState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.model.HomeModel;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.HomeViewHolder> {


    private List<HomeModel> listHomeModel;
    private Context context;

    public HomeFragmentAdapter(List<HomeModel> listHomeModel, Context context) {
        this.listHomeModel = listHomeModel;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        holder.usernameTv.setText(listHomeModel.get(position).getUsername());
        holder.timeTv.setText(listHomeModel.get(position).getTimestamps());

        int count = listHomeModel.get(position).getLikeCount();
        if (count == 0) {
            holder.likeCountTv.setVisibility(View.GONE);
        } else if (count == 1){
            holder.likeCountTv.setText(count + " like");
        } else {
            holder.likeCountTv.setText(count + " likes");
        }
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(255), random.nextInt(256), random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(listHomeModel.get(position).getProfileImage())
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(listHomeModel.get(position).getProfileImage())
                .timeout(7000)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listHomeModel.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTv, timeTv, likeCountTv;
        private CircleImageView profileImage;
        private ImageButton likeBtn, commentBtn, shareBtn;
        private ImageView imageView;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            profileImage = itemView.findViewById(R.id.profileImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
