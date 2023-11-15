package com.example.socialmediasmall.fragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                }
                            });
                            uploadUser(user, name, email);
                        } else {
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
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", " ");
        map.put("uid", user.getUid());
        map.put("following", 0);
        map.put("followers", 0);
        map.put("status", "");

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