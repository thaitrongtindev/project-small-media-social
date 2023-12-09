package com.example.socialmediasmall.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediasmall.R;
import com.example.socialmediasmall.ReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateAccountFragment extends Fragment {


    private EditText nameEdt, emailEdt, passwordEdt, confirmPasswordEdt;
    private TextView loginTv;
    private Button signUpBtn;
    private FrameLayout frameLayout;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public CreateAccountFragment() {
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
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickListener();
    }

    private void clickListener() {
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReplacerActivity) getActivity()).setFragment(new LoginFragment());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEdt.getText().toString().trim();
                String email = emailEdt.getText().toString().trim();
                String password = passwordEdt.getText().toString().trim();
                String confirmPassword = passwordEdt.getText().toString().trim();

                if (name.isEmpty() || name.equals(" ")) {
                    nameEdt.setError("Please input valid name");
                    return;
                }
//                if (!isValidEmail(EMAIL_PATTERN) || email.isEmpty()) {
//                    emailEdt.setError("Please input valid email");
//                    return;
//                }

                if (password.length() < 6 || password.isEmpty()) {

                        passwordEdt.setError("Please input valid password");
                        return;
                    }


                if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                    confirmPasswordEdt.setError("Password no match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                createAccount(email, password, name);

            }
        });
    }

    private void createAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                          //  Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String image = "data:image/jpeg;base64,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";

                            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                            builder.setDisplayName(name);
                            builder.setPhotoUri(Uri.parse(image));
                            user.updateProfile(builder.build());

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                }
                            });
                            uploadUser(user, name, email);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getContext(), "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("LOI", errorMessage );
                        }
                    }
                });
    }

    private void uploadUser(FirebaseUser user, String name, String email) {
        List<String> follower = new ArrayList<>();
        List<String> following = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", " ");
        map.put("uid", user.getUid());
        map.put("status", "");
        map.put("search", name.toLowerCase());

        map.put("following", following);
        map.put("followers", follower);

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Tạo một thể hiện của LoginFragment
                            LoginFragment loginFragment = new LoginFragment();

// Sử dụng FragmentManager và FragmentTransaction để thêm LoginFragment vào hoạt động hiện tại
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(frameLayout.getId(), loginFragment); // R.id.fragment_container là ID của một ViewGroup trong layout XML của hoạt động chứa Fragment
                            fragmentTransaction.addToBackStack(null); // Tùy chọn: Để thêm LoginFragment vào ngăn xếp lưu trữ Fragment
                            fragmentTransaction.commit();

                        } else {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void init(View view) {
        nameEdt = view.findViewById(R.id.nameEdt);
        emailEdt = view.findViewById(R.id.emailEdt);
        passwordEdt = view.findViewById(R.id.nameEdt);
        confirmPasswordEdt = view.findViewById(R.id.confirmPasswordEdt);
        loginTv = view.findViewById(R.id.textBackToLogin);
        signUpBtn = view.findViewById(R.id.signUpBtn);
        progressBar = view.findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

        frameLayout = view.findViewById(R.id.framelayout);
    }
}