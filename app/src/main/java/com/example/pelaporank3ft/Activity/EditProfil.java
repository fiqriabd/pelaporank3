package com.example.pelaporank3ft.Activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfil extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 1000;

    private DocumentReference mUserRef;
    private StorageReference storageReference;
    private Uri uriKameraIntent, uriGambar = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private String userId;

    private AlertDialog dialog;
    private Button btnKamera, btnGaleri;
    private CircleImageView imgPengguna;
    private EditText namaPengguna, emailPengguna;
    private ImageButton btnClose;
    private MaterialButton btnUbahFoto, btnSimpan;
    private String imgUser, namaUser, tglDiupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mUserRef = mFirestore.collection("users").document(userId);

        initView();
        getDataPengguna();
        getTanggal();
    }

    private void initView() {
        imgPengguna = findViewById(R.id.edit_foto_profil);
        namaPengguna = findViewById(R.id.et_nama_edit_profil);
        emailPengguna = findViewById(R.id.et_ubah_imel);
        btnUbahFoto = findViewById(R.id.btn_ubah_foto);
        btnSimpan = findViewById(R.id.btn_ubah_profil_activity);
    }

    private void getDataPengguna() {
        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        String imgProfile = document.getString("img_user");
                        if (imgProfile != null ) {
                            Log.d(TAG, "Dokumen tersedia!");
                            Glide.with(imgPengguna.getContext())
                                    .load(document.getString("img_user"))
                                    .into(imgPengguna);
                        } else {
                            Glide.with(imgPengguna.getContext())
                                    .load(getApplicationContext().getDrawable(R.drawable.avatar))
                                    .into(imgPengguna);
                        }
                        imgUser = document.getString("img_user");
                        namaPengguna.setText(document.getString("name_user"));
                        emailPengguna.setText(document.getString("email_user"));

                        btnUbahFoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ambilGambarUser();
                            }
                        });

                        btnSimpan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (uriGambar != null){
                                    uploadImg();
                                } else {
                                    updateProfil();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Dokumen tidak tersedia!");
                    }
                } else {
                    Log.d(TAG, "Gagal menampilkan data: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_LONG).show();
                takeFoto();
            } else {
                Toast.makeText(this, "Izin kamera ditolak, mohon berikan izin akses kamera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ambilGambarUser() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfil.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_menu_gambar, null);
        builder.setView(view);
        btnClose = view.findViewById(R.id.btn_close_gambar);
        btnKamera = view.findViewById(R.id.btn_kamera);
        btnGaleri = view.findViewById(R.id.btn_galeri);

        dialog = builder.create();
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
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                } else {
                    takeFoto();
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

                launchSelectImg.launch(intent);
                dialog.dismiss();
            }
        });
    }

    private void takeFoto() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriKameraIntent = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, uriKameraIntent);
        intentKamera.resolveActivity(getPackageManager());
        startKamera.launch(intentKamera);
    }

    ActivityResultLauncher<Intent> startKamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {

                try {
                    String[] koloms = {MediaStore.Images.ImageColumns.DATA};
                    Cursor kursorKamera = getContentResolver().query(Uri.parse(uriKameraIntent.toString()), koloms, null, null, null);
                    kursorKamera.moveToFirst();

                    String kameraPath = kursorKamera.getString(0);
                    kursorKamera.close();

                    File fileKamera = new File(kameraPath);
                    uriGambar = Uri.fromFile(fileKamera);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(EditProfil.this.getContentResolver(), uriGambar);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    imgPengguna.setImageBitmap(handleSamplingRotationBitmap(uriGambar));
                    Log.e(String.valueOf(uriGambar), "uri");

                } catch (CursorIndexOutOfBoundsException | IOException ex) {
                    ex.printStackTrace();

                    String[] koloms = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED};
                    Cursor kursors = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, koloms, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC");

                    int columnIndex = kursors.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    kursors.moveToFirst();

                    String pathKamera = kursors.getString(columnIndex);
                    kursors.close();

                    File kameraFile = new File(pathKamera);
                    uriGambar = Uri.fromFile(kameraFile);

                    Bitmap bitmapKamera = null;

                    try {
                        bitmapKamera = MediaStore.Images.Media.getBitmap(EditProfil.this.getContentResolver(), uriGambar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgPengguna.setImageBitmap(bitmapKamera);
                    Log.e(String.valueOf(uriGambar), "uri");
                }
            } else {
                getContentResolver().delete(uriKameraIntent,null,null);
            }
        }
    });

    private Bitmap handleSamplingRotationBitmap(Uri uriKameraSelected) throws IOException {
        int MAX_TINGGI = 1024;
        int MAX_LEBAR = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options opsi = new BitmapFactory.Options();
        opsi.inJustDecodeBounds = true;
        InputStream gambarStream = getContentResolver().openInputStream(uriKameraSelected);
        BitmapFactory.decodeStream(gambarStream, null, opsi);
        gambarStream.close();

        //Calculate inSampleSize
        opsi.inSampleSize = kalkulasiInUkuranSample(opsi, MAX_LEBAR, MAX_TINGGI);

        //Decode bitmap with inSampleSize set
        opsi.inJustDecodeBounds = false;
        gambarStream = getContentResolver().openInputStream(uriKameraSelected);
        Bitmap imgKamera = BitmapFactory.decodeStream(gambarStream, null, opsi);
        if (imgKamera != null){
            imgKamera = rotasiGambarJikaDiperlukan(imgKamera, uriKameraSelected);
        } else {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            return null;
        }
        return imgKamera;
    }

    private int kalkulasiInUkuranSample(BitmapFactory.Options opsi, int reqLebar, int reqTinggi) {
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

    private Bitmap rotasiGambarJikaDiperlukan(Bitmap imgKamera, Uri uriKameraSelected) throws IOException {
        ExifInterface ei = new ExifInterface(uriKameraSelected.getPath());

        switch (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotasiGambar(imgKamera, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotasiGambar(imgKamera, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotasiGambar(imgKamera, 270);
            default:
                return imgKamera;
        }
    }

    private Bitmap rotasiGambar(Bitmap imgKamera, int derajat) {
        Matrix matriks = new Matrix();
        matriks.postRotate((float) derajat);
        Bitmap rotatedImg = Bitmap.createBitmap(imgKamera, 0, 0, imgKamera.getWidth(), imgKamera.getHeight(), matriks, true);
        imgKamera.recycle();
        return rotatedImg;
    }

    ActivityResultLauncher<Intent> launchSelectImg= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("Range")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();

                if (data != null && data.getData() != null){
                    uriGambar = data.getData();
                    String uriString = uriGambar.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        try (Cursor cursor = EditProfil.this.getContentResolver().query(uriGambar, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        }
                    } else if (uriString.startsWith("file://"))  {
                        displayName = myFile.getName();
                    }
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriGambar);
                        imgPengguna.setImageBitmap(bitmap);
                        Log.e(String.valueOf(uriGambar), "uri");
                    } catch (IOException e){
                        //Log the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    private String GetFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //return the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImg() {
        //uriGambar dari Galeri
        if (uriGambar != null && uriGambar.getScheme().equals(ContentResolver.SCHEME_CONTENT)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Profil...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference = storageReference.child(
                    "imagesFotoProfil/"
                            + userId + "." + GetFileExtension(uriGambar));

            //Menambahkan listener untuk upload gambar
            reference.putFile(uriGambar)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Profil" + uri);
                                    imgUser = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    updateProfil();
                                    Log.e(imgUser, "Foto Profil berhasil diunggah berhasil diunggah BY GALERY");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah Gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Profil");
                        }
                    });

            //uriGambar dari Kamera
        } else if (uriGambar != null && uriGambar.getScheme().equals(ContentResolver.SCHEME_FILE)){

            //Menampilkan progress dialog ketika foto diupload
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mohon Tunggu...");
            progressDialog.setMessage("Mengunggah Foto Profil...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            //Defining the child of storageReference / lokasi file disimpan didalam Firestore Storage
            final StorageReference reference = storageReference.child(
                    "imagesFotoProfil/"
                            + userId + "." + "jpg");

            //Menambahkan listener untuk upload gambar
            reference.putFile(uriGambar)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Jika berhasil mengunggah gambar
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "Foto Profil" + uri);
                                    imgUser = String.valueOf(uri);
                                    progressDialog.dismiss();
                                    updateProfil();
                                    Log.e(imgUser, "Foto Profil berhasil diunggah berhasil diunggah BY KAMERA");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Gagal mengunggah Gambar
                            progressDialog.dismiss();
                            Log.w(TAG, "Gagal mengunggah Foto Profil");
                        }
                    });
        } else {
            Log.d(TAG, "Foto Profil kosong");
        }
    }

    private void getTanggal() {
        Locale lokal = new Locale("in", "ID");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", lokal);
        Date tanggal = new Date();
        tglDiupdate = format.format(tanggal);
    }

    private void updateProfil() {
        namaUser = namaPengguna.getText().toString().trim();

        if (TextUtils.isEmpty(namaUser)){
            Toast.makeText(this, "Name Pengguna tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("img_user", imgUser);
        updateUser.put("name_user", namaUser);
        updateUser.put("diupdate_user", tglDiupdate);

        mUserRef.set(updateUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                finish();
                Toast.makeText(EditProfil.this, "Data Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditProfil.this, "Gagal memperbarui Data Profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (uriKameraIntent!=null){
            getContentResolver().delete(uriGambar,null,null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}