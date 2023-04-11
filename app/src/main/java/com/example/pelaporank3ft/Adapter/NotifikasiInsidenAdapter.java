package com.example.pelaporank3ft.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pelaporank3ft.Model.LaporInsidensModel;
import com.example.pelaporank3ft.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class NotifikasiInsidenAdapter extends FirestoreAdapter<NotifikasiInsidenAdapter.ViewHolder>{
    private OnNotifikasiInsidenSelectedListener mListener;

    public NotifikasiInsidenAdapter(Query query, OnNotifikasiInsidenSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.list_notifikasi_insiden, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnNotifikasiInsidenSelectedListener {
        void onNotifikasiInsidenSelected(DocumentSnapshot insidenModel);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView kodeInsiden, lokasiInsiden, waktuInsiden;

        public ViewHolder(View itemView) {
            super(itemView);

            kodeInsiden = itemView.findViewById(R.id.notifikasi_kode_insiden);
            lokasiInsiden = itemView.findViewById(R.id.notifikasi_lokasi_insiden);
            waktuInsiden = itemView.findViewById(R.id.notifikasi_waktu_kejadian);
        }

        public void bind(final DocumentSnapshot snapshot, final OnNotifikasiInsidenSelectedListener listener) {

            LaporInsidensModel model = snapshot.toObject(LaporInsidensModel.class);

            kodeInsiden.setText(model.getKode_laporinsiden());
            lokasiInsiden.setText(model.getLokasi_departemen_insiden());
            waktuInsiden.setText(model.getWaktu_kejadian_insiden());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onNotifikasiInsidenSelected(snapshot);
                    }
                }
            });
        }
    }
}

