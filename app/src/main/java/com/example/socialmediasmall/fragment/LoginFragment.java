package com.example.socialmediasmall.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediasmall.ForgotPasswordActivity;
import com.example.socialmediasmall.MainActivity;
import com.example.socialmediasmall.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment {


    private EditText emailEdt, passwordEdt;
    private TextView signUpTv, forgotPasswordTv;
    private Button loginBtn, googleSignUpBtn, facebookSignUpBtn;
    private ProgressBar progressBar;
    private CheckBox rememberCheckBox;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private GoogleSignInClient signInClient;
    private FirebaseUser mUser;
    private static final int RC_SIGN_IN = 9001;

    private CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        // gui yeu cau xin phep login gooogle
        createRequestLoginGoolge();
        clickListener();

    }

    private void createRequestLoginGoolge() {
//        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
//                GoogleSignInOptions.DEFAULT_SIGN_IN
//        ).requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail().build();
//
//        signInClient = GoogleSignIn.getClient(requireContext(), signInOptions);
        // Khởi tạo và cấu hình GoogleSignInOptions

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();

        // Khởi tạo GoogleSignInClient
        //   signInClient = GoogleSignIn.getClient(requireActivity(), gso);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(requireContext(), signInOptions);




    }

    private void clickListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                clickLogin();
            }
        });

        googleSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGoogleLogin();
            }
        });

        facebookSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFacebookLogin();
            }
        });
        
        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickForgotPassword();
            }
        });
    }

    private void clickForgotPassword() {
        startActivity(new Intent(getContext().getApplicationContext(), ForgotPasswordActivity.class));
    }

    private void clickFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.e("LOI FACBOOK", e.toString());

            }
        });
    }

    //facebook
    //  @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            progressDialog.dismiss();
                            Toast.makeText(getContext().getApplicationContext(), "Facebook Login Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            userFacebookProfile(mUser);
                            getActivity().finish();
                        } else {

                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void userFacebookProfile(FirebaseUser mUser) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", mUser.getDisplayName());
        map.put("email", mUser.getEmail());
        map.put("profileImage", String.valueOf(mUser.getPhotoUrl()));
        map.put("uid", mUser.getUid());

        FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid())
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


    private void clickGoogleLogin() {
        progressDialog.show();
        Intent signInIntent = signInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
           activityResultLauncher.launch(signInIntent);
    }


    //activity result laucher : nhan ket qua tra ve
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            auth(account.getIdToken());
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập thành công
                GoogleSignInAccount account = task.getResult(ApiException.class);
                auth(account.getIdToken());
                // Thực hiện xử lý sau khi đăng nhập thành công ở đây
                Snackbar.make(requireView(), "Đăng nhập thành công: " + account.getDisplayName(), Snackbar.LENGTH_LONG).show();
            } catch (ApiException e) {
                Log.e("TAG", e.toString() );
                // Xử lý lỗi đăng nhập thất bại
                Snackbar.make(requireView(), "Đăng nhập thất bại", Snackbar.LENGTH_LONG).show();
            }
        }

    }


    private void auth(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        // authcredential: đại diện thông tin xác thực từ người dùng
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //      progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Touch3", Toast.LENGTH_SHORT).show();

                    //Toast.makeText(getContext().getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    mUser = mAuth.getCurrentUser();
                    Log.e("User", mUser.getDisplayName() );
                //    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));

                    userProfile(mUser);
                } else {
                    // progressDialog.dismiss();

                    Toast.makeText(getContext().getApplicationContext(), "Login Fail", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void userProfile(FirebaseUser mUser) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", mUser.getDisplayName());
        map.put("email", mUser.getEmail());
        map.put("profileImage", String.valueOf(mUser.getPhotoUrl()));
        map.put("uid", mUser.getUid());

        FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(), ""+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    private void clickLogin() {
        String email = emailEdt.getText().toString();
        String pass = passwordEdt.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (!user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            }

                            sendUserToMainActivity();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e("LOGIN", task.getException().toString() );
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {

        startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
        getActivity().finish();
    }

    private void init(View view) {
        emailEdt = view.findViewById(R.id.emailLIEdt);
        passwordEdt = view.findViewById(R.id.passwordLIEdt);
        rememberCheckBox = view.findViewById(R.id.checkboxRememberPass);
        signUpTv = view.findViewById(R.id.tvSignUp);
        forgotPasswordTv = view.findViewById(R.id.tvForgotPassword);
        facebookSignUpBtn = view.findViewById(R.id.facebookLoginBtn);
        googleSignUpBtn = view.findViewById(R.id.googleLoginBtn);
        loginBtn = view.findViewById(R.id.loginBtn);

        progressBar = view.findViewById(R.id.progress_login);
        progressDialog = new ProgressDialog(getContext());


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //facebook
        callbackManager = CallbackManager.Factory.create();
    }
}