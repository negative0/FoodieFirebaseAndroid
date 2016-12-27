package com.fireblaze.evento.activities;

import android.Manifest;
import android.content.Context;
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
import android.view.WindowManager;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityNewEventBinding;
import com.fireblaze.evento.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class NewEventActivity extends BaseActivity {
    public static final String TAG = NewEventActivity.class.getName();
    private DatabaseReference mDatabase;
    ActivityNewEventBinding binding;
    private final int REQ_SELECT_IMAGE = 2;
    private final int REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE = 3;
    private StorageReference mStorage;

    private Bitmap mainImageBitmap;

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_event);
        getViews();
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        
     }
    private void submitForm(){
        if(!validateName())
            return;
        if(!validateDescription())
            return;
        if(!validateVenue())
            return;
        if(!validatePrize())
            return;
        if(!validateCategory())
            return;
        if(!validateFees())
            return;
        if(!validateDuration())
            return;
        if(isStoragePermissionGranted())
            uploadImage(getImageUri(this,mainImageBitmap));

    }

    private void uploadNewEvent(Uri imagePath){
        String key = mDatabase.child(Constants.EVENTS_KEYWORD).child(getUid()).push().getKey();
        Event event = new Event(key,getUid(),binding.inputName.getText().toString().trim(),
                binding.inputDescription.getText().toString().trim(),
                binding.inputCategory.getText().toString().trim(),
                Integer.parseInt(binding.inputDuration.getText().toString().trim()),
                imagePath.toString(),
                binding.inputVenue.getText().toString().trim().toLowerCase(),
                "NA", null,
                Double.parseDouble(binding.inputFees.getText().toString()),
                Double.parseDouble(binding.inputPrize.getText().toString()),
                binding.inputDuration.toString()
        );
        mDatabase.child(Constants.EVENTS_KEYWORD).child(getUid()).child(key).setValue(event);
        setResult(OrganizerMainActivity.REQ_NEW_ACTIVITY);
        finish();
    }
    private boolean validateDuration(){
        if(binding.inputDuration.getText().toString().trim().isEmpty()){
            binding.inputLayoutDuration.setError(getString(R.string.err_duration));
            requestFocus(binding.inputCategory);
            return false;
        } else {
            binding.inputLayoutDuration.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateCategory(){
        String category = binding.inputCategory.getText().toString().trim();
        String[] validCategories = getResources().getStringArray(R.array.event_categories);
        boolean valid = false;
        for(String s: validCategories){
            if(s.equals(category))
                valid = true;
        }
        if(category.isEmpty()){
            binding.inputLayoutCategory.setError(getString(R.string.err_category));
            requestFocus(binding.inputCategory);
            return false;
        } else if(!valid){
            binding.inputLayoutCategory.setError(getString(R.string.err_category));
            requestFocus(binding.inputCategory);
            return false;
        }

        else {
            binding.inputLayoutCategory.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateName(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            binding.inputLayoutName.setError(getString(R.string.err_name));
            requestFocus(binding.inputName);
            return false;
        } else {
            binding.inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateDescription(){
        if(binding.inputDescription.getText().toString().trim().isEmpty()){
            binding.inputLayoutDescription.setError(getString(R.string.err_description));
            requestFocus(binding.inputDescription);
            return false;
        } else {
            binding.inputLayoutDescription.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateVenue(){
        if(binding.inputVenue.getText().toString().trim().isEmpty()){
            binding.inputLayoutVenue.setError(getString(R.string.err_venue));
            requestFocus(binding.inputVenue);
            return false;
        } else {
            binding.inputLayoutVenue.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePrize(){
        String input = binding.inputPrize.getText().toString().trim();
        if(input.isEmpty()){
            binding.inputLayoutPrize.setError(getString(R.string.err_prize));
            requestFocus(binding.inputVenue);
            return false;
        } else if(Integer.parseInt(input)==0){
            binding.inputLayoutPrize.setError("Enter a non-zero value");
            requestFocus(binding.inputPrize);
            return false;
        } else {
            binding.inputLayoutPrize.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateFees(){
        String input = binding.inputFees.getText().toString().trim();
        if(input.isEmpty()){
            binding.inputLayoutFees.setError(getString(R.string.err_prize));
            requestFocus(binding.inputVenue);
            return false;
        } else if(Integer.parseInt(input)==0){
            binding.inputLayoutFees.setError("Enter a non-zero value");
            requestFocus(binding.inputFees);
            return false;
        } else {
            binding.inputLayoutFees.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE ){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //resume the task of uploading image
                Log.d(TAG, "onRequestPermissionsResult: Permission granted, resuming task");
                uploadImage(getImageUri(this, mainImageBitmap));
            }else {
                Toast.makeText(this,"Need to grant permission to use external storage",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadImage(Uri fileUri){
        if(fileUri == null){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mainImageBitmap != null){
            showProgressDialog();
            StorageReference ref =  mStorage.child(Constants.ORGANIZER_IMAGE).child(getUid()).child(fileUri.getLastPathSegment());
            ref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: upload succeeded");
                    Uri imagePath = taskSnapshot.getMetadata().getDownloadUrl();
                    uploadNewEvent(imagePath);
                    hideProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(NewEventActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
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
    private Uri getImageUri(Context context, Bitmap inImage){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "tempImage", null);
        return Uri.parse(path);

    }
}
