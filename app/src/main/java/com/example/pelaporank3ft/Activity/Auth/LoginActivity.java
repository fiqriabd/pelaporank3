package com.example.pelaporank3ft.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pelaporank3ft.Activity.DashboardActivity;
import com.example.pelaporank3ft.Activity.DashboardActivity_P2K3;
import com.example.pelaporank3ft.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {

    private DocumentReference dbReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private MaterialButton btnSignIn;
    private EditText etEmailLogin, etPasswordLogin;
    private TextView tvSignUpHere;
    private String userId, tipeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initView();
    }

    private void initView() {
        etEmailLogin = findViewById(R.id.et_email_login);
        etPasswordLogin = findViewById(R.id.et_password_login);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvSignUpHere = findViewById(R.id.txt_sign_up_here);

        ProgressDialog progressLogin = new ProgressDialog(LoginActivity.this);
        progressLogin.setMessage("Mohon Tunggu Sebentar...");
        progressLogin.setCancelable(false);
        progressLogin.setIndeterminate(true);

        btnSignIn.setOnClickListener(view -> {

            String email = etEmailLogin.getText().toString().trim();
            String password = etPasswordLogin.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmailLogin.setError("Email tidak boleh kosong");
                return;
            }else if (TextUtils.isEmpty(password)) {
                etPasswordLogin.setError("Password tidak boleh kosong");
                return;
            }else if (password.length() < 8) {
                etPasswordLogin.setError("Password minimal 8 karakter");
                return;
            }

            progressLogin.show();
            //autentikasi User
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    userId = mAuth.getUid();
                    mFirestore = FirebaseFirestore.getInstance();
                    dbReference = mFirestore.collection("users").document(userId);
                    dbReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot value) {
                            if (value != null && value.exists()){
                                tipeUser = value.getString("tipe_user");
                                if (tipeUser.equals("p2k3")){
                                    progressLogin.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashboardActivity_P2K3.class));
                                    finish();
                                } else if (tipeUser.equals("user")) {
                                    progressLogin.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    finish();
                                }
                            } else {
                                progressLogin.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Gagal",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressLogin.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Gagal",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    progressLogin.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Gagal",Toast.LENGTH_SHORT).show();
                }
            }
            );
        });

        tvSignUpHere.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}