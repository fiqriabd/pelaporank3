package com.example.pelaporank3ft.Activity.PotensiBahaya;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LaporPotensiBahaya extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE_TP = 400;
    private static final int MY_CAMERA_REQUEST_CODE_PB = 500;

    private DocumentReference mGeneratePBValue, mUserRef;
    private Double totalValueGenerated;
    private StorageReference storageReference;
    private Uri uriKameraIntent_fotoTP, uriGambarPB_fotoTP = null,
            uriKameraIntent_fotoPB, uriGambarPB_fotoPB = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private String documentId_PB, userId, idPengisi, tglDibuat, tglDiupdate, namaPengisi, emailPengisi;

    private AutoCompleteTextView ddKategoriPelapor, ddUnitCivitasAkademika, ddPotensiBahaya;
    private DatePickerDialog waktuTanggalPotensiBahaya;
    private EditText etKodePotensiBahaya, etFotoTandaPengenal, etNamaPelapor, etEmailPelapor,
            etNipNimPelapor, etNoTeleponPelapor, etTglLapor, etInstitusiDikunjungi,
            etTujuan, etLokasi, etDeskripsi, etResikoBahaya, etUsulanPerbaikan, etFotoPB;
    private SimpleDateFormat formatTanggalPotensiBahaya;
    private String kodePotensiBahaya, tandaPengenal, namaPelapor, emailPelapor, nipNim,
            nomorTeleponPelapor, tglLapor, kategoriPelapor, institusi, tujuan, lokasiDepartemen,
            lokasi, potensiBahaya, deskripsiPotensiBahaya, resikoBahaya, usulanPerbaikan, gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_potensi_bahaya);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mUserRef = mFirestore.collection("users").document(userId);
        mGeneratePBValue = mFirestore.collection("totalValueGenerated").document("totalValueGenerated");
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        initView();
        getDataGenerateID_PotensiBahaya();
        getIDPengisi();
        getTanggal();
    }

    private void initView() {
        etKodePotensiBahaya = findViewById(R.id.et_kode_potensi_bahaya);
        etFotoTandaPengenal = findViewById(R.id.et_foto_tanda_pengenal_pb);
        etNamaPelapor = findViewById(R.id.et_lpb_nama_pelapor);
        etEmailPelapor = findViewById(R.id.et_kolom_email_pelapor_pb);
        etNipNimPelapor = findViewById(R.id.et_lpb_nip_nim_pelapor);
        etNoTeleponPelapor = findViewById(R.id.et_lpb_notelepon_pelapor);
        etTglLapor = findViewById(R.id.et_lpb_tgl_lapor);
        ddKategoriPelapor = findViewById(R.id.lpb_dd_kategori_pelapor);
        etInstitusiDikunjungi = findViewById(R.id.et_lpb_institusi_dikunjungi);
        etTujuan = findViewById(R.id.et_lpb_tujuan);
        ddUnitCivitasAkademika = findViewById(R.id.lpb_dd_unit_civitas_akademika);
        etLokasi = findViewById(R.id.et_lpb_lokasi_pb);
        ddPotensiBahaya = findViewById(R.id.lpb_dd_potensi_bahaya);
        etDeskripsi = findViewById(R.id.et_lpb_deskripsi_pb);
        etResikoBahaya = findViewById(R.id.et_lpb_resiko_bahaya);
        etUsulanPerbaikan = findViewById(R.id.et_lpb_usulan_perbaikan);
        etFotoPB = findViewById(R.id.et_lpb_foto_pb);

        etFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilGambar_FotoTP();
            }
        });

        //Format Tanggal
        formatTanggalPotensiBahaya = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        //Klik untuk menampilkan tanggal
        etTglLapor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tampilkan_tanggal_potensi_bahaya();
            }

            //Menampilkan tanggal
            private void tampilkan_tanggal_potensi_bahaya() {
                Calendar calendar_potensi_bahaya = Calendar.getInstance();
                waktuTanggalPotensiBahaya = new DatePickerDialog(LaporPotensiBahaya.this, (view, year, month, dayOfMonth) -> {
                    Calendar tglPotensiBahaya = Calendar.getInstance();
                    tglPotensiBahaya.set(year, month, dayOfMonth);
                    etTglLapor.setText(formatTanggalPotensiBahaya.format(tglPotensiBahaya.getTime()));
                }, calendar_potensi_bahaya.get(Calendar.YEAR), calendar_potensi_bahaya.get(Calendar.MONTH), calendar_potensi_bahaya.get(Calendar.DAY_OF_MONTH));
                waktuTanggalPotensiBahaya.show();
            }
        });

        //Pilihan pada Kategori Pelapor
        String[] arraykategoripelapor = getResources().getStringArray(R.array.kategoripelapor);
        ArrayAdapter<String> arraykp = new ArrayAdapter<>(this, R.layout.dropdown_item, arraykategoripelapor);
        ddKategoriPelapor.setText(arraykp.getItem(0), false);
        ddKategoriPelapor.setText("");
        ddKategoriPelapor.setAdapter(arraykp);

        //Pilihan pada Unit Civitas Akademika
        String[] arrayunitcivitasakademika = getResources().getStringArray(R.array.departemenft);
        ArrayAdapter<String> arrayuca = new ArrayAdapter<>(this, R.layout.dropdown_item, arrayunitcivitasakademika);
        ddUnitCivitasAkademika.setText(arrayuca.getItem(0),false);
        ddUnitCivitasAkademika.setText("");
        ddUnitCivitasAkademika.setAdapter(arrayuca);

        //Pilihan pada Potensi Bahaya
        String[] arraypotensibahaya = getResources().getStringArray(R.array.jenispotensibahaya);
        ArrayAdapter<String> arraypb = new ArrayAdapter<>(this, R.layout.dropdown_item, arraypotensibahaya);
        ddPotensiBahaya.setText(arraypb.getItem(0), false);
        ddPotensiBahaya.setText("");
        ddPotensiBahaya.setAdapter(arraypb);

        //Klik pada EditText Foto PB
        etFotoPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilGambar_FotoPB();
            }
        });

        //Button Submit
        MaterialButton lpb_btn_submit = findViewById(R.id.lpb_btn_submit);
        lpb_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg_Foto_Tanda_Pengenal();
            }
        });

        //Button Reset
        MaterialButton lpb_btn_reset = findViewById(R.id.lpb_btn_reset);
        lpb_btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResetYT_PB();
            }
        });
    }

    private void getDataGenerateID_PotensiBahaya() {
        mGeneratePBValue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Log.d(TAG, "Dokumen tersedia!");
                        totalValueGenerated = document.getDouble("totalPotensiBahaya");
                        Log.d(TAG, String.valueOf(totalValueGenerated));
                        totalValueGenerated +=1;
                        documentId_PB = "PTNSBHYA-"+ String.format("%03d", Math.round(totalValueGenerated));
                        etKodePotensiBahaya.setText(documentId_PB);
                    }else {
                        Log.d(TAG, "Tidak ada Dokumen");
                    }
                } else {
                    Log.d(TAG, "Gagal mendapatkan ID: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE_TP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_LONG).show();
                takeFoto_TP();
            } else {
                Toast.makeText(this, "Izin kamera ditolak, mohon berikan izin akses kamera", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_CAMERA_REQUEST_CODE_PB) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_LONG).show();
                takeFoto_PB();
            } else {
                Toast.makeText(this, "Izin kamera ditolak, mohon berikan izin akses kamera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ambilGambar_FotoTP() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LaporPotensiBahaya.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_menu_gambar, null);
        builder.setView(view);
        ImageButton btnClose = view.findViewById(R.id.btn_close_gambar);
        Button btnKamera = view.findViewById(R.id.btn_kamera);
        Button btnGaleri = view.findViewById(R.id.btn_galeri);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE_TP);
                } else {
                    takeFoto_TP();
                }
                dialog.dismiss();
            }
        });

        btnGaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Defining Implicit Intent to mobile gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launchFTP_PBActivity.launch(intent);
                dialog.dismiss();
            }
        });
    }

    private void takeFoto_TP() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriKameraIntent_fotoTP = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, uriKameraIntent_fotoTP);
        intentKamera.resolveActivity(getPackageManager());
        startKamera_fotoTP.launch(intentKamera);
    }

    ActivityResultLauncher<Intent> startKamera_fotoTP = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {

                try {
                    String[] koloms = {MediaStore.Images.ImageColumns.DATA};
                    Cursor kursorKamera = getContentResolver().query(Uri.parse(uriKameraIntent_fotoTP.toString()), koloms, null, null, null);
                    kursorKamera.moveToFirst();

                    String kameraPath = kursorKamera.getString(0);
                    kursorKamera.close();

                    String namaFile = "Foto Tanda Pengenal.jpg";

                    File fileKamera = new File(kameraPath);
                    uriGambarPB_fotoTP = Uri.fromFile(fileKamera);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporPotensiBahaya.this.getContentResolver(), uriGambarPB_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    etFotoTandaPengenal.setText(namaFile);
                    bitmapKamera = handleSamplingRotationBitmap_fotoTP(uriGambarPB_fotoTP);
                    Log.e(String.valueOf(uriGambarPB_fotoTP), "uri");

                } catch (CursorIndexOutOfBoundsException | IOException ex) {
                    ex.printStackTrace();

                    String[] koloms = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED};
                    Cursor kursors = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, koloms, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC");

                    int columnIndex = kursors.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    kursors.moveToFirst();

                    String pathKamera = kursors.getString(columnIndex);
                    kursors.close();

                    String namaFile = "Foto Tanda Pengenal.jpg";

                    File kameraFile = new File(pathKamera);
                    uriGambarPB_fotoTP = Uri.fromFile(kameraFile);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporPotensiBahaya.this.getContentResolver(), uriGambarPB_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    etFotoTandaPengenal.setText(namaFile);
                    Log.e(String.valueOf(uriGambarPB_fotoTP), "uri");
                }
            } else {
                getContentResolver().delete(uriKameraIntent_fotoTP,null,null);
            }
        }
    });

    private Bitmap handleSamplingRotationBitmap_fotoTP(Uri uriKameraSelected_fotoTP) throws IOException {
        int MAX_TINGGI = 1024;
        int MAX_LEBAR = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options opsi = new BitmapFactory.Options();
        opsi.inJustDecodeBounds = true;
        InputStream gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoTP);
        BitmapFactory.decodeStream(gambarStream, null, opsi);
        gambarStream.close();

        //Calculate inSampleSize
        opsi.inSampleSize = kalkulasiInUkuranSample_FotoTP(opsi, MAX_LEBAR, MAX_TINGGI);

        //Decode bitmap with inSampleSize set
        opsi.inJustDecodeBounds = false;
        gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoTP);
        Bitmap imgKamera = BitmapFactory.decodeStream(gambarStream, null, opsi);
        if (imgKamera != null){
            imgKamera = rotasiGambarJikaDiperlukan_FotoTP(imgKamera, uriKameraSelected_fotoTP);
        } else {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            return null;
        }
        return imgKamera;
    }

    private int kalkulasiInUkuranSample_FotoTP(BitmapFactory.Options opsi, int reqLebar, int reqTinggi) {
        int tinggi = opsi.outHeight;
        int lebar = opsi.outWidth;
        int inUkuranSample = 1;

        if (tinggi > reqTinggi || lebar > reqLebar) {
            int rasioTinggi = Math.round(((float) tinggi / (float) reqTinggi));
            int rasioLebar = Math.round(((float) lebar / (float) reqLebar));
            inUkuranSample = Math.min(rasioTinggi, rasioLebar);
            float totalPiksel = (float) (lebar * tinggi);
            float totalReqPikselCap = (float) (reqLebar * reqTinggi * 2);
            while (totalPiksel / (inUkuranSample * inUkuranSample) > totalReqPikselCap) {
                inUkuranSample++;
            }
        }
        return inUkuranSample;
    }

    private Bitmap rotasiGambarJikaDiperlukan_FotoTP(Bitmap imgKamera, Uri uriKameraSelected_fotoTP) throws IOException {
        ExifInterface ei = new ExifInterface(uriKameraSelected_fotoTP.getPath());

        switch (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotasiGambar_fotoTP(imgKamera, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotasiGambar_fotoTP(imgKamera, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotasiGambar_fotoTP(imgKamera, 270);
            default:
                return imgKamera;
        }
    }

    private Bitmap rotasiGambar_fotoTP(Bitmap imgKamera, int derajat) {
        Matrix matriks = new Matrix();
        matriks.postRotate((float) derajat);
        Bitmap rotatedImg = Bitmap.createBitmap(imgKamera, 0, 0, imgKamera.getWidth(), imgKamera.getHeight(), matriks, true);
        imgKamera.recycle();
        return rotatedImg;
    }

    private void ambilGambar_FotoPB() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LaporPotensiBahaya.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_menu_gambar, null);
        builder.setView(view);
        ImageButton btnClose = view.findViewById(R.id.btn_close_gambar);
        Button btnKamera = view.findViewById(R.id.btn_kamera);
        Button btnGaleri = view.findViewById(R.id.btn_galeri);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE_PB);
                } else {
                    takeFoto_PB();
                }
                dialog.dismiss();
            }
        });

        btnGaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mendefinisikan Intent implisit ke galeri ponsel
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launchFK_PBActivity.launch(intent);
                dialog.dismiss();
            }
        });
    }

    private void takeFoto_PB() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriKameraIntent_fotoPB = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, uriKameraIntent_fotoPB);
        intentKamera.resolveActivity(getPackageManager());
        startKamera_fotoPB.launch(intentKamera);
    }

    ActivityResultLauncher<Intent> startKamera_fotoPB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {

                try {
                    String[] koloms = {MediaStore.Images.ImageColumns.DATA};
                    Cursor kursorKamera = getContentResolver().query(Uri.parse(uriKameraIntent_fotoPB.toString()), koloms, null, null, null);
                    kursorKamera.moveToFirst();

                    String kameraPath = kursorKamera.getString(0);
                    kursorKamera.close();

                    String namaFile = "Foto Potensi Bahaya.jpg";

                    File fileKamera = new File(kameraPath);
                    uriGambarPB_fotoPB = Uri.fromFile(fileKamera);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporPotensiBahaya.this.getContentResolver(), uriGambarPB_fotoPB);
                        etFotoPB.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    etFotoPB.setText(namaFile);
                    bitmapKamera = handleSamplingRotationBitmap_fotoPB(uriGambarPB_fotoPB);
                    Log.e(String.valueOf(uriGambarPB_fotoPB), "uri");

                } catch (CursorIndexOutOfBoundsException | IOException ex) {
                    ex.printStackTrace();

                    String[] koloms = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED};
                    Cursor kursors = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, koloms, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC");

                    int columnIndex = kursors.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    kursors.moveToFirst();

                    String pathKamera = kursors.getString(columnIndex);
                    kursors.close();

                    String namaFile = "Foto Potensi Bahaya.jpg";

                    File kameraFile = new File(pathKamera);
                    uriGambarPB_fotoPB = Uri.fromFile(kameraFile);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporPotensiBahaya.this.getContentResolver(), uriGambarPB_fotoPB);
                        etFotoPB.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    etFotoPB.setText(namaFile);
                    Log.e(String.valueOf(uriGambarPB_fotoPB), "uri");
                }
            } else {
                getContentResolver().delete(uriKameraIntent_fotoPB,null,null);
            }
        }
    });

    private Bitmap handleSamplingRotationBitmap_fotoPB(Uri uriKameraSelected_fotoPB) throws IOException {
        int MAX_TINGGI = 1024;
        int MAX_LEBAR = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options opsi = new BitmapFactory.Options();
        opsi.inJustDecodeBounds = true;
        InputStream gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoPB);
        BitmapFactory.decodeStream(gambarStream, null, opsi);
        gambarStream.close();

        //Calculate inSampleSize
        opsi.inSampleSize = kalkulasiInUkuranSample_FotoPB(opsi, MAX_LEBAR, MAX_TINGGI);

        //Decode bitmap with inSampleSize set
        opsi.inJustDecodeBounds = false;
        gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoPB);
        Bitmap imgKamera = BitmapFactory.decodeStream(gambarStream, null, opsi);
        if (imgKamera != null){
            imgKamera = rotasiGambarJikaDiperlukan_FotoPB(imgKamera, uriKameraSelected_fotoPB);
        } else {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            return null;
        }
        return imgKamera;
    }

    private int kalkulasiInUkuranSample_FotoPB(BitmapFactory.Options opsi, int reqLebar, int reqTinggi) {
        int tinggi = opsi.outHeight;
        int lebar = opsi.outWidth;
        int inUkuranSample = 1;

        if (tinggi > reqTinggi || lebar > reqLebar) {
            int rasioTinggi = Math.round(((float) tinggi / (float) reqTinggi));
            int rasioLebar = Math.round(((float) lebar / (float) reqLebar));
            inUkuranSample = Math.min(rasioTinggi, rasioLebar);
            float totalPiksel = (float) (lebar * tinggi);
            float totalReqPikselCap = (float) (reqLebar * reqTinggi * 2);
            while (totalPiksel / (inUkuranSample * inUkuranSample) > totalReqPikselCap) {
                inUkuranSample++;
            }
        }
        return inUkuranSample;
    }

    private Bitmap rotasiGambarJikaDiperlukan_FotoPB(Bitmap imgKamera, Uri uriKameraSelected_fotoPB) throws IOException {
        ExifInterface ei = new ExifInterface(uriKameraSelected_fotoPB.getPath());

        switch (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotasiGambar_fotoPB(imgKamera, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotasiGambar_fotoPB(imgKamera, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotasiGambar_fotoPB(imgKamera, 270);
            default:
                return imgKamera;
        }
    }

    private Bitmap rotasiGambar_fotoPB(Bitmap imgKamera, int derajat) {
        Matrix matriks = new Matrix();
        matriks.postRotate((float) derajat);
        Bitmap rotatedImg = Bitmap.createBitmap(imgKamera, 0, 0, imgKamera.getWidth(), imgKamera.getHeight(), matriks, true);
        imgKamera.recycle();
        return rotatedImg;
    }

    ActivityResultLauncher<Intent> launchFTP_PBActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();

                if (data != null && data.getData() != null){
                    uriGambarPB_fotoTP = data.getData();
                    String uriString_fotoTP = uriGambarPB_fotoTP.toString();
                    File myFile_fotoTP = new File(uriString_fotoTP);
                    String path = myFile_fotoTP.getAbsolutePath();
                    String displayName_fotoTP = null;

                    if (uriString_fotoTP.startsWith("content://")) {
                        try (Cursor cursor_fotoTP = LaporPotensiBahaya.this.getContentResolver().query(uriGambarPB_fotoTP, null, null, null, null)) {
                            if (cursor_fotoTP != null && cursor_fotoTP.moveToFirst()) {
                                displayName_fotoTP = cursor_fotoTP.getString(cursor_fotoTP.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                etFotoTandaPengenal.setText(displayName_fotoTP);
                            }
                        }
                    } else if (uriString_fotoTP.startsWith("file://"))  {
                        displayName_fotoTP = myFile_fotoTP.getName();
                        etFotoTandaPengenal.setText(displayName_fotoTP);
                    }
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriGambarPB_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                        Log.e(String.valueOf(uriGambarPB_fotoTP), "uri");

                    } catch (IOException e){
                        //Log the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    ActivityResultLauncher<Intent> launchFK_PBActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();

                if (data != null && data.getData() != null){
                    uriGambarPB_fotoPB = data.getData();
                    String uriString_fotoPB = uriGambarPB_fotoPB.toString();
                    File myFile_fotoPB = new File(uriString_fotoPB);
                    String path = myFile_fotoPB.getAbsolutePath();
                    String displayName_fotoPB = null;

                    if (uriString_fotoPB.startsWith("content://")) {
                        try (Cursor cursor_fotoPB = LaporPotensiBahaya.this.getContentResolver().query(uriGambarPB_fotoPB, null, null, null, null)) {
                            if (cursor_fotoPB != null && cursor_fotoPB.moveToFirst()) {
                                displayName_fotoPB = cursor_fotoPB.getString(cursor_fotoPB.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                etFotoPB.setText(displayName_fotoPB);
                            }
                        }
                    } else if (uriString_fotoPB.startsWith("file://"))  {
                        displayName_fotoPB = myFile_fotoPB.getName();
                        etFotoPB.setText(displayName_fotoPB);
                    }
                    try {
                        Bitmap bitmap_fotoPB = MediaStore.Images.Media.getBitmap(getContentResolver(),uriGambarPB_fotoPB);
                        etFotoPB.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                        Log.e(String.valueOf(uriGambarPB_fotoPB), "uri");

                    } catch (IOException e){
                        //Log the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    //Method untuk mendapatkan ekstensi file (format file) dari gambar yang dipilih dari file path URI
    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        //return the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImg_Foto_Tanda_Pengenal(){
        //uriGambar dari Galeri
        if (uriGambarPB_fotoTP != null && uriGambarPB_fotoTP.getScheme().equals(ContentResolver.SCHEME_CONTENT)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Tanda Pengenal...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference = storageReference.child(
                                                "imagesPotensiBahaya/"
                                                + documentId_PB + "-Foto_Tanda_Pengenal" + "." + GetFileExtension(uriGambarPB_fotoTP));

            //menambahkan listener untuk upload gambar
            reference.putFile(uriGambarPB_fotoTP)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Tanda Pengenal Potensi Bahaya" + uri);
                                    tandaPengenal = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    if (uriGambarPB_fotoTP != null){
                                        uploadImg_FotoPB();
                                    } else {
                                        Toast.makeText(LaporPotensiBahaya.this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.e(tandaPengenal, "Foto Tanda Pengenal berhasil diunggah BY GALERI");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Tanda Pengenal");
                        }
                    });

        //uriGambar dari Kamera
        } else if (uriGambarPB_fotoTP != null && uriGambarPB_fotoTP.getScheme().equals(ContentResolver.SCHEME_FILE)){
            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Tanda Pengenal...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference = storageReference.child(
                    "imagesPotensiBahaya/"
                            + documentId_PB + "-Foto_Tanda_Pengenal" + "." + "jpg");

            //menambahkan listener untuk upload gambar
            reference.putFile(uriGambarPB_fotoTP)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Tanda Pengenal Potensi Bahaya" + uri);
                                    tandaPengenal = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    if (uriGambarPB_fotoTP != null){
                                        uploadImg_FotoPB();
                                    } else {
                                        Toast.makeText(LaporPotensiBahaya.this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.e(tandaPengenal, "Foto Tanda Pengenal berhasil diunggah BY KAMERA");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Tanda Pengenal");
                        }
                    });

        } else {
            Toast.makeText(this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImg_FotoPB() {
        //uriGambar dari Galeri
        if (uriGambarPB_fotoPB != null && uriGambarPB_fotoPB.getScheme().equals(ContentResolver.SCHEME_CONTENT)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setTitle("Mohon Tunggu...");
            progressDialog1.setMessage("Mengunggah Foto Potensi Bahaya...");
            progressDialog1.show();
            progressDialog1.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference1 = storageReference.child(
                    "imagesPotensiBahaya/"
                            + documentId_PB + "-Foto_PB" + "." + GetFileExtension(uriGambarPB_fotoPB));

            //menambahkan listener untuk upload gambar
            reference1.putFile(uriGambarPB_fotoPB)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Potensi Bahaya" + uri);
                                    gambar = String.valueOf(uri);
                                    progressDialog1.dismiss();
                                    submit_pb();
                                    Log.e(gambar, "gambarPB by GALERI");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog1.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto PB");
                        }
                    });

        //uriGambar dari Kamera
        } else if (uriGambarPB_fotoPB != null && uriGambarPB_fotoPB.getScheme().equals(ContentResolver.SCHEME_FILE)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setTitle("Mohon Tunggu...");
            progressDialog1.setMessage("Mengunggah Foto Potensi Bahaya...");
            progressDialog1.show();
            progressDialog1.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference1 = storageReference.child(
                    "imagesPotensiBahaya/"
                            + documentId_PB + "-Foto_PB" + "." + "jpg");

            //menambahkan listener untuk upload gambar
            reference1.putFile(uriGambarPB_fotoPB)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Potensi Bahaya" + uri);
                                    gambar = String.valueOf(uri);
                                    progressDialog1.dismiss();
                                    submit_pb();
                                    Log.e(gambar, "gambarPB by KAMERA");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog1.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto PB");
                        }
                    });
        } else {
            Toast.makeText(this, "Foto Potensi Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private void getIDPengisi() {
        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Log.d(TAG, "Dokumen Tersedia");
                        idPengisi = document.getString("id_user");
                        namaPengisi = document.getString("name_user");
                        emailPengisi = document.getString("email_user");

                        etNamaPelapor.setText(namaPengisi);
                        etEmailPelapor.setText(emailPengisi);
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
        tglDibuat = format.format(tanggal);
        tglDiupdate = tglDibuat;
    }

    private void submit_pb() {
        kodePotensiBahaya = etKodePotensiBahaya.getText().toString().trim();
        namaPelapor = etNamaPelapor.getText().toString().trim();
        emailPelapor = etEmailPelapor.getText().toString().trim();
        nipNim = etNipNimPelapor.getText().toString().trim();
        nomorTeleponPelapor = etNoTeleponPelapor.getText().toString().trim();
        tglLapor = etTglLapor.getText().toString().trim();
        kategoriPelapor = ddKategoriPelapor.getText().toString().trim();
        institusi = etInstitusiDikunjungi.getText().toString().trim();
        tujuan = etTujuan.getText().toString().trim();
        lokasiDepartemen = ddUnitCivitasAkademika.getText().toString().trim();
        lokasi = etLokasi.getText().toString().trim();
        potensiBahaya = ddPotensiBahaya.getText().toString().trim();
        deskripsiPotensiBahaya = etDeskripsi.getText().toString().trim();
        resikoBahaya = etResikoBahaya.getText().toString().trim();
        usulanPerbaikan = etUsulanPerbaikan.getText().toString().trim();

        if (TextUtils.isEmpty(kodePotensiBahaya)){
            Toast.makeText(this, "Kode Potensi Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(namaPelapor)){
            Toast.makeText(this, "Nama Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(emailPelapor)){
            Toast.makeText(this, "Email Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (!emailPelapor.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
            Toast.makeText(this, "Masukkan Email Pelapor yang valid", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(nipNim)){
            Toast.makeText(this, "NIP/NIM tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(nomorTeleponPelapor)){
            Toast.makeText(this, "Nomor Telepon Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(tglLapor)){
            Toast.makeText(this, "Tanggal Lapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(kategoriPelapor)){
            Toast.makeText(this, "Kategori Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(institusi)){
            Toast.makeText(this, "Institusi yang dikunjungi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(tujuan)){
            Toast.makeText(this, "Tujuan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(lokasiDepartemen)){
            Toast.makeText(this, "Unit Civitas Akademika tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(lokasi)){
            Toast.makeText(this, "Lokasi Potensi Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(potensiBahaya)){
            Toast.makeText(this, "Potensi Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(deskripsiPotensiBahaya)){
            Toast.makeText(this, "Deskripsi Potensi Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(resikoBahaya)){
            Toast.makeText(this, "Resiko Bahaya tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(usulanPerbaikan)){
            Toast.makeText(this, "Usulan Perbaikan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference documentReference = mFirestore.collection("potensiBahayas").document(kodePotensiBahaya);
        Map<String, Object> laporPotensiBahaya = new HashMap<>();
        laporPotensiBahaya.put("kode_potensibahaya", kodePotensiBahaya);
        laporPotensiBahaya.put("tanda_pengenal_pb", tandaPengenal);
        laporPotensiBahaya.put("nama_pelapor_pb", namaPelapor);
        laporPotensiBahaya.put("email_pelapor_pb", emailPelapor);
        laporPotensiBahaya.put("nip_nim_pb", nipNim);
        laporPotensiBahaya.put("nomor_telepon_pelapor_pb", nomorTeleponPelapor);
        laporPotensiBahaya.put("tgl_lapor_pb", tglLapor);
        laporPotensiBahaya.put("kategori_pelapor_pb", kategoriPelapor);
        laporPotensiBahaya.put("institusi_pb", institusi);
        laporPotensiBahaya.put("tujuan_pb", tujuan);
        laporPotensiBahaya.put("lokasi_departemen_pb", lokasiDepartemen);
        laporPotensiBahaya.put("lokasi_pb", lokasi);
        laporPotensiBahaya.put("potensi_bahaya", potensiBahaya);
        laporPotensiBahaya.put("deskripsi_pb", deskripsiPotensiBahaya);
        laporPotensiBahaya.put("resiko_bahaya_pb", resikoBahaya);
        laporPotensiBahaya.put("usulan_perbaikan_pb", usulanPerbaikan);
        laporPotensiBahaya.put("gambar_pb", gambar);
        laporPotensiBahaya.put("id_user", idPengisi);
        laporPotensiBahaya.put("dibuat_pb", tglDibuat);
        laporPotensiBahaya.put("diupdate_pb", tglDiupdate);
        laporPotensiBahaya.put("status_deleted_pb", false);
        laporPotensiBahaya.put("status_laporan_pb", "Pending");
        documentReference.set(laporPotensiBahaya).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reset_pb();
                updateTotalGenerate(totalValueGenerated);
                progressDialog.dismiss();
                onBackPressed();
                Toast.makeText(LaporPotensiBahaya.this, "Berhasil menambahkan Potensi Bahaya", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LaporPotensiBahaya.this, "Gagal menambahkan Potensi Bahaya", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalGenerate(Double totalValueGenerated) {
        Map<String, Object> updateIDPB = new HashMap<>();
        updateIDPB.put("totalPotensiBahaya", totalValueGenerated);
        mGeneratePBValue.set(updateIDPB, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "Berhasil melakukan penambahan ID Dokumen");
                getDataGenerateID_PotensiBahaya();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Gagal melakukan penambahan ID Dokumen");
            }
        });
    }

    private void dialogResetYT_PB() {
        //Dialog Konfirmasi Ya atau Tidak
        DialogInterface.OnClickListener dialogReset = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        reset_pb();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builderReset = new AlertDialog.Builder(this);
        builderReset.setMessage("Reset data?").setPositiveButton("Ya", dialogReset)
                .setNegativeButton("Tidak", dialogReset).show();
    }

    private void reset_pb() {
        etFotoTandaPengenal.getText().clear();
        etNipNimPelapor.getText().clear();
        etNoTeleponPelapor.getText().clear();
        etTglLapor.getText().clear();
        ddKategoriPelapor.setText(null);
        etInstitusiDikunjungi.getText().clear();
        etTujuan.getText().clear();
        ddUnitCivitasAkademika.setText(null);
        etLokasi.getText().clear();
        ddPotensiBahaya.setText(null);
        etDeskripsi.getText().clear();
        etResikoBahaya.getText().clear();
        etUsulanPerbaikan.getText().clear();
        etFotoPB.getText().clear();
        uriGambarPB_fotoTP = null;
        uriGambarPB_fotoPB = null;
        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        etFotoPB.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } if (uriKameraIntent_fotoTP!=null){
            getContentResolver().delete(uriGambarPB_fotoTP,null,null);
            return true;
        } if (uriKameraIntent_fotoPB!=null) {
            getContentResolver().delete(uriGambarPB_fotoPB, null, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}