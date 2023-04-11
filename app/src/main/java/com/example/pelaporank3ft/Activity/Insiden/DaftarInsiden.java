package com.example.pelaporank3ft.Activity.Insiden;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pelaporank3ft.Adapter.DaftarInsidenAdapter;
import com.example.pelaporank3ft.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DaftarInsiden extends AppCompatActivity implements DaftarInsidenAdapter.OnInsidenSelectedListener{

    private DocumentReference mUserIn;
    private DaftarInsidenAdapter mAdapter;
    private FloatingActionButton btnTambahInsiden;
    private ProgressBar pbInsiden;
    private RecyclerView daftarInsidenRec;
    private String userPengisi, userIn, idPengisi, tipePengisi, tglDiupdate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private Query mQuery;

    private AlertDialog dialog;
    private ImageButton btnClose;
    private Button btnDetail, btnEditStatusLaporan, btnHapus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_insiden);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userIn = mAuth.getCurrentUser().getUid();
        userPengisi = mAuth.getUid();
        mQuery = mFirestore.collection("laporInsidens").whereEqualTo("status_deleted_insiden", false);
        mStorage = FirebaseStorage.getInstance();

        initView();
        getTanggal();
    }

    private void initView() {
        btnTambahInsiden = findViewById(R.id.btn_tambah_insiden);
        daftarInsidenRec = findViewById(R.id.recycler_daftar_insiden);
        pbInsiden = findViewById(R.id.pb_insiden);

        pbInsiden.setVisibility(View.VISIBLE);

        if (mQuery == null) {
            Log.w(TAG, "Tidak ada data");
        }

        mAdapter = new DaftarInsidenAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty
                if (getItemCount() == 0) {
                    daftarInsidenRec.setVisibility(View.GONE);
                    pbInsiden.setVisibility(View.GONE);
                    Log.w(TAG, "Jumlah Insiden = 0");
                } else {
                    daftarInsidenRec.setVisibility(View.VISIBLE);
                    pbInsiden.setVisibility(View.GONE);
                    Log.w(TAG, "Menampilkan Daftar Insiden");
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e(TAG, "Eror: cek log untuk info");
            }
        };

        btnTambahInsiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DaftarInsiden.this, LaporInsiden.class));
            }
        });

        daftarInsidenRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        daftarInsidenRec.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void getTanggal() {
        Locale lokal = new Locale("in", "ID");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", lokal);
        Date tanggal = new Date();
        tglDiupdate = format.format(tanggal);
    }

    @Override
    public void onInsidenSelected(DocumentSnapshot insidenModel) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference docPengisi = insidenModel.getReference();
        docPengisi.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dokumen = task.getResult();
                    idPengisi = dokumen.getString("id_user");

                    Log.d(TAG, "UserLogin:  " + userIn);

                    mUserIn = mFirestore.collection("users").document(userIn);
                    mUserIn.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot dokumen1 = task.getResult();
                                tipePengisi = dokumen1.getString("tipe_user");

                                Log.d(TAG, "Tipe User: " + tipePengisi);

                                final AlertDialog.Builder builder = new AlertDialog.Builder(DaftarInsiden.this);
                                View view = getLayoutInflater().inflate(R.layout.dialog_menu_opsi, null);
                                builder.setView(view);
                                btnClose = view.findViewById(R.id.btn_close);
                                btnDetail = view.findViewById(R.id.btn_detail);
                                btnEditStatusLaporan = view.findViewById(R.id.btn_edit_status_laporan);
                                btnHapus = view.findViewById(R.id.btn_hapus);
                                dialog = builder.create();
                                Log.d(TAG, "User Sekarang: "+userIn);

                                btnDetail.setEnabled(userIn.equals(idPengisi)||tipePengisi.equals("p2k3"));
                                if (!Objects.equals(tipePengisi, "p2k3")){
                                    btnEditStatusLaporan.setVisibility(View.GONE);
                                } else {
                                    btnEditStatusLaporan.setVisibility(View.VISIBLE);
                                }
                                btnHapus.setEnabled(userIn.equals(idPengisi)||tipePengisi.equals("p2k3"));

                                progressDialog.dismiss();
                                dialog.show();
                                Log.d(TAG, "ID Pengisi: "+idPengisi);

                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                btnDetail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final DocumentReference docInsidenRef = insidenModel.getReference();
                                        docInsidenRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    Intent intent = new Intent(DaftarInsiden.this, DetailInsiden.class);
                                                    intent.putExtra(DetailInsiden.DETAIL_KODE_INSIDEN, document.getString("kode_laporinsiden"));
                                                    startActivity(intent);
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });

                                btnEditStatusLaporan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final DocumentReference docInsidenRef = insidenModel.getReference();
                                        docInsidenRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                Intent intent = new Intent(DaftarInsiden.this, EditStatusInsiden.class);
                                                intent.putExtra(EditStatusInsiden.DETAIL_EDIT_STATUS_INSIDEN, document.getString("kode_laporinsiden"));
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                btnHapus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogHapusYT_Insiden();
                                    }

                                    private void dialogHapusYT_Insiden() {
                                        DialogInterface.OnClickListener dialogHapus = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                switch (i) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        hapus_insiden();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        dialogInterface.dismiss();
                                                        break;
                                                }
                                            }
                                        };
                                        AlertDialog.Builder builderHapus = new AlertDialog.Builder(DaftarInsiden.this);
                                        builderHapus.setMessage("Hapus Data?").setPositiveButton("Ya", dialogHapus)
                                                .setNegativeButton("Tidak", dialogHapus).show();
                                    }


                                    private void hapus_insiden() {
                                        final DocumentReference docInsidenRef = insidenModel.getReference();
                                        Map<String, Object> deleteInsiden = new HashMap<>();
                                        deleteInsiden.put("status_deleted_insiden", true);
                                        deleteInsiden.put("diupdate_insiden", tglDiupdate);
                                        docInsidenRef.set(deleteInsiden, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                Toast.makeText(DaftarInsiden.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "Dokumen berhasil dihapus!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(DaftarInsiden.this, "Gagal menghapus Data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(DaftarInsiden.this, "Gagal menampilkan dialog menu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    dialog.dismiss();
                    Toast.makeText(DaftarInsiden.this, "Gagal menampilkan dialog menu", Toast.LENGTH_SHORT).show();
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