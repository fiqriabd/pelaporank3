package com.example.pelaporank3ft.Activity.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pelaporank3ft.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UbahPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private EditText etPasswordLama, etPasswordBaru, etKonfirmasiPasswordBaru;
    private MaterialButton btnUbahPasswordActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        initView();
    }

    private void initView() {
        etPasswordLama = findViewById(R.id.et_password_lama);
        etPasswordBaru = findViewById(R.id.et_password_baru);
        etKonfirmasiPasswordBaru = findViewById(R.id.et_konfirmasi_password_baru);
        btnUbahPasswordActivity = findViewById(R.id.btn_ubah_password_activity);

        ProgressDialog progressUbahPw = new ProgressDialog(UbahPassword.this);
        progressUbahPw.setMessage("Mohon Tunggu Sebentar");
        progressUbahPw.setCancelable(false);
        progressUbahPw.setIndeterminate(true);

        btnUbahPasswordActivity.setOnClickListener(view -> {
            String oldPassword = etPasswordLama.getText().toString().trim();
            String newPassword = etPasswordBaru.getText().toString().trim();
            String confirmNewPassword = etKonfirmasiPasswordBaru.getText().toString().trim();

            if(TextUtils.isEmpty(oldPassword)){
                etPasswordLama.setError("Kolom tidak boleh kosong");
                etPasswordLama.requestFocus();
                return;
            }else if(TextUtils.isEmpty(newPassword)){
                etPasswordBaru.setError("Kolom tidak boleh kosong");
                return;
            }else if(TextUtils.isEmpty(confirmNewPassword)){
                etKonfirmasiPasswordBaru.setError("Kolom tidak boleh kosong");
                return;
            }else if(oldPassword.length() < 8){
                etPasswordLama.setError("Password minimal 8 karakter");
                return;
            }else if(newPassword.length() < 8) {
                etPasswordBaru.setError("Password minimal 8 karakter");
                return;
            }else if(newPassword.equals(oldPassword)){
                etPasswordBaru.setError("Masukkan kata sandi baru");
                return;
            }else if(!confirmNewPassword.equals(newPassword)){
                etKonfirmasiPasswordBaru.setError("Konfirmasi kata sandi tidak cocok");
                return;
            }

            progressUbahPw.show();
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), oldPassword);
            mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    mUser.updatePassword(confirmNewPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            progressUbahPw.dismiss();
                            Toast.makeText(UbahPassword.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            progressUbahPw.dismiss();
                            Toast.makeText(UbahPassword.this, "Password gagal diubah", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    progressUbahPw.dismiss();
                    Toast.makeText(UbahPassword.this, "Gagal autentikasi", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}