package com.example.pelaporank3ft.Activity.PotensiBahaya;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pelaporank3ft.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InvestigasiPotensiBahaya extends AppCompatActivity {

    public static final String DETAIL_EDIT_STATUS_PB = "kode_pb";
    private DocumentReference mPBRef, mUserRef;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private AutoCompleteTextView statusPB;
    private ImageView imgFotoTandaPengenal, imgFotoPB;
    private MaterialButton btnSimpan;
    private String statusLaporanPB, tglDiupdate, idP2K3, namaP2K3;
    private TextView tvKodePB, tvTglLapor, tvNamaPelapor, tvEmailPelapor, tvNipNim, tvNoTelpPelapor,
            tvKategoriPelapor, tvInstitusi, tvTujuan, tvUCAkademika, tvLokasiPB, tvPotensiBahaya,
            tvDeskripsiPB, tvResikoPB, tvUsulanPerbaikan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigasi_potensi_bahaya);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mUserRef = mFirestore.collection("users").document(userId);
        String kodePB = getIntent().getExtras().getString(DETAIL_EDIT_STATUS_PB);
        mPBRef = mFirestore.collection("potensiBahayas").document(kodePB);

        initView();
        getDataPB();
        getDataPengisi();
        getTanggal();
    }

    private void initView() {
        statusPB = findViewById(R.id.ddstatus_laporan_pb_notifikasi);
        tvKodePB = findViewById(R.id.tv_detail_kode_pb_notifikasi);
        tvTglLapor = findViewById(R.id.tv_detail_tanggal_lapor_pb_notifikasi);
        imgFotoTandaPengenal = findViewById(R.id.img_detail_foto_tp_pb_notifikasi);
        tvNamaPelapor = findViewById(R.id.tv_lpb_detail_nama_pelapor_notifikasi);
        tvEmailPelapor = findViewById(R.id.tv_detail_email_pelapor_pb_notifikasi);
        tvNipNim = findViewById(R.id.tv_detail_nip_nim_pelapor_pb_notifikasi);
        tvNoTelpPelapor = findViewById(R.id.tv_lpb_detail_notelepon_pelapor_notifikasi);
        tvKategoriPelapor = findViewById(R.id.tv_lpb_detail_kategori_pelapor_notifikasi);
        tvInstitusi = findViewById(R.id.tv_lpb_detail_institusi_notifikasi);
        tvTujuan = findViewById(R.id.tv_lpb_detail_tujuan_notifikasi);
        tvUCAkademika = findViewById(R.id.tv_lpb_detail_unit_ca_notifikasi);
        tvLokasiPB = findViewById(R.id.tv_lpb_detail_lokasi_pb_notifikasi);
        tvPotensiBahaya = findViewById(R.id.tv_lpb_detail_potensi_bahaya_notifikasi);
        tvDeskripsiPB = findViewById(R.id.tv_lpb_detail_deskripsi_pb_notifikasi);
        tvResikoPB = findViewById(R.id.tv_lpb_detail_resiko_bahaya_notifikasi);
        tvUsulanPerbaikan = findViewById(R.id.tv_lpb_detail_usulan_perbaikan_notifikasi);
        imgFotoPB = findViewById(R.id.img_detail_foto_kejadian_pb_notifikasi);
        btnSimpan = findViewById(R.id.btn_submit_status_pb_notifikasi);
    }

    private void getDataPB() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        AlertDialog alertDialog = new AlertDialog.Builder(InvestigasiPotensiBahaya.this).create();
        alertDialog.setMessage("Gagal menampilkan data");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });

        mPBRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Dokumen tersedia!");
                        statusPB.setText(document.getString("status_laporan_pb"));
                        tvKodePB.setText(document.getString("kode_potensibahaya"));
                        tvTglLapor.setText(document.getString("tgl_lapor_pb"));
                        Glide.with(imgFotoTandaPengenal.getContext())
                                .load(document.getString("tanda_pengenal_pb"))
                                .into(imgFotoTandaPengenal);
                        tvNamaPelapor.setText(document.getString("nama_pelapor_pb"));
                        tvEmailPelapor.setText(document.getString("email_pelapor_pb"));
                        tvNipNim.setText(document.getString("nip_nim_pb"));
                        tvNoTelpPelapor.setText(document.getString("nomor_telepon_pelapor_pb"));
                        tvKategoriPelapor.setText(document.getString("kategori_pelapor_pb"));
                        tvInstitusi.setText(document.getString("institusi_pb"));
                        tvTujuan.setText(document.getString("tujuan_pb"));
                        tvUCAkademika.setText(document.getString("lokasi_departemen_pb"));
                        tvLokasiPB.setText(document.getString("lokasi_pb"));
                        tvPotensiBahaya.setText(document.getString("potensi_bahaya"));
                        tvDeskripsiPB.setText(document.getString("deskripsi_pb"));
                        tvResikoPB.setText(document.getString("resiko_bahaya_pb"));
                        tvUsulanPerbaikan.setText(document.getString("usulan_perbaikan_pb"));
                        Glide.with(imgFotoPB.getContext())
                                .load(document.getString("gambar_pb"))
                                .into(imgFotoPB);
                        progressDialog.dismiss();

                        //Pilihan pada Status Laporan
                        String[] statuslaporanpb = getResources().getStringArray(R.array.status);
                        ArrayAdapter<String> statuspb = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, statuslaporanpb);
                        statuspb.setNotifyOnChange(true);
                        statusPB.setAdapter(statuspb);
                        statuspb.notifyDataSetChanged();
                        
                        imgFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(InvestigasiPotensiBahaya.this);
                                View view_pb = getLayoutInflater().inflate(R.layout.dialog_img_full, null);
                                builder.setView(view_pb);

                                ImageView imgFull = (ImageView) view_pb.findViewById(R.id.imgFull);
                                Glide.with(imgFull.getContext())
                                        .load(document.getString("tanda_pengenal_pb"))
                                        .into(imgFull);
                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                ImageButton btnCloseImg = view_pb.findViewById(R.id.btn_close_img);
                                btnCloseImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                        imgFotoPB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(InvestigasiPotensiBahaya.this);
                                View view_pb = getLayoutInflater().inflate(R.layout.dialog_img_full, null);
                                builder.setView(view_pb);

                                ImageView imgFull = (ImageView) view_pb.findViewById(R.id.imgFull);
                                Glide.with(imgFull.getContext())
                                        .load(document.getString("gambar_pb"))
                                        .into(imgFull);
                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                ImageButton btnCloseImg = view_pb.findViewById(R.id.btn_close_img);
                                btnCloseImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        Log.d(TAG, "Dokumen tidak tersedia!");
                        progressDialog.dismiss();
                        alertDialog.show();
                    }
                }  else {
                    Log.d(TAG, "Gagal menampilkan data: ", task.getException());
                    progressDialog.dismiss();
                    alertDialog.show();
                }
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatusPB();
            }
        });
    }

    private void getDataPengisi() {
        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Log.d(TAG, "Dokumen Tersedia");
                        idP2K3 = document.getString("id_user");
                        namaP2K3 = document.getString("name_user");
                    } else {
                        Log.d(TAG, "Tidak ada Dokumen");
                    }
                } else {
                    Log.d(TAG, "Gagal mendapatkan data: ", task.getException());
                }
            }
        });
    }

    private void getTanggal() {
        Locale lokal = new Locale("in", "ID");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", lokal);
        Date tanggal = new Date();
        tglDiupdate = format.format(tanggal);
    }

    private void updateStatusPB() {
        statusLaporanPB = statusPB.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        Map<String, Object> updatePBs = new HashMap<>();
        updatePBs.put("status_laporan_pb", statusLaporanPB);
        updatePBs.put("diupdate_pb", tglDiupdate);
        updatePBs.put("id_p2k3_pb", idP2K3);
        updatePBs.put("nama_p2k3_pb", namaP2K3);
        mPBRef.set(updatePBs, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "Berhasil");
                progressDialog.dismiss();
                onBackPressed();
                Toast.makeText(InvestigasiPotensiBahaya.this, "Data Investigasi Potensi Bahaya berhasil disimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Gagal", e);
                progressDialog.dismiss();
                Toast.makeText(InvestigasiPotensiBahaya.this, "Gagal menyimpan Data Investigasi Potensi Bahaya", Toast.LENGTH_SHORT).show();
            }
        });
    }
}