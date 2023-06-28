package com.example.pelaporank3ft.Activity.Insiden;

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

public class LaporInsiden extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE_KEJADIAN = 200;
    private static final int MY_CAMERA_REQUEST_CODE_TP = 300;

    private DocumentReference mGenerateInsidenValue, mUserRef;
    private Double totalValueGenerated;
    private StorageReference storageReference;
    private Uri uriKameraIntent_fotoKejadian, uriGambar_fotoKejadian = null,
            uriKameraIntent_fotoTP, uriGambar_fotoTP = null;

    private DatePickerDialog waktuKejadianInsiden;
    private SimpleDateFormat formatTanggalInsiden;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;

    private AutoCompleteTextView ddInsiden,ddLokasi,ddPenyebabInsiden;
    private EditText etKodeLaporInsiden, etWaktuKejadian, etLokasiRinci, etKronologi, etFotoKejadian,
            etFotoTandaPengenal, etNamaPelapor, etEmailPelapor, etNoTeleponPelapor, etUnitPelapor,
            etNamaKorban, etEmailKorban, etNoTeleponKorban, etUnitKorban;
    private String kodeLaporInsiden, tandaPengenal, waktuKejadian, lokasiDepartemen, lokasiRinci,
            jenisInsiden, kronologi, penyebabInsiden, namaPelapor, emailPelapor, nomorTeleponPelapor,
            unitPelapor, namaKorban, emailKorban, nomerTeleponKorban, unitKorban, gambarKejadian;
    private String documentId_Insiden, userId, idPengisi, tglDibuat, tglDiupdate, namaPengisi, emailPengisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_insiden);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mUserRef = mFirestore.collection("users").document(userId);
        mGenerateInsidenValue = mFirestore.collection("totalValueGenerated").document("totalValueGenerated");
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        initView();
        getDataGenerateID_Insiden();
        getIDPengisi();
        getTanggal();
    }

    private void initView() {
        etKodeLaporInsiden = findViewById(R.id.et_kode_lapor_insiden);
        etWaktuKejadian = findViewById(R.id.et_kolom_waktu_kejadian_insiden);
        ddLokasi = findViewById(R.id.ddlokasi_insiden);
        etLokasiRinci = findViewById(R.id.et_kolom_lokasi_rinci_insiden);
        ddInsiden = findViewById(R.id.ddinsiden);
        etKronologi = findViewById(R.id.et_kolom_kronologi_insiden);
        ddPenyebabInsiden = findViewById(R.id.ddpenyebabinsiden);
        etFotoKejadian = findViewById(R.id.et_foto_kejadian_insiden);
        etFotoTandaPengenal = findViewById(R.id.et_foto_tanda_pengenal_insiden);
        etNamaPelapor = findViewById(R.id.et_kolom_nama_pelapor_insiden);
        etEmailPelapor = findViewById(R.id.et_kolom_email_pelapor_insiden);
        etNoTeleponPelapor = findViewById(R.id.et_kolom_notelepon_pelapor_insiden);
        etUnitPelapor = findViewById(R.id.et_kolom_unit_pelapor_insiden);
        etNamaKorban = findViewById(R.id.et_kolom_nama_korban_insiden);
        etEmailKorban = findViewById(R.id.et_kolom_email_korban_insiden);
        etNoTeleponKorban = findViewById(R.id.et_kolom_notelepon_korban_insiden);
        etUnitKorban = findViewById(R.id.et_kolom_unit_korban_insiden);

        //Format Tanggal
        formatTanggalInsiden = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        //Klik untuk menampilkan tanggal
        etWaktuKejadian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkan_tanggal_insiden();
            }

            //Menampilkan tanggal
            private void tampilkan_tanggal_insiden() {
                Calendar calendar_insiden = Calendar.getInstance();
                waktuKejadianInsiden = new DatePickerDialog(LaporInsiden.this, (view, year, month, dayOfMonth) -> {
                    Calendar tglInsiden = Calendar.getInstance();
                    tglInsiden.set(year, month, dayOfMonth);
                    etWaktuKejadian.setText(formatTanggalInsiden.format(tglInsiden.getTime()));
                }, calendar_insiden.get(Calendar.YEAR), calendar_insiden.get(Calendar.MONTH), calendar_insiden.get(Calendar.DAY_OF_MONTH));
                waktuKejadianInsiden.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                waktuKejadianInsiden.show();
            }
        });

        //Pilihan pada Lokasi (Fakultas Teknik)
        String[] arrayfakultasteknik = getResources().getStringArray(R.array.departemenft);
        ArrayAdapter<String> arrayft = new ArrayAdapter<>(this, R.layout.dropdown_item, arrayfakultasteknik);
        ddLokasi.setText(arrayft.getItem(0),false);
        ddLokasi.setText("");
        ddLokasi.setAdapter(arrayft);

        //Pilihan pada Jenis Insiden
        String[] arrayjenisinsiden = getResources().getStringArray(R.array.jenisinsiden);
        ArrayAdapter<String> arrayinsiden = new ArrayAdapter<>(this, R.layout.dropdown_item, arrayjenisinsiden);
        ddInsiden.setText(arrayinsiden.getItem(0), false);
        ddInsiden.setText("");
        ddInsiden.setAdapter(arrayinsiden);

        //Pilihan pada Penyebab Insiden
        String[] arraypilihpenyebab = getResources().getStringArray(R.array.pilihpenyebab);
        ArrayAdapter<String> arraypenyebab = new ArrayAdapter<>(this, R.layout.dropdown_item, arraypilihpenyebab);
        ddPenyebabInsiden.setText(arraypenyebab.getItem(0), false);
        ddPenyebabInsiden.setText("");
        ddPenyebabInsiden.setAdapter(arraypenyebab);

        etFotoKejadian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilGambar_FotoKejadian();
            }
        });

        etFotoTandaPengenal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilGambar_FotoTP();
            }
        });

        //Button Submit
        MaterialButton btn_submit_insiden = findViewById(R.id.btn_submit_insiden);
        btn_submit_insiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriGambar_fotoKejadian != null){
                    UploadGambar_FotoKejadian();
                } else {
                    Toast.makeText(LaporInsiden.this, "Foto Kejadian tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button Reset
        MaterialButton btn_reset_insiden = findViewById(R.id.btn_reset_insiden);
        btn_reset_insiden.setOnClickListener(view -> dialogResetYT_Insiden());
    }

    private void getDataGenerateID_Insiden() {
        mGenerateInsidenValue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document tersedia!");
                        totalValueGenerated = document.getDouble("totalLaporInsiden");
                        Log.d(TAG, String.valueOf(totalValueGenerated));
                        totalValueGenerated +=1;
                        documentId_Insiden = "INSDN-"+ String.format("%03d", Math.round(totalValueGenerated));
                        etKodeLaporInsiden.setText(documentId_Insiden);
                    } else {
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
        if (requestCode == MY_CAMERA_REQUEST_CODE_KEJADIAN) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_LONG).show();
                takeFoto_fotoKejadian();
            } else {
                Toast.makeText(this, "Izin kamera ditolak, mohon berikan izin akses kamera", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_CAMERA_REQUEST_CODE_TP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_LONG).show();
                takeFoto_fotoTP();
            } else {
                Toast.makeText(this, "Izin kamera ditolak, mohon berikan izin akses kamera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ambilGambar_FotoKejadian() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LaporInsiden.this);
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
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE_KEJADIAN);
                } else {
                    takeFoto_fotoKejadian();
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
                launchFK_InsidenActivity.launch(intent);
                dialog.dismiss();
            }
        });
    }

    private void takeFoto_fotoKejadian() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriKameraIntent_fotoKejadian = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, uriKameraIntent_fotoKejadian);
        intentKamera.resolveActivity(getPackageManager());
        startKamera_fotoKejadian.launch(intentKamera);
    }

    ActivityResultLauncher<Intent> startKamera_fotoKejadian = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {

                try {
                    String[] koloms = {MediaStore.Images.ImageColumns.DATA};
                    Cursor kursorKamera = getContentResolver().query(Uri.parse(uriKameraIntent_fotoKejadian.toString()), koloms, null, null, null);
                    kursorKamera.moveToFirst();

                    String kameraPath = kursorKamera.getString(0);
                    kursorKamera.close();

                    String namaFile = "Foto Kejadian.jpg";

                    File fileKamera = new File(kameraPath);
                    uriGambar_fotoKejadian = Uri.fromFile(fileKamera);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporInsiden.this.getContentResolver(), uriGambar_fotoKejadian);
                        etFotoKejadian.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    etFotoKejadian.setText(namaFile);
                    bitmapKamera = handleSamplingRotationBitmap_fotoKejadian(uriGambar_fotoKejadian);
                    Log.e(String.valueOf(uriGambar_fotoKejadian), "uri");

                } catch (CursorIndexOutOfBoundsException | IOException ex) {
                    ex.printStackTrace();

                    String[] koloms = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED};
                    Cursor kursors = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, koloms, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC");

                    int columnIndex = kursors.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    kursors.moveToFirst();

                    String pathKamera = kursors.getString(columnIndex);
                    kursors.close();

                    String namaFile = "Foto Kejadian.jpg";

                    File kameraFile = new File(pathKamera);
                    uriGambar_fotoKejadian = Uri.fromFile(kameraFile);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporInsiden.this.getContentResolver(), uriGambar_fotoKejadian);
                        etFotoKejadian.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    etFotoKejadian.setText(namaFile);
                    Log.e(String.valueOf(uriGambar_fotoKejadian), "uri");
                }
            } else {
                getContentResolver().delete(uriKameraIntent_fotoKejadian,null,null);
            }
        }
    });

    private Bitmap handleSamplingRotationBitmap_fotoKejadian(Uri uriKameraSelected_fotoKejadian) throws IOException {
        int MAX_TINGGI = 1024;
        int MAX_LEBAR = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options opsi = new BitmapFactory.Options();
        opsi.inJustDecodeBounds = true;
        InputStream gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoKejadian);
        BitmapFactory.decodeStream(gambarStream, null, opsi);
        gambarStream.close();

        //Calculate inSampleSize
        opsi.inSampleSize = kalkulasiInUkuranSample_FotoKejadian(opsi, MAX_LEBAR, MAX_TINGGI);

        //Decode bitmap with inSampleSize set
        opsi.inJustDecodeBounds = false;
        gambarStream = getContentResolver().openInputStream(uriKameraSelected_fotoKejadian);
        Bitmap imgKamera = BitmapFactory.decodeStream(gambarStream, null, opsi);
        if (imgKamera != null){
            imgKamera = rotasiGambarJikaDiperlukan_FotoKejadian(imgKamera, uriKameraSelected_fotoKejadian);
        } else {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            return null;
        }
        return imgKamera;
    }

    private int kalkulasiInUkuranSample_FotoKejadian(BitmapFactory.Options opsi, int reqLebar, int reqTinggi) {
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

    private Bitmap rotasiGambarJikaDiperlukan_FotoKejadian(Bitmap imgKamera, Uri uriKameraSelected_fotoKejadian) throws IOException {
        ExifInterface ei = new ExifInterface(uriKameraSelected_fotoKejadian.getPath());

        switch (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotasiGambar_fotoKejadian(imgKamera, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotasiGambar_fotoKejadian(imgKamera, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotasiGambar_fotoKejadian(imgKamera, 270);
            default:
                return imgKamera;
        }
    }

    private Bitmap rotasiGambar_fotoKejadian(Bitmap imgKamera, int derajat) {
        Matrix matriks = new Matrix();
        matriks.postRotate((float) derajat);
        Bitmap rotatedImg = Bitmap.createBitmap(imgKamera, 0, 0, imgKamera.getWidth(), imgKamera.getHeight(), matriks, true);
        imgKamera.recycle();
        return rotatedImg;
    }

    private void ambilGambar_FotoTP() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LaporInsiden.this);
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
                    takeFoto_fotoTP();
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
                launchFTP_InsidenActivity.launch(intent);
                dialog.dismiss();
            }
        });
    }

    private void takeFoto_fotoTP() {
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
                    uriGambar_fotoTP = Uri.fromFile(fileKamera);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporInsiden.this.getContentResolver(), uriGambar_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    etFotoTandaPengenal.setText(namaFile);
                    bitmapKamera = handleSamplingRotationBitmap_fotoTP(uriGambar_fotoTP);
                    Log.e(String.valueOf(uriGambar_fotoTP), "uri");

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
                    uriGambar_fotoTP = Uri.fromFile(kameraFile);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(LaporInsiden.this.getContentResolver(), uriGambar_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    etFotoTandaPengenal.setText(namaFile);
                    Log.e(String.valueOf(uriGambar_fotoTP), "uri");
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

    ActivityResultLauncher<Intent> launchFK_InsidenActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();

                if (data != null && data.getData() != null){
                    uriGambar_fotoKejadian = data.getData();
                    String uriString_fotoKejadian = uriGambar_fotoKejadian.toString();
                    File myFile_fotoKejadian = new File(uriString_fotoKejadian);
                    String path = myFile_fotoKejadian.getAbsolutePath();
                    String displayName_fotoKejadian = null;

                    if (uriString_fotoKejadian.startsWith("content://")){
                        try (Cursor cursor_fotoKejadian = LaporInsiden.this.getContentResolver().query(uriGambar_fotoKejadian, null, null, null, null)) {
                            if (cursor_fotoKejadian != null && cursor_fotoKejadian.moveToFirst()) {
                                displayName_fotoKejadian = cursor_fotoKejadian.getString(cursor_fotoKejadian.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                etFotoKejadian.setText(displayName_fotoKejadian);
                            }
                        }
                    } else if (uriString_fotoKejadian.startsWith("file://")) {
                        displayName_fotoKejadian = myFile_fotoKejadian.getName();
                        etFotoKejadian.setText(displayName_fotoKejadian);
                    }
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriGambar_fotoKejadian);
                        etFotoKejadian.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                        Log.e(String.valueOf(uriGambar_fotoKejadian), "uri");
                    } catch (IOException e){
                        //Log the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    ActivityResultLauncher<Intent> launchFTP_InsidenActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();

                if (data != null && data.getData() != null){
                    uriGambar_fotoTP = data.getData();
                    String uriString_fotoTP = uriGambar_fotoTP.toString();
                    File myFile_fotoTP = new File(uriString_fotoTP);
                    String path = myFile_fotoTP.getAbsolutePath();
                    String displayName_fotoTP = null;

                    if (uriString_fotoTP.startsWith("content://")){
                        try (Cursor cursor_fotoTP = LaporInsiden.this.getContentResolver().query(uriGambar_fotoTP, null, null, null, null)) {
                            if (cursor_fotoTP != null && cursor_fotoTP.moveToFirst()) {
                                displayName_fotoTP = cursor_fotoTP.getString(cursor_fotoTP.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                etFotoTandaPengenal.setText(displayName_fotoTP);
                            }
                        }
                    } else if (uriString_fotoTP.startsWith("file://")) {
                        displayName_fotoTP = myFile_fotoTP.getName();
                        etFotoTandaPengenal.setText(displayName_fotoTP);
                    }
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriGambar_fotoTP);
                        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_img), null);
                        Log.e(String.valueOf(uriGambar_fotoTP), "uri");
                    } catch (IOException e){
                        //Log the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadGambar_FotoKejadian() {
        //uriGambar dari Galeri
        if (uriGambar_fotoKejadian != null && uriGambar_fotoKejadian.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Kejadian...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference ref = storageReference.child(
                    "imagesLaporInsiden/"
                            + documentId_Insiden + "-Foto_Kejadian" + "." + GetFileExtension(uriGambar_fotoKejadian));

            //menambahkan listener untuk upload gambar
            ref.putFile(uriGambar_fotoKejadian)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Kejadian Insiden" + uri);
                                    gambarKejadian = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    if (uriGambar_fotoTP != null){
                                        UploadGambar_FotoTandaPengenal();
                                    } else {
                                        Toast.makeText(LaporInsiden.this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.e(gambarKejadian, "Foto Kejadian Insiden berhasil diunggah BY GALERI");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Kejadian Insiden");
                        }
                    });

            //uriGambar dari Kamera
        } else if (uriGambar_fotoKejadian != null && uriGambar_fotoKejadian.getScheme().equals(ContentResolver.SCHEME_FILE)){
            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Kejadian...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference ref = storageReference.child(
                    "imagesLaporInsiden/"
                            + documentId_Insiden + "-Foto_Kejadian" + "." + "jpg");

            //menambahkan listener untuk upload gambar
            ref.putFile(uriGambar_fotoKejadian)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Kejadian Insiden" + uri);
                                    gambarKejadian = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    if (uriGambar_fotoTP != null){
                                        UploadGambar_FotoTandaPengenal();
                                    } else {
                                        Toast.makeText(LaporInsiden.this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.e(gambarKejadian, "Foto Kejadian Insiden berhasil diunggah BY KAMERA");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Kejadian Insiden");
                        }
                    });
        } else {
            Toast.makeText(this, "Foto Kejadian tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadGambar_FotoTandaPengenal() {
        //uriGambar dari Galeri
        if (uriGambar_fotoTP != null && uriGambar_fotoTP.getScheme().equals(ContentResolver.SCHEME_CONTENT)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setTitle("Mohon Tunggu...");
            progressDialog1.setMessage("Mengunggah Foto Tanda Pengenal...");
            progressDialog1.show();
            progressDialog1.setCanceledOnTouchOutside(false);

            // Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference ref1 = storageReference.child(
                    "imagesLaporInsiden/"
                            + documentId_Insiden + "-Foto_Tanda_Pengenal" + "." + GetFileExtension(uriGambar_fotoTP));

            //menambahkan listener untuk upload gambar
            ref1.putFile(uriGambar_fotoTP)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            ref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Tanda Pengenal Insiden" + uri);
                                    tandaPengenal = String.valueOf(uri);
                                    progressDialog1.dismiss();
                                    submit_insiden();
                                    Log.e(tandaPengenal, "Foto Tanda Pengenal Insiden berhasil diunggah");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog1.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Tanda Pengenal Insiden");
                        }
                    });

            //uriGambar dari Kamera
        } else if(uriGambar_fotoTP != null && uriGambar_fotoTP.getScheme().equals(ContentResolver.SCHEME_FILE)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setTitle("Mohon Tunggu...");
            progressDialog1.setMessage("Mengunggah Foto Tanda Pengenal...");
            progressDialog1.show();
            progressDialog1.setCanceledOnTouchOutside(false);

            // Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference ref1 = storageReference.child(
                    "imagesLaporInsiden/"
                            + documentId_Insiden + "-Foto_Tanda_Pengenal" + "." + "jpg");

            //menambahkan listener untuk upload gambar
            ref1.putFile(uriGambar_fotoTP)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            ref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Tanda Pengenal Insiden" + uri);
                                    tandaPengenal = String.valueOf(uri);
                                    progressDialog1.dismiss();
                                    submit_insiden();
                                    Log.e(tandaPengenal, "Foto Tanda Pengenal Insiden berhasil diunggah BY KAMERA");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah gambar
                            progressDialog1.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Tanda Pengenal Insiden");
                        }
                    });
        } else {
            Toast.makeText(this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
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

    private void submit_insiden() {
        kodeLaporInsiden = etKodeLaporInsiden.getText().toString().trim();
        waktuKejadian = etWaktuKejadian.getText().toString().trim();
        lokasiDepartemen = ddLokasi.getText().toString().trim();
        lokasiRinci = etLokasiRinci.getText().toString().trim();
        jenisInsiden = ddInsiden.getText().toString().trim();
        kronologi = etKronologi.getText().toString().trim();
        penyebabInsiden = ddPenyebabInsiden.getText().toString().trim();
        namaPelapor = etNamaPelapor.getText().toString().trim();
        emailPelapor = etEmailPelapor.getText().toString().trim();
        nomorTeleponPelapor = etNoTeleponPelapor.getText().toString().trim();
        unitPelapor = etUnitPelapor.getText().toString().trim();
        namaKorban = etNamaKorban.getText().toString().trim();
        emailKorban = etEmailKorban.getText().toString().trim();
        nomerTeleponKorban = etNoTeleponKorban.getText().toString().trim();
        unitKorban = etUnitKorban.getText().toString().trim();

        if (TextUtils.isEmpty(kodeLaporInsiden)) {
            Toast.makeText(this, "Kode Lapor Insiden tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(waktuKejadian)){
            Toast.makeText(this, "Waktu Kejadian tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (lokasiDepartemen.isEmpty()){
            Toast.makeText(this, "Lokasi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(lokasiRinci)){
            Toast.makeText(this, "Lokasi Rinci tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(jenisInsiden)){
            Toast.makeText(this, "Jenis Insiden tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(kronologi)){
            Toast.makeText(this, "Kronologi Kejadian tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(penyebabInsiden)){
            Toast.makeText(this, "Penyebab Insiden tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(gambarKejadian)){
            Toast.makeText(this, "Foto Kejadian tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(tandaPengenal)){
            Toast.makeText(this, "Foto Tanda Pengenal tidak boleh kosong", Toast.LENGTH_SHORT).show();
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
        }else if (TextUtils.isEmpty(nomorTeleponPelapor)){
            Toast.makeText(this, "Nomor Telepon Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(unitPelapor)){
            Toast.makeText(this, "Unit Pelapor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference documentReference = mFirestore.collection("laporInsidens").document(kodeLaporInsiden);
        Map<String, Object> laporInsiden = new HashMap<>();
        laporInsiden.put("kode_laporinsiden", kodeLaporInsiden);
        laporInsiden.put("waktu_kejadian_insiden", waktuKejadian);
        laporInsiden.put("lokasi_departemen_insiden", lokasiDepartemen);
        laporInsiden.put("lokasi_rinci_insiden", lokasiRinci);
        laporInsiden.put("jenis_insiden", jenisInsiden);
        laporInsiden.put("kronologi_insiden", kronologi);
        laporInsiden.put("penyebab_insiden", penyebabInsiden);
        laporInsiden.put("gambar_insiden", gambarKejadian);
        laporInsiden.put("tanda_pengenal_insiden", tandaPengenal);
        laporInsiden.put("nama_pelapor_insiden", namaPelapor);
        laporInsiden.put("email_pelapor_insiden", emailPelapor);
        laporInsiden.put("nomor_telepon_pelapor_insiden", nomorTeleponPelapor);
        laporInsiden.put("unit_pelapor_insiden", unitPelapor);
        laporInsiden.put("nama_korban_insiden", namaKorban);
        laporInsiden.put("email_korban_insiden", emailKorban);
        laporInsiden.put("nomer_telepon_korban_insiden", nomerTeleponKorban);
        laporInsiden.put("unit_korban_insiden", unitKorban);
        laporInsiden.put("id_user", idPengisi);
        laporInsiden.put("dibuat_insiden", tglDibuat);
        laporInsiden.put("diupdate_insiden", tglDiupdate);
        laporInsiden.put("status_deleted_insiden", false);
        laporInsiden.put("status_laporan_insiden", "Pending");
        laporInsiden.put("kategori_insiden", "");
        laporInsiden.put("tenggat_waktu_insiden", "");
        laporInsiden.put("tindakan_insiden", "");
        laporInsiden.put("id_p2k3_insiden","");
        laporInsiden.put("nama_p2k3_insiden", "");
        documentReference.set(laporInsiden).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reset_insiden();
                updateTotalGenerate(totalValueGenerated);
                progressDialog.dismiss();
                onBackPressed();
                Toast.makeText(LaporInsiden.this, "Berhasil menambahkan Insiden", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LaporInsiden.this, "Gagal menambahkan Insiden", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void updateTotalGenerate(Double totalValueGenerated) {
        Map<String, Object> updateIDInsiden = new HashMap<>();
        updateIDInsiden.put("totalLaporInsiden", totalValueGenerated);
        mGenerateInsidenValue.set(updateIDInsiden, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "Berhasil melakukan penambahan pada total ID dokumen");
                getDataGenerateID_Insiden();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Gagal melakukan penambahan pada total ID dokumen", e);
            }
        });
    }

    private void dialogResetYT_Insiden() {
        //Dialog Konfirmasi Ya atau Tidak
        DialogInterface.OnClickListener dialogReset = (dialogInterface, i) -> {
            switch (i){
                case DialogInterface.BUTTON_POSITIVE:
                    reset_insiden();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialogInterface.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builderReset = new AlertDialog.Builder(this);
        builderReset.setMessage("Reset data?").setPositiveButton("Ya", dialogReset)
                .setNegativeButton("Tidak", dialogReset).show();
    }

    private void reset_insiden() {
        etWaktuKejadian.getText().clear();
        ddLokasi.setText(null);
        etLokasiRinci.getText().clear();
        ddInsiden.setText(null);
        etKronologi.getText().clear();
        ddPenyebabInsiden.setText(null);
        etFotoKejadian.getText().clear();
        etFotoTandaPengenal.getText().clear();
        etNoTeleponPelapor.getText().clear();
        etUnitPelapor.getText().clear();
        etNamaKorban.getText().clear();
        etEmailKorban.getText().clear();
        etNoTeleponKorban.getText().clear();
        etUnitKorban.getText().clear();
        uriGambar_fotoKejadian = null;
        uriGambar_fotoTP = null;
        etFotoKejadian.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        etFotoTandaPengenal.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } if (uriKameraIntent_fotoKejadian!=null){
            getContentResolver().delete(uriGambar_fotoKejadian,null,null);
            return true;
        } if (uriKameraIntent_fotoTP!=null) {
            getContentResolver().delete(uriGambar_fotoTP, null, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}