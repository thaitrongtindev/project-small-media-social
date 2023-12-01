package com.example.socialmediasmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.hardware.lights.LightState;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.interfaceListener.IOnPressed;
import com.example.socialmediasmall.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.HomeViewHolder> {


    private List<HomeModel> listHomeModel;
    private Context context;

    private IOnPressed iOnPressed;

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
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.usernameTv.setText(listHomeModel.get(position).getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String s = sdf.format(listHomeModel.get(position).getTimestamp());
        holder.timeTv.setText(s);

        List<String> likes = listHomeModel.get(position).getLikes();
        int count = likes.size();
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
    }

    @Override
    public int getItemCount() {
        return listHomeModel.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTv, timeTv, likeCountTv , descriptionTv;
        private CircleImageView profileImage;
        private ImageButton likeBtn, commentBtn, shareBtn, commentSendBtn;
        private ImageView imageView;
        private CheckBox likeCheckbox;
        private EditText commentEdit;
        private LinearLayout commentLayout;
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

            commentEdit = itemView.findViewById(R.id.commentEdit);
            commentSendBtn = itemView.findViewById(R.id.commentSendBtn);
            commentLayout = itemView.findViewById(R.id.commentLayout);

        }

        public void clickListener(int position, String id, String name, String uid, List<String> mListLikes) {

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commentLayout.getVisibility() == View.GONE) {
                        commentLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            likeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    iOnPressed.onLiked(position, id, uid , mListLikes, isChecked);
                }
            });

            commentSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment = commentEdit.getText().toString();
                    iOnPressed.onComment(position, id, uid,comment, commentLayout, commentEdit);


                }
            });
        }
    }

    public void onPressed(IOnPressed iOnPressed) {
        this.iOnPressed = iOnPressed;
    }
}
