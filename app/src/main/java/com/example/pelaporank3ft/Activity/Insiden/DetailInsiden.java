package com.example.pelaporank3ft.Activity.Insiden;

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

public class DetailInsiden extends AppCompatActivity {

    public static final String DETAIL_KODE_INSIDEN = "kode_insiden";
    private DocumentReference mInsidenRef;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private ImageView imgFotoTandaPengenal, imgFotoKejadianInsiden;
    private TextView tvStatusLaporan, tvKodeInsiden, tvWaktuKejadian, tvLokasi, tvLokasiRinci, tvJenisInsiden,
            tvKronologi, tvPenyebabInsiden, tvNamaPelapor, tvEmailPelapor, tvNoTeleponPelapor,
            tvUnitPelapor, tvNamaKorban, tvEmailKorban, tvNoTeleponKorban, tvUnitKorban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_insiden);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        String kodeInsiden = getIntent().getExtras().getString(DETAIL_KODE_INSIDEN);
        mInsidenRef = mFirestore.collection("laporInsidens").document(kodeInsiden);

        initView();
        getDataInsiden();
    }

    private void initView() {
        tvStatusLaporan = findViewById(R.id.tv_detail_status_insiden);
        tvKodeInsiden = findViewById(R.id.tv_detail_kode_insiden);
        tvWaktuKejadian = findViewById(R.id.tv_detail_waktu_kejadian_insiden);
        tvLokasi = findViewById(R.id.tv_detail_lokasi_kejadian_insiden);
        tvLokasiRinci = findViewById(R.id.tv_detail_lokasi_rinci_insiden);
        tvJenisInsiden = findViewById(R.id.tv_detail_jenis_insiden);
        tvKronologi = findViewById(R.id.tv_detail_kronologi_kejadian_insiden);
        tvPenyebabInsiden = findViewById(R.id.tv_detail_penyebab_insiden);
        imgFotoTandaPengenal = findViewById(R.id.img_detail_foto_tp_insiden);
        tvNamaPelapor = findViewById(R.id.tv_detail_nama_pelapor_insiden);
        tvEmailPelapor = findViewById(R.id.tv_detail_email_pelapor_insiden);
        tvNoTeleponPelapor = findViewById(R.id.tv_detail_notelepon_pelapor_insiden);
        tvUnitPelapor = findViewById(R.id.tv_detail_unit_pelapor_insiden);
        tvNamaKorban = findViewById(R.id.tv_detail_nama_korban_insiden);
        tvEmailKorban = findViewById(R.id.tv_detail_email_korban_insiden);
        tvNoTeleponKorban = findViewById(R.id.tv_detail_notelepon_korban_insiden);
        tvUnitKorban = findViewById(R.id.tv_detail_unit_korban_insiden);
        imgFotoKejadianInsiden = findViewById(R.id.img_detail_foto_kejadian_insiden);
    }

    private void getDataInsiden() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        AlertDialog alertDialog = new AlertDialog.Builder(DetailInsiden.this).create();
        alertDialog.setMessage("Gagal menampilkan data");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });

        mInsidenRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Dokumen tersedia!");
                        tvStatusLaporan.setText(document.getString("status_laporan_insiden"));
                        tvKodeInsiden.setText(document.getString("kode_laporinsiden"));
                        tvWaktuKejadian.setText(document.getString("waktu_kejadian_insiden"));
                        tvLokasi.setText(document.getString("lokasi_departemen_insiden"));
                        tvLokasiRinci.setText(document.getString("lokasi_rinci_insiden"));
                        tvJenisInsiden.setText(document.getString("jenis_insiden"));
                        tvKronologi.setText(document.getString("kronologi_insiden"));
                        tvPenyebabInsiden.setText(document.getString("penyebab_insiden"));
                        Glide.with(imgFotoTandaPengenal.getContext())
                                .load(document.getString("tanda_pengenal_insiden"))
                                .into(imgFotoTandaPengenal);
                        tvNamaPelapor.setText(document.getString("nama_pelapor_insiden"));
                        tvEmailPelapor.setText(document.getString("email_pelapor_insiden"));
                        tvNoTeleponPelapor.setText(document.getString("nomor_telepon_pelapor_insiden"));
                        tvUnitPelapor.setText(document.getString("unit_pelapor_insiden"));
                        tvNamaKorban.setText(document.getString("nama_korban_insiden"));
                        tvEmailKorban.setText(document.getString("email_korban_insiden"));
                        tvNoTeleponKorban.setText(document.getString("nomer_telepon_korban_insiden"));
                        tvUnitKorban.setText(document.getString("unit_korban_insiden"));
                        Glide.with(imgFotoKejadianInsiden.getContext())
                                .load(document.getString("gambar_insiden"))
                                .into(imgFotoKejadianInsiden);
                        progressDialog.dismiss();

                        imgFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailInsiden.this);
                                View view = getLayoutInflater().inflate(R.layout.dialog_img_full, null);
                                builder.setView(view);

                                ImageView imgFull = (ImageView) view.findViewById(R.id.imgFull);
                                Glide.with(imgFull.getContext())
                                        .load(document.getString("tanda_pengenal_insiden"))
                                        .into(imgFull);
                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                ImageButton btnCloseImg = view.findViewById(R.id.btn_close_img);
                                btnCloseImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                        imgFotoKejadianInsiden.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailInsiden.this);
                                View view = getLayoutInflater().inflate(R.layout.dialog_img_full, null);
                                builder.setView(view);

                                ImageView imgFull = (ImageView) view.findViewById(R.id.imgFull);
                                Glide.with(imgFull.getContext())
                                        .load(document.getString("gambar_insiden"))
                                        .into(imgFull);
                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                ImageButton btnCloseImg = view.findViewById(R.id.btn_close_img);
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
                } else {
                    Log.d(TAG, "Gagal menampilkan data: ", task.getException());
                    progressDialog.dismiss();
                    alertDialog.show();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
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