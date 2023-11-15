package com.example.socialmediasmall.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.GalleryAdapter;
import com.example.socialmediasmall.model.GalleryImages;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {


    private EditText descEt;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ImageButton backBtn, nextBtn;
    private GalleryAdapter adapter;
    private List<GalleryImages> listImages;
    public AddFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);
        listImages = new ArrayList<>();
        adapter = new GalleryAdapter(listImages);

        recyclerView.setAdapter(adapter);
    }

    private void init(View view) {
        descEt = view.findViewById(R.id.descriptionET);
        imageView  = view.findViewById(R.id.imageView);
        recyclerView  = view.findViewById(R.id.recyclerView);
        backBtn  = view.findViewById(R.id.backBtn);
        nextBtn  = view.findViewById(R.id.nextBtn);
    }
}