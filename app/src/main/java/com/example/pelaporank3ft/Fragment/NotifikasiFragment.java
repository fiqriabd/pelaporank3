package com.example.pelaporank3ft.Fragment;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pelaporank3ft.Activity.DashboardActivity_P2K3;
import com.example.pelaporank3ft.Activity.Insiden.EditStatusInsiden;
import com.example.pelaporank3ft.Activity.PotensiBahaya.EditStatusPotensiBahaya;
import com.example.pelaporank3ft.Adapter.NotifikasiInsidenAdapter;
import com.example.pelaporank3ft.Adapter.NotifikasiPBAdapter;
import com.example.pelaporank3ft.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class NotifikasiFragment extends Fragment implements NotifikasiInsidenAdapter.OnNotifikasiInsidenSelectedListener, NotifikasiPBAdapter.OnNotifikasiPBSelectedListener {

    private NotifikasiInsidenAdapter mAdapter_Insiden;
    private NotifikasiPBAdapter mAdapter_PB;

    private TotalDataListener totalDataListener;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private Query mQuery_Insiden;
    private Query mQuery_PB;

    private int jumlahinsiden = 0, jumlahpb = 0, jumlahdata = 0;
    private ProgressBar pbNotifikasi;
    private RecyclerView notifikasiInsidenRec, notifikasiPBRec;
    private String userPengisi, userIn, jml;
    private TextView jumlahNotifikasi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifikasi, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userIn = mAuth.getCurrentUser().getUid();
        userPengisi = mAuth.getUid();
        mStorage = FirebaseStorage.getInstance();

        mQuery_Insiden = mFirestore.collection("laporInsidens")
                .whereEqualTo("status_deleted_insiden", false)
                .whereEqualTo("status_laporan_insiden", "Pending");

        mQuery_PB = mFirestore.collection("potensiBahayas")
                .whereEqualTo("status_deleted_pb", false)
                .whereEqualTo("status_laporan_pb", "Pending");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifikasiInsidenRec = view.findViewById(R.id.recycler_notifikasi_insiden);
        notifikasiPBRec = view.findViewById(R.id.recycler_notifikasi_PB);
        pbNotifikasi = view.findViewById(R.id.pb_notifikasi);
        jumlahNotifikasi = view.findViewById(R.id.jumlah_angka_notifikasi);

        pbNotifikasi.setVisibility(View.VISIBLE);

        if (mQuery_Insiden == null) {
            Log.w(TAG, "Tidak ada data");
        }

        mAdapter_Insiden = new NotifikasiInsidenAdapter(mQuery_Insiden, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty
                if (getItemCount() == 0) {
                    notifikasiInsidenRec.setVisibility(View.GONE);
                    pbNotifikasi.setVisibility(View.GONE);
                    Log.w(TAG, "Jumlah Insiden = 0");
                } else {
                    notifikasiInsidenRec.setVisibility(View.VISIBLE);
                    pbNotifikasi.setVisibility(View.GONE);
                    Log.w(TAG, "Menampilkan Daftar Insiden");
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e(TAG, "Eror: cek log untuk info");
            }
        };

        if (mQuery_PB == null) {
            Log.w(TAG, "Tidak ada data");
        }

        mAdapter_PB = new NotifikasiPBAdapter(mQuery_PB, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty
                if (getItemCount() == 0) {
                    notifikasiPBRec.setVisibility(View.GONE);
                    pbNotifikasi.setVisibility(View.GONE);
                    Log.w(TAG, "Jumlah Potensi Bahaya = 0");
                } else {
                    notifikasiPBRec.setVisibility(View.VISIBLE);
                    pbNotifikasi.setVisibility(View.GONE);
                    Log.w(TAG, "Menampilkan Daftar Potensi Bahaya");
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e(TAG, "Eror: cek log untuk info");
            }
        };
        notifikasiInsidenRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        notifikasiPBRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        notifikasiInsidenRec.setAdapter(mAdapter_Insiden);
        notifikasiPBRec.setAdapter(mAdapter_PB);


        mQuery_Insiden.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }
                jumlahinsiden = value.size();
                updateTotalData(jumlahdata);

                mQuery_PB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            return;
                        }
                        jumlahpb = value.size();
                        jumlahdata = jumlahinsiden + jumlahpb;
                        updateTotalData(jumlahdata);
                    }
                });
            }

        });
    }

    public void updateTotalData(int jumlahdata) {
        this.jumlahdata = jumlahdata;
        jumlahNotifikasi.setText(String.valueOf(jumlahdata));
        if (totalDataListener != null) {
            totalDataListener.onDataTotalUpdated(jumlahdata);
        }
    }

    public interface TotalDataListener{
        void onDataTotalUpdated(int jumlahdata);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            totalDataListener = (TotalDataListener) context;
        } catch (ClassCastException e){
            Log.d(TAG, "TOTAL DATANYA : "+ jumlahdata);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter_Insiden != null) {
            mAdapter_Insiden.startListening();
        }
        if (mAdapter_PB != null) {
            mAdapter_PB.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter_Insiden != null) {
            mAdapter_Insiden.stopListening();
        }
        if (mAdapter_PB != null) {
            mAdapter_PB.stopListening();
        }
    }

    @Override
    public void onNotifikasiInsidenSelected(DocumentSnapshot insidenModel) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference editStatusInsiden = insidenModel.getReference();
        editStatusInsiden.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                progressDialog.dismiss();
                Intent intent = new Intent(getContext(), EditStatusInsiden.class);
                intent.putExtra(EditStatusInsiden.DETAIL_EDIT_STATUS_INSIDEN, document.getString("kode_laporinsiden"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNotifikasiPBSelected(DocumentSnapshot PBModel) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference editStatusPB = PBModel.getReference();
        editStatusPB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                progressDialog.dismiss();
                Intent intent = new Intent(getContext(), EditStatusPotensiBahaya.class);
                intent.putExtra(EditStatusPotensiBahaya.DETAIL_EDIT_STATUS_PB, document.getString("kode_potensibahaya"));
                startActivity(intent);
            }
        });
    }
}