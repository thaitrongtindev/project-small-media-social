package com.example.socialmediasmall.fragment;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.model.PostImageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private TextView nameTv, toolbarNameTv, statusTv, followingCountTv, followersCountTv, postCountTv;
    private CircleImageView profileImage;
    private Button followBtn;
    private Boolean isMyProfile = true;
    private RecyclerView recyclerView;
    private LinearLayout countLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private ImageButton edtProfileBtn;
    FirestoreRecyclerAdapter<PostImageModel, PostImageViewHolder> adapter;


    private void uploadImages(Uri imageUri) {
        //upload storage
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("Profile Images");
        reference.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                                    request.setPhotoUri(uri);
                                    mUser.updateProfile(request.build());

                                    // update image again while user edit profile image
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("profileImage", imageUrl);
                                    //firebaseFirestore
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(mUser.getUid())
                                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Updated Profile Users Successful", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Updated Profile Users Error", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Upload Storage Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        if (isMyProfile) {
            followBtn.setVisibility(GONE);
            countLayout.setVisibility(View.VISIBLE);
        } else {
            followBtn.setVisibility(View.VISIBLE);
            countLayout.setVisibility(GONE);
        }

        loadBasicData();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadPostImages();
        recyclerView.setAdapter(adapter);

        //edit profile image
        edtProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnEditProfileImage();
            }
        });

    }

    private void clickOnEditProfileImage() {
    }



    private void loadPostImages() {
        if (isMyProfile == true) {
            uid = mUser.getUid();
        } else {
        }

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("Users").document(uid);

        Query query = reference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostImageViewHolder holder, int position, @NonNull PostImageModel model) {
                Glide.with(holder.itemView.getContext().getApplicationContext()).
                        load(model.getImageUrl())
                        .timeout(6500)
                        .into(holder.postImageView);

            }

            @NonNull
            @Override
            public PostImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_item, parent, false);
                return new PostImageViewHolder(view);
            }
        };

    }

    private class PostImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImageView;

        public PostImageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageView);

        }
    }

    private void loadBasicData() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                .document(mUser.getUid());

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    String name = value.getString("name");
                    String status = value.getString("status");
                    int followers = Math.toIntExact(value.getLong("followers"));
                    int following = Math.toIntExact(value.getLong("following"));

                    nameTv.setText(name);
                    toolbarNameTv.setText(name);
                    statusTv.setText(status);
                    followingCountTv.setText(String.valueOf(following));
                    followersCountTv.setText(String.valueOf(followers));

                    String profileURL = value.getString("profileImage");
                    Glide.with(getContext().getApplicationContext())
                            .load(profileURL)
                            .placeholder(R.drawable.ic_person)
                            .timeout(6500)
                            .into(profileImage);

                } else {
                    Log.e("Error", error.toString());
                }
            }
        });

        postCountTv.setText(String.valueOf("" + HomeFragment.LIST_SIZE));
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        nameTv = view.findViewById(R.id.nameTv);
        toolbarNameTv = view.findViewById(R.id.toolbarName);
        statusTv = view.findViewById(R.id.statusTv);
        followersCountTv = view.findViewById(R.id.followerCountTv);
        followingCountTv = view.findViewById(R.id.followingCountTv);
        postCountTv = view.findViewById(R.id.postsCountTv);
        profileImage = view.findViewById(R.id.profileImage);
        recyclerView = view.findViewById(R.id.recyclerView);
        countLayout = view.findViewById(R.id.countLayout);

        followBtn = view.findViewById(R.id.followBtn);

        edtProfileBtn = view.findViewById(R.id.edit_profileIamge);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}