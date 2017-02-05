package com.fireblaze.evento.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityUploadImageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = UploadImageActivity.class.getName();
    private ActivityUploadImageBinding binding;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private Bitmap mainImageBitmap;
    private static final int  REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE = 1;
    private final int REQ_SELECT_IMAGE = 2;
    public static final String UPLOAD_PATH_KEYWORD = "uploadPath";
    public static final String DOWNLOAD_URL_RESULT = "downloadUrl";
    public static final String REQ_UPLOAD_IMAGE = "uploadImage";

    private String path;
    private boolean uploadUserImage = false;


    @Override
    public View getContainer() {
        return binding.activityUploadImage;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_select_image:
                getImageFromGallery();
                break;
            case R.id.btn_upload_image:
                if(isStoragePermissionGranted())
                    uploadImage(getImageUri(UploadImageActivity.this,mainImageBitmap));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_upload_image);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        Bundle b = getIntent().getExtras();
        if(b==null){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            path = b.getString(UPLOAD_PATH_KEYWORD, "");
        }
        binding.btnUploadImage.setEnabled(false);

        binding.btnSelectImage.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
        Log.d(TAG, "onCreate: Data = "+getIntent().getDataString());
    }
    private void uploadImage(Uri fileUri){
        if(fileUri == null){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mainImageBitmap != null){
            showProgressDialog();
            StorageReference ref =  mStorage.child(path);
            ref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: upload succeeded");
                    Uri imagePath = taskSnapshot.getMetadata().getDownloadUrl();
                    Intent i = new Intent();
                    i.putExtra(DOWNLOAD_URL_RESULT,imagePath.toString());
                    setResult(RESULT_OK,i);
                    hideProgressDialog();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(UploadImageActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public boolean isStoragePermissionGranted(){
        if(Build.VERSION.SDK_INT >=23)
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "isStoragePermissionGranted: permission granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE);
                return false;
            }
        else {

            // Automatically allowed permission if sdk <=23
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE ){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //resume the task of uploading image
                Log.d(TAG, "onRequestPermissionsResult: Permission granted, resuming task");
                if(mainImageBitmap != null)
                uploadImage(getImageUri(this, mainImageBitmap));
            }else {
                Toast.makeText(this,"Need to grant permission to use external storage",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Uri getImageUri(Context context, Bitmap inImage){
        if(inImage == null) {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "tempImage", null);
        return Uri.parse(path);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_SELECT_IMAGE:
                if(resultCode == RESULT_OK && data != null){
                    //Get image from data
                    Uri selectedImage = data.getData();
                    if(selectedImage != null){
                        try {
                            mainImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            binding.image.setImageBitmap(mainImageBitmap);
                            binding.btnUploadImage.setEnabled(true);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
    private void getImageFromGallery(){
        Intent galleryIntent = new  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,REQ_SELECT_IMAGE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
