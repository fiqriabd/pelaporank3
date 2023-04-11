package com.example.pelaporank3ft.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pelaporank3ft.Model.LaporInsidensModel;
import com.example.pelaporank3ft.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class DaftarInsidenAdapter extends FirestoreAdapter<DaftarInsidenAdapter.ViewHolder>{
    private OnInsidenSelectedListener mListener;

    public DaftarInsidenAdapter(Query query, OnInsidenSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.list_daftar_insiden, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnInsidenSelectedListener {
        void onInsidenSelected(DocumentSnapshot insidenModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView kodeInsiden, waktuInsiden, lokasiInsiden, pelaporInsiden, korbanInsiden, statusLaporan;

        public ViewHolder(View itemView) {
            super(itemView);

            kodeInsiden = itemView.findViewById(R.id.tv_list_data_kode_lapor_insiden);
            waktuInsiden = itemView.findViewById(R.id.tv_list_data_waktu_kejadian_insiden);
            lokasiInsiden = itemView.findViewById(R.id.tv_list_data_lokasi_insiden);
            pelaporInsiden = itemView.findViewById(R.id.tv_list_nama_pelapor_insiden);
            korbanInsiden = itemView.findViewById(R.id.tv_list_nama_korban_insiden);
            statusLaporan = itemView.findViewById(R.id.tv_list_status_laporan_insiden);
        }

        public void bind(final DocumentSnapshot snapshot, final OnInsidenSelectedListener listener) {

            LaporInsidensModel model = snapshot.toObject(LaporInsidensModel.class);

            kodeInsiden.setText(model.getKode_laporinsiden());
            waktuInsiden.setText(model.getWaktu_kejadian_insiden());
            lokasiInsiden.setText(model.getLokasi_departemen_insiden());
            pelaporInsiden.setText(model.getNama_pelapor_insiden());
            if (model.getNama_korban_insiden() == null){
                korbanInsiden.setText("");
            } else  {
                korbanInsiden.setText(model.getNama_korban_insiden());
            }
            statusLaporan.setText(model.getStatus_laporan_insiden());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onInsidenSelected(snapshot);
                    }
                }
            });
        }

    }
}
