package com.example.pelaporank3ft.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.pelaporank3ft.Fragment.DaftarLaporan;
import com.example.pelaporank3ft.Fragment.HomeFragment;
import com.example.pelaporank3ft.Fragment.ProfilFragment;
import com.example.pelaporank3ft.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private ActionBar titleBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        initView();

    }

    private void initView() {
        titleBar = getSupportActionBar();
        openFragment(new HomeFragment());
        titleBar.setTitle("Pelaporan K3 FT UNDIP");
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.nav_home):
                        openFragment(new HomeFragment());
                        titleBar.setTitle("Pelaporan K3 FT UNDIP");
                        return true;
                    case (R.id.nav_daftar_laporan):
                        openFragment(new DaftarLaporan());
                        titleBar.setTitle("Laporan");
                        return true;
                    case (R.id.nav_profil):
                        openFragment(new ProfilFragment());
                        titleBar.setTitle("Profil");
                        return true;
                }
                return false;
            }
        });
    }

    private void openFragment(Fragment FRG) {
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.frame_layout, FRG);
        FT.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}