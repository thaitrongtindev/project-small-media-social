package com.example.socialmediasmall.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.CommentAdapter;
import com.example.socialmediasmall.model.CommentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentFragment extends Fragment {


    private EditText commentEdt;
    private ImageButton sendBtn;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentModel> mlistComment;
    private FirebaseUser mUser;
    private String id, uid;
    private CollectionReference collectionReference;
    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        collectionReference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid).collection("Post Images")
                .document(id)
                .collection("comments");

        loadCommentData();
        clickListener();
    }

    private void clickListener() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEdt.getText().toString().trim();
                if (comment.isEmpty() || comment == "") {
                    Toast.makeText(getContext(), "Enter comment", Toast.LENGTH_SHORT).show();
                    return;
                }



                String commentID = collectionReference.document().getId();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", mUser.getUid());
                map.put("comment", comment);
                map.put("commentID", commentID);
                map.put("postID", id);

                collectionReference.document(commentID)
                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    commentEdt.setText("");
                                } else {
                                    Toast.makeText(getContext(), "Failed to comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void loadCommentData() {

    }

    private void init(View view) {
        sendBtn = view.findViewById(R.id.sendBtn);
        commentEdt = view.findViewById(R.id.commentEDT);
        recyclerView = view.findViewById(R.id.recyclerView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mlistComment = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), mlistComment);
        recyclerView.setAdapter(commentAdapter);

        if (getArguments() == null)
            return;

        id = getArguments().getString("id");
        uid = getArguments().getString("uid");
    }
}