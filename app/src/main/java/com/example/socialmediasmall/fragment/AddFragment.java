package com.example.socialmediasmall.fragment;

import static android.app.Activity.RESULT_OK;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialmediasmall.MainActivity;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.adapter.GalleryAdapter;
import com.example.socialmediasmall.interfaceListener.ISendImage;
import com.example.socialmediasmall.model.GalleryImages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddFragment extends Fragment implements ISendImage {


    private EditText descEt;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ImageButton backBtn, nextBtn;
    private GalleryAdapter adapter;
    private List<GalleryImages> listImages;

    private static final int REQUEST_CODE_GALLERY = 123;

    private Uri imageUri;
    private String imageUrl;
    private FirebaseUser mUser;
    private final ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    // Xử lý lỗi nếu có
                } else if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                        Uri image = cropResult.getUri();

                        // Hiển thị hình ảnh đã cắt lên ImageView bằng Glide
                        Glide.with(requireContext())
                                .load(image)
                                .into(imageView);

                        // Hiển thị ImageView và Button khi có hình ảnh
                        imageView.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
    );


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
        clickListener();
    }

    private void clickListener() {
        adapter.sendImage(this);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnBtnNext();
            }
        });
    }

    private void clickOnBtnNext() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Post Images/"
                + System.currentTimeMillis());
        reference.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    Log.e("imageUrl- Storage", imageUrl.toString());
                                    upLoadData(uri.toString());
                                }
                            });
                        }
                    }
                });
    }

    private void upLoadData(String imageUrl) {

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Users").document(mUser.getUid())
                .collection("Post Images");

        String id = collectionReference.document().getId();// layas id cua "Post Images"

        String description = descEt.getText().toString().trim();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description", description);
        map.put("imageUrl", imageUrl);
        map.put("timestamp", FieldValue.serverTimestamp());

        map.put("profileImage", String.valueOf(mUser.getPhotoUrl()));
        map.put("likeCount", 0);
        map.put("username", mUser.getDisplayName());


        // gui data len firebasefirestore
        collectionReference.document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println();
                            Toast.makeText(getContext(), "ADD Fragment Data len firestore ss", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void init(View view) {
        descEt = view.findViewById(R.id.descriptionET);
        imageView = view.findViewById(R.id.imageView);
        recyclerView = view.findViewById(R.id.recyclerView);
        backBtn = view.findViewById(R.id.backBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }


        // được sử dụng để đảm bảo rằng việc cập nhật giao diện người dùng sẽ xảy ra trên luồng UI.



    // Trong phương thức bạn muốn mở Gallery (ví dụ: trong một sự kiện nhấn nút)


    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();



        if (mainActivity.isGalleryOpened == false) {
            openGallery();
            mainActivity.isGalleryOpened = true;
        }
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    // Xử lý kết quả sau khi người dùng đã chọn hình ảnh từ Gallery
    @Override
    public  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();

            // Sử dụng selectedImageUri cho mục đích của bạn, ví dụ: hiển thị hình ảnh trong ImageView
            if (imageUri != null) {
                // Hiển thị hình ảnh đã chọn trong ImageView (ví dụ: imageView.setImageURI(selectedImageUri);)
                Glide.with(requireContext())
                        .load(imageUri)
                        .into(imageView);

                // Hiển thị ImageView và Button khi có hình ảnh
                imageView.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                // Gọi các hàm xử lý hình ảnh hoặc hiển thị nó trong ứng dụng của bạn ở đây
            }
        }
    }

    @Override
    public void onSend(Uri picUri) {
        imageUri = picUri;
        Log.e("imageUri in Fun OnSend", imageUri.toString());

//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMultiTouchEnabled(true)
//                .start(requireContext(), cropActivityResultLauncher);
        // Tạo intent để khởi chạy hoạt động CropImage
   //   upLoadData(imageUri);

    }


}