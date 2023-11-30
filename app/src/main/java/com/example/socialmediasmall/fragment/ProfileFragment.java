package com.example.socialmediasmall.fragment;

import static android.view.View.GONE;

import static com.example.socialmediasmall.MainActivity.IS_SEARCHED_USER;
import static com.example.socialmediasmall.MainActivity.USER_ID;

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

import android.provider.MediaStore;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private String userUid;
    private ImageButton edtProfileBtn;
    private Uri imageUri;
    private Boolean isFollowed;
    private int count;

    private DocumentReference userRef, myRef;
    List<String> followersList, followingList, followingListV2, followersListV2;
    FirestoreRecyclerAdapter<PostImageModel, PostImageViewHolder> adapter;


    private void uploadImages(Uri imageUri) {
        //upload storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Profile Images");

        // Tạo tên file cho ảnh hoặc sử dụng tên duy nhất (ví dụ: UID của người dùng)
        String uniqueImageName = "image_" + System.currentTimeMillis() + ".jpg"; // Tạo tên duy nhất cho ảnh

        // Tham chiếu đến file mới trong thư mục "Profile Images"
        StorageReference imageReference = reference.child(uniqueImageName);
        //  StorageReference reference = FirebaseStorage.getInstance().getReference();
        //   reference.child("Profile Images");
        imageReference.putFile(imageUri)
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
                            Log.e("Storage", task.getException().getMessage().toString());
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

        Log.e("isFollowed", isFollowed + "");
        myRef = FirebaseFirestore.getInstance().collection("Users")
                .document(mUser.getUid());//nguwoifi dung hiện tại
        Log.e("mUser.getUid", mUser.getUid().toString());
        if (IS_SEARCHED_USER) {
            isMyProfile = false;
            // userUid: id cua user minh nhan vao ,(user (ban be))
            userUid = USER_ID;
            myRef = FirebaseFirestore.getInstance().collection("Users")
                    .document(userUid);//nguwoifi dung hiện tại
            loadData();
        } else {
            isMyProfile = true;
            userUid = mUser.getUid();
        }

        if (isMyProfile) {
            edtProfileBtn.setVisibility(View.VISIBLE);
            followBtn.setVisibility(GONE);
            countLayout.setVisibility(View.VISIBLE);
        } else {
            edtProfileBtn.setVisibility(GONE);
            followBtn.setVisibility(View.VISIBLE);
            //        countLayout.setVisibility(GONE);
        }
        userRef = FirebaseFirestore.getInstance().collection("Users")
                .document(userUid);// truy suất đến document của user (ban)


        loadBasicData();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadPostImages();
        recyclerView.setAdapter(adapter);


        clickListener();

    }

    private void loadData() {
        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("ERORR", error.getMessage());
                    return;
                }
                if (value == null || !value.exists()) {
                    return;
                }
                followingListV2 = (List<String>) value.get("following"); // danh sách người đang theo dõi

            }
        });
    }

    private void clickListener() {

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowed) {
                    // followersList: ds nguười theo dỗi
                    followersList.remove(mUser.getUid());// opposite user
                    // da follow: gio muon unfollwer
                    //   Toast.makeText("followersList", "", Toast.LENGTH_SHORT).show();
                    Log.e("followersList", followersList.size() + "");
                    followingListV2.remove(userUid); //us
                    Log.e("followersListV2", followersListV2.size() + "");
                    Map<String, Object> map = new HashMap<>();
                    map.put("followers", followersList);

                    final  Map<String, Object> map2 = new HashMap<>();
                    map2.put("following", followingListV2);

                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                followBtn.setText(R.string.follow);
                                myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                                            Log.e("Unf", "Unf");
                                        } else {
                                            Toast.makeText(getContext(), "UNFollow error", Toast.LENGTH_SHORT).show();

                                            Log.e("Error", task.getException().getMessage().toString());
                                        }
                                    }
                                });
                            } else {
                                Log.e("Update following error", "" + task.getException().getMessage());
                            }
                        }
                    });


                } else {
                    followersList.add(mUser.getUid());// opposite user

                    followingListV2.add(userUid); //us
                    Log.e("followingListV2", "" + followingListV2.size());
                    Map<String, Object> map = new HashMap<>();
                    map.put("followers", followersList);

                    final   Map<String, Object> map2 = new HashMap<>();
                    map2.put("following", followingListV2);


                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                followBtn.setText(R.string.unfollow);
                                myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Un" +
                                                    "Followed", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.e("Error", task.getException().getMessage().toString());

                                        }
                                    }
                                });
                            } else {
                                Log.e("Update following error", "" + task.getException().getMessage());
                            }
                        }
                    });


                }
            }
        });
        //edit profile image
        edtProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnEditProfileImage();
            }
        });


    }

    private void clickOnEditProfileImage() {
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Toast.makeText(getContext(), "" + imageUri.toString(), Toast.LENGTH_SHORT).show();

                        // load anh len image profile
                        Glide.with(requireContext()).
                                load(imageUri).
                                into(profileImage);

                        // upload imageUrl firebaseStorage
                        uploadImages(imageUri);

                    }
                }
            }
    );


    private void loadPostImages() {

//            DocumentReference reference = FirebaseFirestore.getInstance()
//                    .collection("Users").document(mUser.getUid());
        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("Users").document(userUid);

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

                count = getItemCount();
                postCountTv.setText("" + count);
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

            postImageView = itemView.findViewById(R.id.imageView);

        }
    }

    private void loadBasicData() {

        //        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
        //                .document(userUid);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    String name = value.getString("name");
                    String status = value.getString("status");


                    nameTv.setText(name);
                    toolbarNameTv.setText(name);
                    statusTv.setText(status);


                    followersList = (List<String>) value.get("followers");
                    followingList = (List<String>) value.get("following");

                    Log.e("followersList", ""+followersList.size() );
                    Log.e("followingList", ""+followingList.size() );


                    followersCountTv.setText("" + followersList.size());
                    followingCountTv.setText("" + followingList.size());
                    //   postCountTv.setText(("" + HomeFragment.LIST_SIZE));
                    // Log.e("SIZE", "" +HomeFragment.LIST_SIZE );


                    String profileURL = value.getString("profileImage");
                    Glide.with(requireContext())
                            .load(profileURL)
                            .placeholder(R.drawable.ic_person)
                            .timeout(6500)
                            .into(profileImage);


                    if (followersList.contains(mUser.getUid())) {
                        //followingList.contains(mUser.getUid())
                        // kiểm tra xem người dùng hiện tại đã theo dỗi user này chưa
                        followBtn.setText(R.string.unfollow);
                        isFollowed = true;
                    } else {
                        isFollowed = false;
                        followBtn.setText(R.string.follow);
                    }
                } else {
                    Log.e("Error", error.toString());
                }
            }
        });

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


        followersList = new ArrayList<>();
        followingList = new ArrayList<>();
        followingListV2 = new ArrayList<>();
        followersListV2 = new ArrayList<>();


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