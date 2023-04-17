package com.example.pelaporank3ft.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pelaporank3ft.Model.PotensiBahayaModel;
import com.example.pelaporank3ft.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class PotensiBahayaAdapter extends FirestoreAdapter<PotensiBahayaAdapter.ViewHolder> {
    private OnPotensiBahayaSelectedListener mListener;

    public PotensiBahayaAdapter(Query query, OnPotensiBahayaSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.list_daftar_potensi_bahaya, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnPotensiBahayaSelectedListener {

        void onPotensiBahayaSelected(DocumentSnapshot potensiBahayaModel);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView kodePB, waktuPB, lokasiPB, pelaporPB, statusLaporan;

        public ViewHolder(View itemView) {
            super(itemView);

            kodePB = itemView.findViewById(R.id.tv_list_data_kode_lapor_pb);
            waktuPB = itemView.findViewById(R.id.tv_list_data_tgl_lapor_pb);
            pelaporPB = itemView.findViewById(R.id.tv_list_nama_pelapor_pb);
            lokasiPB = itemView.findViewById(R.id.tv_list_data_lokasi_pb);
            statusLaporan = itemView.findViewById(R.id.tv_list_status_laporan_pb);
        }

        public void bind(final DocumentSnapshot snapshot, final OnPotensiBahayaSelectedListener listener) {

            PotensiBahayaModel model = snapshot.toObject(PotensiBahayaModel.class);

            kodePB.setText(model.getKode_potensibahaya());
            waktuPB.setText(model.getTgl_lapor_pb());
            lokasiPB.setText(model.getLokasi_departemen_pb());
            pelaporPB.setText(model.getNama_pelapor_pb());
            if (Objects.equals(model.getStatus_laporan_pb(), "Pending")){
                statusLaporan.setText("Pending");
                statusLaporan.setTextColor(ContextCompat.getColor((Context) listener, R.color.red));
            } else if (Objects.equals(model.getStatus_laporan_pb(), "Ditindaklanjuti")) {
                statusLaporan.setText("Ditindaklanjuti");
                statusLaporan.setTextColor(ContextCompat.getColor((Context) listener, R.color.oren));
            } else {
                statusLaporan.setText("Disetujui");
                statusLaporan.setTextColor(ContextCompat.getColor((Context) listener, R.color.green));
            }

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPotensiBahayaSelected(snapshot);
                    }
                }
            });
        }

    }
}
