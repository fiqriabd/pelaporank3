package com.example.pelaporank3ft.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.pelaporank3ft.Activity.Auth.LoginActivity;
import com.example.pelaporank3ft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private DocumentReference dbReference;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String userId, tipeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        cekSesiUser();
    }

    private void cekSesiUser() {
        Handler handler = new Handler();
        if (mAuth.getCurrentUser() != null){

            userId = mAuth.getCurrentUser().getUid();
            dbReference = mFirestore.collection("users").document(userId);
            dbReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                    if (value != null && value.exists()) {
                        tipeUser = value.getString("tipe_user");
                        if (tipeUser.equals("p2k3")){
                                //jeda 1 detik
                                handler.postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), DashboardActivity_P2K3.class));
                                finish();
                            }, 1000);
                        } else if (tipeUser.equals("user")) {
                                //jeda 1 detik
                                handler.postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                finish();
                            }, 1000);
                        }
                    }
                }
            });
        } else {
            //jeda 1 detik
            handler.postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }, 1000);
        }
    }
}