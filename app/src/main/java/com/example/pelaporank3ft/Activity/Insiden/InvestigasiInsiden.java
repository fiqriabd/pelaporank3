package com.example.pelaporank3ft.Activity.Insiden;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InvestigasiInsiden extends AppCompatActivity {

    public static final String DETAIL_EDIT_STATUS_INSIDEN = "kode_insiden";
    private DocumentReference mInsidenRef, mUserRef;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private String userId;

    private DatePickerDialog tenggatWaktuInsiden;
    private SimpleDateFormat formatTanggalInsiden;

    private AutoCompleteTextView statusInsiden, kategoriInsiden;
    private EditText tvTenggatWaktu, tvTindakan;
    private MaterialButton btnSimpan;
    private ImageView imgFotoTandaPengenal, imgFotoKejadianInsiden;
    private String statusLaporanInsiden, idP2K3, namaP2K3, kategoriIs, waktuTenggat, tindakanYg, tglDiupdate;
    private TextView tvKodeInsiden, tvLokasi, tvWaktuKejadian, tvLokasiRinci, tvJenisInsiden,
            tvKronologi, tvPenyebabInsiden, tvNamaPelapor, tvEmailPelapor, tvNoTeleponPelapor,
            tvUnitPelapor, tvNamaKorban, tvEmailKorban, tvNoTeleponKorban, tvUnitKorban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigasi_insiden);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mUserRef = mFirestore.collection("users").document(userId);
        String kodeInsiden = getIntent().getExtras().getString(DETAIL_EDIT_STATUS_INSIDEN);
        mInsidenRef = mFirestore.collection("laporInsidens").document(kodeInsiden);

        initView();
        getDataInsiden();
        getDataPengisi();
        getTanggal();
    }

    private void initView() {
        statusInsiden = findViewById(R.id.ddstatus_laporan_insiden_notifikasi);
        kategoriInsiden = findViewById(R.id.ddkategori_insiden_notifikasi);
        tvTenggatWaktu = findViewById(R.id.tenggat_waktu_insiden);
        tvTindakan = findViewById(R.id.tindakan_yg_diberikan_insiden);
        tvKodeInsiden = findViewById(R.id.tv_detail_kode_insiden_notifikasi);
        tvWaktuKejadian = findViewById(R.id.tv_detail_waktu_kejadian_insiden_notifikasi);
        tvLokasi = findViewById(R.id.tv_detail_lokasi_kejadian_insiden_notifikasi);
        tvLokasiRinci = findViewById(R.id.tv_detail_lokasi_rinci_insiden_notifikasi);
        tvJenisInsiden = findViewById(R.id.tv_detail_jenis_insiden_notifikasi);
        tvKronologi = findViewById(R.id.tv_detail_kronologi_kejadian_insiden_notifikasi);
        tvPenyebabInsiden = findViewById(R.id.tv_detail_penyebab_insiden_notifikasi);
        imgFotoTandaPengenal = findViewById(R.id.img_detail_foto_tp_insiden_notifikasi);
        tvNamaPelapor = findViewById(R.id.tv_detail_nama_pelapor_insiden_notifikasi);
        tvEmailPelapor = findViewById(R.id.tv_detail_email_pelapor_insiden_notifikasi);
        tvNoTeleponPelapor = findViewById(R.id.tv_detail_notelepon_pelapor_insiden_notifikasi);
        tvUnitPelapor = findViewById(R.id.tv_detail_unit_pelapor_insiden_notifikasi);
        tvNamaKorban = findViewById(R.id.tv_detail_nama_korban_insiden_notifikasi);
        tvEmailKorban = findViewById(R.id.tv_detail_email_korban_insiden_notifikasi);
        tvNoTeleponKorban = findViewById(R.id.tv_detail_notelepon_korban_insiden_notifikasi);
        tvUnitKorban = findViewById(R.id.tv_detail_unit_korban_insiden_notifikasi);
        imgFotoKejadianInsiden = findViewById(R.id.img_detail_foto_kejadian_insiden_notifikasi);
        btnSimpan = findViewById(R.id.btn_submit_status_insiden_notifikasi);

        //Format Tanggal
        formatTanggalInsiden = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        tvTenggatWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkan_tenggat_waktu();
            }

            private void tampilkan_tenggat_waktu() {
                Calendar calendar_investigasi = Calendar.getInstance();
                tenggatWaktuInsiden = new DatePickerDialog(InvestigasiInsiden.this, (view, year, month, dayOfMonth) -> {
                    Calendar tglInsiden = Calendar.getInstance();
                    tglInsiden.set(year, month, dayOfMonth);
                    tvTenggatWaktu.setText(formatTanggalInsiden.format(tglInsiden.getTime()));
                }, calendar_investigasi.get(Calendar.YEAR), calendar_investigasi.get(Calendar.MONTH), calendar_investigasi.get(Calendar.DAY_OF_MONTH));
                tenggatWaktuInsiden.show();
            }
        });
    }

    private void getDataInsiden() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        AlertDialog alertDialog = new AlertDialog.Builder(InvestigasiInsiden.this).create();
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
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Dokumen tersedia!");
                        statusInsiden.setText(document.getString("status_laporan_insiden"));
                        kategoriInsiden.setText(document.getString("kategori_insiden"));
                        tvTenggatWaktu.setText(document.getString("tenggat_waktu_insiden"));
                        tvTindakan.setText(document.getString("tindakan_insiden"));
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

                        //Pilihan pada Status Laporan
                        String[] statuslaporanins = getResources().getStringArray(R.array.status);
                        ArrayAdapter<String> statusins = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, statuslaporanins);
                        statusins.setNotifyOnChange(true);
                        statusInsiden.setAdapter(statusins);
                        statusins.notifyDataSetChanged();

                        //Pilihan pada Kategori Insiden
                        String[] kategoriinsidens = getResources().getStringArray(R.array.kategoriinsiden);
                        ArrayAdapter<String> kategoris = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, kategoriinsidens);
                        kategoris.setNotifyOnChange(true);
                        kategoriInsiden.setAdapter(kategoris);
                        kategoris.notifyDataSetChanged();

                        imgFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(InvestigasiInsiden.this);
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(InvestigasiInsiden.this);
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

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatusInsiden();
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

    private void updateStatusInsiden() {
        statusLaporanInsiden = statusInsiden.getText().toString().trim();
        kategoriIs = kategoriInsiden.getText().toString().trim();
        waktuTenggat = tvTenggatWaktu.getText().toString().trim();
        tindakanYg = tvTindakan.getText().toString().trim();

        if (kategoriIs.isEmpty()) {
            Toast.makeText(this, "Kategori Insiden tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(waktuTenggat)){
            Toast.makeText(this, "Waktu Tenggat tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(tindakanYg)) {
            Toast.makeText(this, "Tindakan Yang Diberikan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        Map<String, Object> updateInsidens = new HashMap<>();
        updateInsidens.put("status_laporan_insiden", statusLaporanInsiden);
        updateInsidens.put("diupdate_insiden", tglDiupdate);
        updateInsidens.put("kategori_insiden", kategoriIs);
        updateInsidens.put("tenggat_waktu_insiden", waktuTenggat);
        updateInsidens.put("tindakan_insiden", tindakanYg);
        updateInsidens.put("id_p2k3_insiden", idP2K3);
        updateInsidens.put("nama_p2k3_insiden", namaP2K3);
        mInsidenRef.set(updateInsidens, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "Berhasil");
                progressDialog.dismiss();
                onBackPressed();
                Toast.makeText(InvestigasiInsiden.this, "Data Investigasi Insiden berhasil disimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Gagal", e);
                progressDialog.dismiss();
                Toast.makeText(InvestigasiInsiden.this, "Gagal menyimpan Data Investigasi Insiden", Toast.LENGTH_SHORT).show();
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