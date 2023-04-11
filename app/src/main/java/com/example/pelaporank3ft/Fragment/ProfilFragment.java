package com.example.pelaporank3ft.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pelaporank3ft.Activity.Auth.LoginActivity;
import com.example.pelaporank3ft.Activity.Auth.UbahPassword;
import com.example.pelaporank3ft.Activity.EditProfil;
import com.example.pelaporank3ft.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment {

    private CircleImageView imgUser;
    private DocumentReference dbReference;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private TextView namaPengguna, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);

        imgUser = rootView.findViewById(R.id.foto_profil);
        namaPengguna = rootView.findViewById(R.id.txtnamapengguna_fragmentakun);
        email = rootView.findViewById(R.id.txtemail_fragmentakun);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        dbReference = mFirestore.collection("users").document(userId);

        dbReference.addSnapshotListener((value, profil) -> {
            if(value != null && value.exists()){
                String imgProfile = value.getString("img_user");
                if (imgProfile != null){
                    Glide.with(imgUser.getContext().getApplicationContext())
                            .load(value.getString("img_user"))
                            .into(imgUser);
                } else  {
                    Glide.with(imgUser.getContext().getApplicationContext())
                            .load((R.drawable.avatar))
                            .into(imgUser);
                }
                namaPengguna.setText(value.getString("name_user"));
                email.setText(value.getString("email_user"));
            }
            namaPengguna.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);

            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater1 = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_img = inflater1.inflate(R.layout.dialog_img_full,null);
                    builder.setView(view_img);
                    ImageView imgFull = (ImageView) view_img.findViewById(R.id.imgFull);
                    String imgProfile = value.getString("img_user");
                    if (imgProfile != null){
                        Glide.with(imgFull.getContext())
                                .load(value.getString("img_user"))
                                .into(imgFull);
                    } else  {
                        Glide.with(imgFull.getContext())
                                .load((R.drawable.avatar))
                                .into(imgFull);
                    }

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    ImageButton btnCloseImg = view_img.findViewById(R.id.btn_close_img);
                    btnCloseImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        });

        ProgressDialog progressLogout = new ProgressDialog(getActivity());
        progressLogout.setMessage("Mohon Tunggu Sebentar");
        progressLogout.setCancelable(false);
        progressLogout.setIndeterminate(true);

        //Tombol Edit Profil
        MaterialButton goeditprofil = rootView.findViewById(R.id.btn_edit_profil);
        goeditprofil.setOnClickListener(view -> {
            Intent go_edit_profil = new Intent(getActivity(), EditProfil.class);
            startActivity(go_edit_profil);
        });

        //Tombol Ubah Password
        MaterialButton goubahpw = rootView.findViewById(R.id.btn_ubah_password);
        goubahpw.setOnClickListener(view1 -> {
            Intent go_ubah_pw = new Intent(getActivity(), UbahPassword.class);
            startActivity(go_ubah_pw);
        });

        //Tombol Logout
        MaterialButton gologout = rootView.findViewById(R.id.btn_sign_out);
        gologout.setOnClickListener(view2 ->{
            DialogInterface.OnClickListener dialogLogout = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mAuth.signOut();
                            Intent go_login_ = new Intent(getActivity(), LoginActivity.class);
                            startActivity(go_login_);
                            requireActivity().finish();
                            Toast.makeText(getActivity(), "Logout Berhasil", Toast.LENGTH_SHORT).show();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builderLogout = new AlertDialog.Builder(requireActivity());
            builderLogout.setMessage("Logout?").setPositiveButton("Ya", dialogLogout)
                    .setNegativeButton("Tidak", dialogLogout).show();
        });

        return rootView;
    }
}