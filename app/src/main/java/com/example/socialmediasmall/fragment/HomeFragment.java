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

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.HomeFragmentAdapter;
import com.example.socialmediasmall.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeFragmentAdapter homeFragmentAdapter;
    private List<HomeModel> mListHomeModels;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference reference;


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

        mListHomeModels = new ArrayList<>();
        homeFragmentAdapter = new HomeFragmentAdapter(mListHomeModels, getContext());
        recyclerView.setAdapter(homeFragmentAdapter);

        loadDatatFromFirebase();
    }

    private void loadDatatFromFirebase() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Users").document(mUser.getUid())
                .collection("Post Images");

        collectionReference.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Error", error.getMessage());
                    return;
                }

                for (QueryDocumentSnapshot snapshot : value) {
                    // get data from Post Images to display HomeFragment
                    mListHomeModels.add(new HomeModel(
                            (String) snapshot.get("username".toString()),
                            (String) snapshot.get("timestamps".toString()),
                            (String) snapshot.get("profileImage".toString()),
                            (String) snapshot.get("postImage".toString()),
                            (String) snapshot.get("uId".toString()),
                            Integer.parseInt(snapshot.get("likeCount").toString()),
                            (String) snapshot.get("comments".toString()),
                            (String) snapshot.get("description".toString()),
                            (String) snapshot.get("id".toString())

                    ));
                    homeFragmentAdapter.notifyDataSetChanged();

                }
            }
        });
        homeFragmentAdapter.notifyDataSetChanged();
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