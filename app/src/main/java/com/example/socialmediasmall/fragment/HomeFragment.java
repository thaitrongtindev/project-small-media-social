package com.example.socialmediasmall.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.ReplacerActivity;
import com.example.socialmediasmall.adapter.HomeFragmentAdapter;
import com.example.socialmediasmall.interfaceListener.IOnPressed;
import com.example.socialmediasmall.model.HomeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Arrays;
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

    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();

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
        recyclerView.setHasFixedSize(true);

        Log.e("mListHomeModels", mListHomeModels.size() + "");
        homeFragmentAdapter = new HomeFragmentAdapter(mListHomeModels, getActivity());
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
            public void setCommentCount(TextView textView) {
                Activity activity = getActivity();

                commentCount.observe((LifecycleOwner) activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (commentCount.getValue() == 0) {
                            textView.setVisibility(View.GONE);
                        } else
                            textView.setVisibility(View.VISIBLE);
                        textView.setText("Set all " + commentCount.getValue() + " Comments");
                    }
                });

            }
        });
    }


    private boolean isHomeModelExists(String homeModelId) {
        for (HomeModel model : mListHomeModels) {
            if (model.getId().equals(homeModelId)) {
                return true; // Trả về true nếu HomeModel đã tồn tại trong danh sách
            }
        }
        return false; // Trả về false nếu HomeModel không tồn tại trong danh sách
    }

    private void loadDatatFromFirebase() {
//        CollectionReference collectionReference = FirebaseFirestore.getInstance()
//                .collection("Users").document(mUser.getUid())
//                .collection("Post Images");

        reference = FirebaseFirestore.getInstance()
                .collection("Users").document(mUser.getUid());

        Log.e("LOADĐATA", mUser.getUid().toString());

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
                //   HomeModel homeModel = (HomeModel) value.toObject(HomeModel.class);
                List<String> uidList = (List<String>) value.get("following"); // lấy list người dùng hiện tại đang theo dỗi người khác
                Log.e("uidList", uidList.toString());

                if (uidList == null) {
                    return;
                }

                collectionReference.whereIn("uid", uidList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.e("eror", error.getMessage());
                                }

                                for (QueryDocumentSnapshot snapshot : value) {
                                    snapshot.getReference().collection("Post Images").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            //  Log.e("eror", error.getMessage());
                                            if (error != null) {
                                                Log.e("eror", error.getMessage());
                                            }
                                            if (value == null) {
                                                return;
                                            }
                                            //  mListHomeModels.clear();


//                                            if (mListHomeModels.size() == 0) {
//                                                mListHomeModels.clear();
//                                            } // Xóa danh sách hiện tại trước khi thêm dữ liệu mới
                                            Log.e("Home1", "" + mListHomeModels.size());
                                            for (QueryDocumentSnapshot snapshot1 : value) {
                                                if (!snapshot1.exists())
                                                    return;

                                                System.out.println(snapshot1.getData());
                                                //    Log.e("Snapshot1",snapshot1.getData().toString() );
                                                HomeModel homeModel = snapshot1.toObject(HomeModel.class);
                                                if (!isHomeModelExists(homeModel.getId())) {
                                                    mListHomeModels.add(new HomeModel(homeModel.getName(),
                                                            homeModel.getTimestamp(),
                                                            homeModel.getProfileImage(),
                                                            homeModel.getImageUrl(),
                                                            homeModel.getUid(),
                                                            homeModel.getDescription(),
                                                            homeModel.getId(),
                                                            homeModel.getLikes()

                                                    ));
                                                    //được sử dụng để lấy danh sách các comment từ Firestore.
                                                    snapshot1.getReference().collection("comments").get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        int count = 0;
                                                                        for (QueryDocumentSnapshot snapshot2 : task.getResult()) {
                                                                            Log.e("snapshot2 HOME", snapshot2.getData().toString() );
                                                                            count++;
                                                                        }
                                                                        //comment count
                                                                        commentCount.setValue(count);
                                                                        Log.e("commentCount", commentCount.getValue().toString() );

                                                                    }
                                                                }
                                                            });
                                                }

                                            }

                                            Log.e("Home2", "" + mListHomeModels.size());

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