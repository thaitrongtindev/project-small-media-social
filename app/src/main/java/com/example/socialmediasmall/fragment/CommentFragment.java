package com.example.socialmediasmall.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

//        String image = "data:image/jpeg;base64,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";
//
//        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
//
//        builder.setPhotoUri(Uri.parse(image));
//        mUser.updateProfile(builder.build());
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
                map.put("name", mUser.getDisplayName());
                map.put("profileImageUrl", mUser.getPhotoUrl());

                collectionReference.document(commentID)
                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Send success", Toast.LENGTH_SHORT).show();
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
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;
                if (value == null)
                    return;

                CommentModel commentModel = null;
                Log.e("mlistComment", mlistComment.size() + "");
                mlistComment.clear();
                for (DocumentSnapshot snapshot : value) {

                    Log.e("snapshot commentFragment", snapshot.getData().toString());
                    Log.e("snapshot value", value.size() + "");
                    commentModel = snapshot.toObject(CommentModel.class);
                    mlistComment.add(commentModel);
                    Log.e("mlistComment", mlistComment.size() + "");

                }
                commentAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0); // Di chuyển đến vị trí đầu tiên

                Log.e("mlistComment_1", mlistComment.size() + "");
            }
        });
    }

    private void init(View view) {
        sendBtn = view.findViewById(R.id.sendBtn);
        commentEdt = view.findViewById(R.id.commentEDT);
        recyclerView = view.findViewById(R.id.commentRecyclerView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mlistComment = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true); // Hiển thị dữ liệu từ dưới lên

        commentAdapter = new CommentAdapter(getContext(), mlistComment);
        recyclerView.setAdapter(commentAdapter);

        if (getArguments() == null)
            return;

        id = getArguments().getString("id");
        uid = getArguments().getString("uid");
    }
}