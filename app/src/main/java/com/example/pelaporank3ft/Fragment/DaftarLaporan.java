package com.example.pelaporank3ft.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pelaporank3ft.Activity.Insiden.DaftarInsiden;
import com.example.pelaporank3ft.Activity.PotensiBahaya.DaftarPotensiBahaya;
import com.example.pelaporank3ft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DaftarLaporan extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View daftar = inflater.inflate(R.layout.fragment_daftar_laporan, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        //Daftar insiden
        LinearLayout daftarinsiden = daftar.findViewById(R.id.ll_daftar_insiden);
        daftarinsiden.setOnClickListener(v->{
            Intent go_daftarinsiden = new Intent(getActivity(), DaftarInsiden.class);
            startActivity(go_daftarinsiden);
        });

        //Daftar Potensi Bahaya
        LinearLayout daftarpb = daftar.findViewById(R.id.ll_daftar_potensi_bahaya);
        daftarpb.setOnClickListener(v->{
            Intent go_daftarpb = new Intent(getActivity(), DaftarPotensiBahaya.class);
            startActivity(go_daftarpb);
        });

        return daftar;
    }
}