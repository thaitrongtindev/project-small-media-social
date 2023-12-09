package com.example.socialmediasmall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.lights.LightState;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.ReplacerActivity;
import com.example.socialmediasmall.interfaceListener.IOnPressed;
import com.example.socialmediasmall.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.HomeViewHolder> {


    private List<HomeModel> listHomeModel;
    private Activity context;

    private IOnPressed iOnPressed;

    public HomeFragmentAdapter(List<HomeModel> listHomeModel, Activity context) {
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
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.usernameTv.setText(listHomeModel.get(position).getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String s = sdf.format(listHomeModel.get(position).getTimestamp());
        holder.timeTv.setText(s);

        List<String> likes = listHomeModel.get(position).getLikes();
        int count = likes.size();
        Log.e("count", ""+count);
        if (count == 0) {
            holder.likeCountTv.setVisibility(View.GONE);
        } else if (count == 1){
            holder.likeCountTv.setText(count + " like");
        } else {
            holder.likeCountTv.setText(count + " likes");
        }

        // check if already liek
        if (likes.contains(mUser.getUid())) {
            holder.likeCheckbox.setChecked(true);
        } else {
            holder.likeCheckbox.setChecked(false);
        }

        holder.descriptionTv.setText(listHomeModel.get(position).getDescription());
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(255), random.nextInt(256), random.nextInt(256));

        // image person profile
        Glide.with(context.getApplicationContext())
                .load(listHomeModel.get(position).getProfileImage())
                .timeout(6500)
                .into(holder.profileImage);

        // image post
        Glide.with(context.getApplicationContext())
                .load(listHomeModel.get(position).getImageUrl())
                .timeout(7000)
                .into(holder.imageView);
        
        holder.clickListener(position, listHomeModel.get(position).getId(), 
                listHomeModel.get(position).getName(),
                listHomeModel.get(position).getUid(),
                listHomeModel.get(position).getLikes())
        ;
        Log.e("Posistion", position + "" );
        Log.e("ID", listHomeModel.get(position).getId() + "");
        Log.e("Name",listHomeModel.get(position).getName() +"") ;
        Log.e("Uid", listHomeModel.get(position).getUid() +"");
        Log.e("Likes", listHomeModel.get(position).getLikes() + "");


    }

    @Override
    public int getItemCount() {
        return listHomeModel.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTv, timeTv, likeCountTv , descriptionTv, commentTV;
        private CircleImageView profileImage;
        private ImageButton  commentBtn, shareBtn;
        private ImageView imageView;
        private CheckBox likeCheckbox;


        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            profileImage = itemView.findViewById(R.id.profileImage);
            likeCheckbox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTv = itemView.findViewById(R.id.descTv);
            commentTV = itemView.findViewById(R.id.commentTV);

            iOnPressed.setCommentCount(commentTV);

        }

        public void clickListener(int position, String id, String name, String uid, List<String> mListLikes) {
            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ReplacerActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("uid", uid);
                    intent.putExtra("isComment", true);

                    context.startActivity(intent);

                }
            });
            likeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    iOnPressed.onLiked(position, id, uid , mListLikes, isChecked);
                }
            });

        }
    }

    public void onPressed(IOnPressed iOnPressed) {
        this.iOnPressed = iOnPressed;
    }
}
