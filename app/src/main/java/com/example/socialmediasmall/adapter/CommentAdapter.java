package com.example.socialmediasmall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.model.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<CommentModel> mListComment;

    public CommentAdapter(Context context, List<CommentModel> mListComment) {
        this.context = context;
        this.mListComment = mListComment;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        Glide.with(context).load(mListComment.get(position)
                        .getProfileImageUrl())
                .into(holder.profileImage);
        holder.nameTV.setText(mListComment.get(position).getName());
        holder.commentTV.setText(mListComment.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return mListComment.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV, commentTV;
        private CircleImageView profileImage;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameTV = itemView.findViewById(R.id.nameTV);
            commentTV = itemView.findViewById(R.id.commentTV);
        }
    }
}
