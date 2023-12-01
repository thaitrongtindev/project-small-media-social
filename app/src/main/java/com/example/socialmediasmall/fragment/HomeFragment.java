package com.example.socialmediasmall.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.HomeFragmentAdapter;
import com.example.socialmediasmall.interfaceListener.IOnPressed;
import com.example.socialmediasmall.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    public static int LIST_SIZE = 0;
    private RecyclerView recyclerView;
    private HomeFragmentAdapter homeFragmentAdapter;
    private List<HomeModel> mListHomeModels;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference reference;
    private List<String> mListLikes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        //document reference
        //  reference = FirebaseFirestore.getInstance().collection("Posts").document(mUser.getUid());
        mListLikes = new ArrayList<>();
        mListHomeModels = new ArrayList<>();
        homeFragmentAdapter = new HomeFragmentAdapter(mListHomeModels, getContext());
        recyclerView.setAdapter(homeFragmentAdapter);

        loadDatatFromFirebase();

        homeFragmentAdapter.onPressed(new IOnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likes, boolean isChecked) {
                 reference = FirebaseFirestore.getInstance()
                        .collection("Users").document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likes.contains(mUser.getUid())) {
                    // user hiện tại đã like rồi mà nhấn vào nữa thì xóa
                    likes.remove(mUser.getUid());
                } else {
                    likes.add(mUser.getUid());
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likes);

                reference.update(map);
            }

            @Override
            public void onComment(int position, String id, String uid, String comment) {
                if (comment.isEmpty() != comment.equals(" ")) {
                    Toast.makeText(getContext(), "Can not send empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                CollectionReference collectionReference = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(uid).collection("Post Images")
                        .document(id)
                        .collection("Comments");

                String commentID = collectionReference.document().getId();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", mUser.getUid());
                map.put("comment", comment);
                map.put("commentID", commentID);
                map.put("postID", id);

                collectionReference.document(commentID)
                        .set(map);




            }
        });
    }

    private void loadDatatFromFirebase() {
//        CollectionReference collectionReference = FirebaseFirestore.getInstance()
//                .collection("Users").document(mUser.getUid())
//                .collection("Post Images");

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("Users").document(mUser.getUid());

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Error", error.getMessage());
                    return;
                }
                if (value == null) {
                    return;
                }
                HomeModel homeModel = (HomeModel) value.toObject(HomeModel.class);
                List<String> uidList = (List<String>) value.get("follwing");

                collectionReference.whereIn("uid", uidList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.d("eror", error.getMessage());
                                }
                                for (QueryDocumentSnapshot snapshot : value) {
                                    snapshot.getReference().collection("Post Images")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot snapshot1 : value) {
                                                        HomeModel homeModel = snapshot.toObject(HomeModel.class);
                                                        Log.e("Home", "" + homeModel.getName());
                                                        mListHomeModels.add(new HomeModel(homeModel.getName(),
                                                                homeModel.getTimestamp(),
                                                                homeModel.getProfileImage(),
                                                                homeModel.getImageUrl(),
                                                                homeModel.getUid(),
                                                                homeModel.getComments(),
                                                                homeModel.getDescription(),
                                                                homeModel.getId(),
                                                                homeModel.getLikes()
                                                        ));
                                                    }
                                                    homeFragmentAdapter.notifyDataSetChanged();

                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }
}