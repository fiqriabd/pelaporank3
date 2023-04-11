package com.example.pelaporank3ft.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pelaporank3ft.Activity.Insiden.LaporInsiden;
import com.example.pelaporank3ft.Activity.PotensiBahaya.LaporPotensiBahaya;
import com.example.pelaporank3ft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //Lapor Insiden
        LinearLayout laporinsiden = homeView.findViewById(R.id.ll_lapor_insiden_dashboard);
        laporinsiden.setOnClickListener(v -> {
            Intent go_laporinsiden = new Intent(getActivity(), LaporInsiden.class);
            startActivity(go_laporinsiden);
        });

        //Lapor Potensi Bahaya
        LinearLayout laporpotensibahaya = homeView.findViewById(R.id.ll_lapor_potensi_bahaya_dashboard);
        laporpotensibahaya.setOnClickListener(v -> {
            Intent go_laporpotensibahaya = new Intent(getActivity(), LaporPotensiBahaya.class);
            startActivity(go_laporpotensibahaya);
        });

        return homeView;
    }
}