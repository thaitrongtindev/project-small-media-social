package com.example.socialmediasmall.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

        String id  = collectionReference.document().getId();// layas id cua "Post Images"

        String description = descEt.getText().toString().trim();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description",description);
        map.put("imageUrl", imageUrl);
        map.put("timestamp", FieldValue.serverTimestamp());

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

    @Override
    public void onResume() {
        super.onResume();
        // được sử dụng để đảm bảo rằng việc cập nhật giao diện người dùng sẽ xảy ra trên luồng UI.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dexter.withContext(getContext())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    // để truy cập vào thư mục lưu trữ bên ngoài ứng dụng
                                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download");

                                    if (file.exists()) {
                                        File[] files = file.listFiles();
                                        for (File file1 : files) {
                                            if (file1.getAbsolutePath().endsWith(".jpg") || file1.getAbsolutePath().endsWith(".png")) {
                                                listImages.add(new GalleryImages(Uri.fromFile(file1)));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        });
            }
        });
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
        Intent intent = CropImage.activity(imageUri)
                .getIntent(requireContext());

        // Khởi chạy hoạt động CropImage bằng startActivityForResult và Intent đã tạo
        cropActivityResultLauncher.launch(intent);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (requestCode == RESULT_OK) {
//                Uri image  = result.getUri();
//                Glide.with(getContext())
//                        .load(image)
//                        .into(imageView);
//                imageView.setVisibility(View.VISIBLE);
//                nextBtn.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Uri resultUri = CropImage.getActivityResult(result.getData()).getUri();
//                        Glide.with(getContext())
//                                .load(resultUri)
//                                .into(imageView);
//                        imageView.setVisibility(View.VISIBLE);
//                        nextBtn.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//    );
}