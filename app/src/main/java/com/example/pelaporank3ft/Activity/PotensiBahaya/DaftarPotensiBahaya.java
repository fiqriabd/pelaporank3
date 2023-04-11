package com.example.pelaporank3ft.Activity.PotensiBahaya;

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

import com.example.pelaporank3ft.Activity.Insiden.DaftarInsiden;
import com.example.pelaporank3ft.Activity.Insiden.EditStatusInsiden;
import com.example.pelaporank3ft.Adapter.PotensiBahayaAdapter;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DaftarPotensiBahaya extends AppCompatActivity implements PotensiBahayaAdapter.OnPotensiBahayaSelectedListener{

    private DocumentReference mUserIn;
    private FloatingActionButton btnTambahPB;
    private PotensiBahayaAdapter mAdapter;
    private ProgressBar pbPotensiBahaya;
    private RecyclerView daftarPotensiBahayaRec;
    private String userIn, userPengisi, idPengisi, tipePengisi;

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
        setContentView(R.layout.activity_daftar_potensi_bahaya);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mUser = mAuth.getCurrentUser();
        userIn = mAuth.getCurrentUser().getUid();
        userPengisi = mAuth.getUid();
        mQuery = mFirestore.collection("potensiBahayas").whereEqualTo("status_deleted_pb", false);

        initView();

    }

    private void initView() {
        btnTambahPB = findViewById(R.id.btn_tambah_potensi_bahaya);
        daftarPotensiBahayaRec = findViewById(R.id.recycler_daftar_pb);
        pbPotensiBahaya = findViewById(R.id.pb_potensibahaya);

        pbPotensiBahaya.setVisibility(View.VISIBLE);

        if (mQuery == null){
            Log.w(TAG, "Tidak ada data");
        }

        mAdapter = new PotensiBahayaAdapter(mQuery, this){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    daftarPotensiBahayaRec.setVisibility(View.GONE);
                    pbPotensiBahaya.setVisibility(View.GONE);
                    Log.w(TAG, "Jumlah Potensi Bahaya = 0");
                } else {
                    daftarPotensiBahayaRec.setVisibility(View.VISIBLE);
                    pbPotensiBahaya.setVisibility(View.GONE);
                    Log.w(TAG, "Menampilkan Daftar Potensi Bahaya");
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e(TAG, "Eror: cek log untuk info");
            }
        };

        btnTambahPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DaftarPotensiBahaya.this, LaporPotensiBahaya.class));
            }
        });

        daftarPotensiBahayaRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        daftarPotensiBahayaRec.setAdapter(mAdapter);
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

    @Override
    public void onPotensiBahayaSelected(DocumentSnapshot PBahayaModel) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference docPengisi = PBahayaModel.getReference();
        docPengisi.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dokumen = task.getResult();
                    idPengisi = dokumen.getString("id_user");
                    Log.d(TAG, "UserLogin: " + userIn);

                    mUserIn = mFirestore.collection("users").document(userIn);
                    mUserIn.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot dokumen1 = task.getResult();
                                tipePengisi = dokumen1.getString("tipe_user");

                                Log.d(TAG, "Tipe User: " + tipePengisi);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DaftarPotensiBahaya.this);
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
                                        final DocumentReference docPBRef = PBahayaModel.getReference();
                                        docPBRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    Intent intent = new Intent(DaftarPotensiBahaya.this, DetailPotensiBahaya.class);
                                                    intent.putExtra(DetailPotensiBahaya.DETAIL_KODE_POTENSI_BAHAYA, document.getString("kode_potensibahaya"));
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
                                        final DocumentReference docPBRef = PBahayaModel.getReference();
                                        docPBRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                Intent intent = new Intent(DaftarPotensiBahaya.this, EditStatusPotensiBahaya.class);
                                                intent.putExtra(EditStatusPotensiBahaya.DETAIL_EDIT_STATUS_PB, document.getString("kode_potensibahaya"));
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                btnHapus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogHapusYT_PB();
                                    }

                                    private void dialogHapusYT_PB() {
                                        DialogInterface.OnClickListener dialogHapus = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                switch (i) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        hapus_pb();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        dialogInterface.dismiss();
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builderHapus = new AlertDialog.Builder(DaftarPotensiBahaya.this);
                                        builderHapus.setMessage("Hapus Data?").setPositiveButton("Ya", dialogHapus)
                                                .setNegativeButton("Tidak", dialogHapus).show();
                                    }

                                    private void hapus_pb() {
                                        final DocumentReference docPBRef = PBahayaModel.getReference();
                                        Map<String, Object> deletePB = new HashMap<>();
                                        deletePB.put("status_deleted_pb", true);
                                        docPBRef.set(deletePB, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                Toast.makeText(DaftarPotensiBahaya.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "Dokumen berhasil dihapus!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DaftarPotensiBahaya.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                                Log.w(TAG, "Dokumen gagal dihapus", e);
                                            }
                                        });
                                    }
                                });
                            }  else {
                                dialog.dismiss();
                                Toast.makeText(DaftarPotensiBahaya.this, "Gagal menampilkan dialog menu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else{
                    dialog.dismiss();
                    Toast.makeText(DaftarPotensiBahaya.this, "Gagal menampilkan dialog menu", Toast.LENGTH_SHORT).show();
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