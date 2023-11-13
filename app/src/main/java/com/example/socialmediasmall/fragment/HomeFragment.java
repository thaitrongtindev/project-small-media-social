package com.example.socialmediasmall.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.HomeFragmentAdapter;
import com.example.socialmediasmall.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        reference = FirebaseFirestore.getInstance().collection("Posts").document(mUser.getUid());

        mListHomeModels = new ArrayList<>();
        homeFragmentAdapter = new HomeFragmentAdapter(mListHomeModels, getContext());
        recyclerView.setAdapter(homeFragmentAdapter);
        
        loadDatatFromFirebase();
    }

    private void loadDatatFromFirebase() {
        mListHomeModels.add(new HomeModel("Trong Tin", "13/11/2023", "", "", "123456", 12));
        mListHomeModels.add(new HomeModel("Van Chuong", "11/11/2023", "", "", "321654", 21));
        mListHomeModels.add(new HomeModel("Trong Nghia", "13/11/2023", "", "", "478945", 10));
        mListHomeModels.add(new HomeModel("Vuong Bui", "13/11/2023", "", "", "316542", 19));
        mListHomeModels.add(new HomeModel("Thai Bao", "13/11/2023", "", "", "846453", 33));

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