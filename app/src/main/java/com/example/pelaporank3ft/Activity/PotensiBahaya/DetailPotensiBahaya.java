package com.example.pelaporank3ft.Activity.PotensiBahaya;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pelaporank3ft.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailPotensiBahaya extends AppCompatActivity {

    public static final String DETAIL_KODE_POTENSI_BAHAYA = "kode_potensi_bahaya";
    private DocumentReference mPBRef;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private ImageView imgFotoTandaPengenal, imgFotoPB;
    private TextView tvStatusLaporan, tvKodePB, tvTglLapor, tvNamaPelapor, tvEmailPelapor, tvNipNim,
            tvNoTelpPelapor, tvKategoriPelapor, tvInstitusi, tvTujuan, tvUCAkademika, tvLokasiPB,
            tvPotensiBahaya, tvDeskripsiPB, tvResikoPB, tvUsulanPerbaikan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_potensi_bahaya);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        String kodePotensiBahaya = getIntent().getExtras().getString(DETAIL_KODE_POTENSI_BAHAYA);
        mPBRef = mFirestore.collection("potensiBahayas").document(kodePotensiBahaya);

        initView();
        getDataPB();
    }

    private void initView() {
        tvStatusLaporan = findViewById(R.id.tv_detail_status_pb);
        tvKodePB = findViewById(R.id.tv_detail_kode_pb);
        tvTglLapor = findViewById(R.id.tv_detail_tanggal_lapor_pb);
        imgFotoTandaPengenal = findViewById(R.id.img_detail_foto_tp_pb);
        tvNamaPelapor = findViewById(R.id.tv_lpb_detail_nama_pelapor);
        tvEmailPelapor = findViewById(R.id.tv_detail_email_pelapor_pb);
        tvNipNim = findViewById(R.id.tv_detail_nip_nim_pelapor_pb);
        tvNoTelpPelapor = findViewById(R.id.tv_lpb_detail_notelepon_pelapor);
        tvKategoriPelapor = findViewById(R.id.tv_lpb_detail_kategori_pelapor);
        tvInstitusi = findViewById(R.id.tv_lpb_detail_institusi);
        tvTujuan = findViewById(R.id.tv_lpb_detail_tujuan);
        tvUCAkademika = findViewById(R.id.tv_lpb_detail_unit_ca);
        tvLokasiPB = findViewById(R.id.tv_lpb_detail_lokasi_pb);
        tvPotensiBahaya = findViewById(R.id.tv_lpb_detail_potensi_bahaya);
        tvDeskripsiPB = findViewById(R.id.tv_lpb_detail_deskripsi_pb);
        tvResikoPB = findViewById(R.id.tv_lpb_detail_resiko_bahaya);
        tvUsulanPerbaikan = findViewById(R.id.tv_lpb_detail_usulan_perbaikan);
        imgFotoPB = findViewById(R.id.img_detail_foto_kejadian_pb);
    }

    private void getDataPB() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        AlertDialog alertDialog = new AlertDialog.Builder(DetailPotensiBahaya.this).create();
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
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Log.d(TAG, "Dokumen tersedia!");
                        tvStatusLaporan.setText(document.getString("status_laporan_pb"));
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

                        imgFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailPotensiBahaya.this);
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailPotensiBahaya.this);
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
                        Log.d(TAG, "Gagal menampilkan data");
                        progressDialog.dismiss();
                        alertDialog.show();
                    }
                } else {
                    Log.d(TAG, "Gagal menampilkan data: ", task.getException());
                    progressDialog.dismiss();
                    alertDialog.show();
                }
            }
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