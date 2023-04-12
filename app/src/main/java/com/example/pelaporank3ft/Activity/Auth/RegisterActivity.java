package com.example.pelaporank3ft.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pelaporank3ft.Activity.DashboardActivity;
import com.example.pelaporank3ft.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId, tglDibuat, tglDiupdate, tipeUser;

    private MaterialButton btnSignUp;
    private EditText namaPenggunaRegister, emailRegister, passwordRegister, konfirmasiPasswordRegister;
    private TextView tvSignInHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        getTanggal();
        initView();
    }

    private void getTanggal() {
        Locale lokal = new Locale("in", "ID");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", lokal);
        Date tanggal = new Date();
        tglDibuat = format.format(tanggal);
        tglDiupdate = tglDibuat;
    }

    private void initView() {
        namaPenggunaRegister = findViewById(R.id.et_namapengguna_register);
        emailRegister = findViewById(R.id.et_email_register);
        passwordRegister = findViewById(R.id.et_password_register);
        konfirmasiPasswordRegister = findViewById(R.id.et_konfirmasi_password_register);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvSignInHere = findViewById(R.id.txt_sign_in_here);
        tipeUser = "user";

        ProgressDialog progressRegister = new ProgressDialog(RegisterActivity.this);
        progressRegister.setMessage("Mohon Tunggu Sebentar...");
        progressRegister.setCancelable(false);
        progressRegister.setIndeterminate(true);

        btnSignUp.setOnClickListener(view -> {
            String nama = namaPenggunaRegister.getText().toString().trim();
            String email = emailRegister.getText().toString().trim();
            String password = passwordRegister.getText().toString().trim();
            String konfirmasi_password = konfirmasiPasswordRegister.getText().toString().trim();

            if (TextUtils.isEmpty(nama)) {
                namaPenggunaRegister.setError("Nama Pengguna tidak boleh kosong");
                return;
            }else if (TextUtils.isEmpty(email)) {
                emailRegister.setError("Email tidak boleh kosong");
                return;
            }else if (TextUtils.isEmpty(password)) {
                passwordRegister.setError("Password tidak boleh kosong");
                return;
            }else if (TextUtils.isEmpty(konfirmasi_password)){
                konfirmasiPasswordRegister.setError("Konfirmasi Password tidak boleh kosong");
                return;
            }else if (password.length() < 8) {
                passwordRegister.setError("Password minimal 8 karakter");
                return;
            }else if (!konfirmasi_password.equals(password)) {
                konfirmasiPasswordRegister.setError("Konfirmasi kata sandi tidak cocok");
                return;
            }

            progressRegister.show();
            //Register user ke Firebase
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    progressRegister.dismiss();
                    Toast.makeText(RegisterActivity.this, "User berhasil dibuat", Toast.LENGTH_SHORT).show();
                    userId = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = mFirestore.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("name_user", nama);
                    user.put("email_user", email);
                    user.put("id_user", userId);
                    user.put("img_user", null);
                    user.put("dibuat_user", tglDibuat);
                    user.put("diupdate_user", tglDiupdate);
                    user.put("tipe_user", tipeUser);
                    documentReference.set(user).addOnSuccessListener(regUser -> {
                        Log.d("TAG", "User berhasil dibuat dengan user ID: " + userId);
                    });
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                } else{
                    progressRegister.dismiss();
                    Toast.makeText(RegisterActivity.this, "Gagal membuat user!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignInHere.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}