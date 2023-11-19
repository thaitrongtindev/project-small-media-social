package com.example.socialmediasmall.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.UserAdapter;
import com.example.socialmediasmall.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {


    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mListUser;
    CollectionReference collectionReference;


    public SearchFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        collectionReference = FirebaseFirestore.getInstance().collection("Users");

        loadUserData();
        searchUser();


    }

    private void searchUser() {
        // listener event searchview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                collectionReference.orderBy("search").startAt(query).endAt(query + "\uf8ff")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    mListUser.clear();
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        if (!snapshot.exists()) {
                                            return;
                                        }

                                        User users = snapshot.toObject(User.class);
                                        mListUser.add(users);
                                    }
                                    userAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    loadUserData();
                }
                return false;
            }
        });
    }

    private void loadUserData() {


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value == null) {
                    return;
                }
                mListUser.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    User users = snapshot.toObject(User.class);
                    Log.e("User", users.getEmail());
                    Log.e("LítUser", mListUser.toString());
                    mListUser.add(users);

                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void init(View view) {
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListUser = new ArrayList<>();
        userAdapter = new UserAdapter(mListUser);
        recyclerView.setAdapter(userAdapter);

    }
}