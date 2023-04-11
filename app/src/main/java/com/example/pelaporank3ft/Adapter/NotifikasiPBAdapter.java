package com.example.pelaporank3ft.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pelaporank3ft.Model.PotensiBahayaModel;
import com.example.pelaporank3ft.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class NotifikasiPBAdapter extends FirestoreAdapter<NotifikasiPBAdapter.ViewHolder>{
    private OnNotifikasiPBSelectedListener mListener;

    public NotifikasiPBAdapter(Query query, OnNotifikasiPBSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.list_notifikasi_pb, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnNotifikasiPBSelectedListener {
        void onNotifikasiPBSelected(DocumentSnapshot PBModel);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView kodePB, lokasiPB, tanggalLPB;

        public ViewHolder(View itemView) {
            super(itemView);

            kodePB = itemView.findViewById(R.id.notifikasi_kode_pb);
            lokasiPB = itemView.findViewById(R.id.notifikasi_lokasi_pb);
            tanggalLPB = itemView.findViewById(R.id.notifikasi_tanggal_lapor);
        }

        public void bind(final DocumentSnapshot snapshot, final OnNotifikasiPBSelectedListener listener) {

            PotensiBahayaModel model = snapshot.toObject(PotensiBahayaModel.class);

            kodePB.setText(model.getKode_potensibahaya());
            lokasiPB.setText(model.getLokasi_departemen_pb());
            tanggalLPB.setText(model.getTgl_lapor_pb());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onNotifikasiPBSelected(snapshot);
                    }
                }
            });
        }
    }
}
