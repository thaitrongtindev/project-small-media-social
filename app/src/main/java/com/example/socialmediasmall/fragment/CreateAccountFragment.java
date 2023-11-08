package com.example.socialmediasmall.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediasmall.FragmentReplaceActivity;
import com.example.socialmediasmall.MainActivity;
import com.example.socialmediasmall.R;
import com.example.socialmediasmall.fragment.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;




public class CreateAccountFragment extends Fragment {


    private EditText nameEdt, emailEdt, passwordEdt, confirmPasswordEdt;
    private TextView loginTv;
    private Button signUpBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
 //   private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

   // private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

//    public static boolean isValidEmail(String email) {
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

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
                ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEdt.getText().toString().trim();
                String email = emailEdt.getText().toString().trim();
                String password = passwordEdt.getText().toString().trim();
                String confirmPassword = confirmPasswordEdt.getText().toString().trim();


                progressBar.setVisibility(View.VISIBLE);
                createAccount(email, password, name);

            }
        });
    }

    private void createAccount(String email, String password, String name) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(getContext(), "Create account success", Toast.LENGTH_SHORT).show();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Log.e("USEREmail", user.getDisplayName());
//                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            uploadUser(user, name, email);
//                        } else {
//                            Toast.makeText(getContext(), ""+task.getException(),Toast.LENGTH_SHORT).show();
//                            Log.e("Task" , task.getException().toString() );
//                        }
//                    }
//                });
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

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
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
    }
}