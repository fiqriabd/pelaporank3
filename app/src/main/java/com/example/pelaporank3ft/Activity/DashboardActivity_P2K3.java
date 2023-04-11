package com.example.pelaporank3ft.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pelaporank3ft.Fragment.DaftarLaporan;
import com.example.pelaporank3ft.Fragment.HomeFragment;
import com.example.pelaporank3ft.Fragment.NotifikasiFragment;
import com.example.pelaporank3ft.Fragment.ProfilFragment;
import com.example.pelaporank3ft.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class DashboardActivity_P2K3 extends AppCompatActivity implements NotifikasiFragment.TotalDataListener{

    private ActionBar titleBar;
    private BottomNavigationView navigationView;

    private Query mQuery_Insiden;
    private Query mQuery_PB;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private int jumlahinsiden = 0, jumlahpb = 0, jumlahdatas = 0;
    private int jumlahData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_p2k3);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        mQuery_Insiden = mFirestore.collection("laporInsidens")
                .whereEqualTo("status_deleted_insiden", false)
                .whereEqualTo("status_laporan_insiden", "Pending");

        mQuery_PB = mFirestore.collection("potensiBahayas")
                .whereEqualTo("status_deleted_pb", false)
                .whereEqualTo("status_laporan_pb", "Pending");

        initView();
    }

    private void initView() {
        if (mQuery_Insiden == null){
            Log.w(TAG, "Tidak ada data");
        }

        if (mQuery_PB == null) {
            Log.w(TAG, "Tidak ada data");
        }

        mQuery_Insiden.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }
                jumlahinsiden = value.size();
                updateTotalData(jumlahdatas);

                mQuery_PB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        jumlahpb = value.size();
                        jumlahdatas = jumlahinsiden + jumlahpb;
                        updateTotalData(jumlahdatas);
                    }
                });
            }

        });

        titleBar = getSupportActionBar();
        openFragment(new HomeFragment());
        titleBar.setTitle("Pelaporan K3 FT UNDIP");

        navigationView = findViewById(R.id.bottom_nav_p2k3);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.nav_home_p2k3):
                        updateTotalData(jumlahdatas);
                        openFragment(new HomeFragment());
                        titleBar.setTitle("Pelaporan K3 FT UNDIP");
                        return true;
                    case (R.id.nav_daftar_laporan_p2k3):
                        updateTotalData(jumlahdatas);
                        openFragment(new DaftarLaporan());
                        titleBar.setTitle("Laporan");
                        return true;
                    case (R.id.nav_notifikasi_p2k3):
                        onDataTotalUpdated(jumlahData);
                        openFragment(new NotifikasiFragment());
                        titleBar.setTitle("Notifikasi");
                        return true;
                    case (R.id.nav_profil_p2k3):
                        updateTotalData(jumlahdatas);
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
        FT.replace(R.id.frame_layout_p2k3, FRG);
        FT.commit();
    }

    public void updateTotalData(int jumlahdatas) {
        this.jumlahdatas = jumlahdatas;
        if (jumlahdatas > 0){
            navigationView.getOrCreateBadge(R.id.nav_notifikasi_p2k3).setNumber(jumlahdatas);
        } else {
            navigationView.removeBadge(0);
        }

        Log.d(TAG, "TOTAL DATETETENG TENG TENG: "+ jumlahdatas);
    }

    public void onDataTotalUpdated(int jumlahData){
        this.jumlahData = jumlahData;
        if (jumlahData > 0){
            navigationView.getOrCreateBadge(R.id.nav_notifikasi_p2k3).setNumber(jumlahData);
        } else {
            navigationView.removeBadge(0);
        }

        Log.d(TAG, "TOTAL DATETETEN: "+ jumlahData);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}