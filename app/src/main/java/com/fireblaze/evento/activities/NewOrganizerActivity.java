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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.SelectLocation;
import com.fireblaze.evento.databinding.ActivityNewOrganizerBinding;
import com.fireblaze.evento.models.ImageItem;
import com.fireblaze.evento.models.Location;
import com.fireblaze.evento.models.Organizer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewOrganizerActivity extends BaseActivity implements View.OnClickListener{

    public final String TAG = NewOrganizerActivity.class.getName();
    private ActivityNewOrganizerBinding binding;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private Location selectedLocation = null;

    private Bitmap mainImageBitmap;

    private final int REQ_SELECT_IMAGE = 2;
    public static final int REQ_GET_LOCATION = 1001;
    private final int REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE = 3;


    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }
    
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_organizer);

        setSupportActionBar(binding.toolbar);
        getViews();

        //Set onClickListeners
        binding.btnSubmit.setOnClickListener(this);
        binding.btnSetLocation.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                submitForm();
                break;
            case R.id.button_location:
                getLocation();
                break;
            case R.id.button_upload_image:
                getImageFromGallery();
                break;
        }
    }

    private void getImageFromGallery(){
        Intent galleryIntent = new  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,REQ_SELECT_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_GET_LOCATION:
                LatLng point = (LatLng) data.getExtras().get("point");
                selectedLocation = new Location(point);
                Toast.makeText(this,"point:"+point.toString(),Toast.LENGTH_SHORT).show();
                break;
            case REQ_SELECT_IMAGE:
                if(resultCode == RESULT_OK && data != null){
                    //Get image from data
                    Uri selectedImage = data.getData();
                    if(selectedImage != null){
                        try {
                            mainImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    private Uri getImageUri(Context context,Bitmap inImage){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "tempImage", null);
        return Uri.parse(path);

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
                    uploadNewOrganizer(imagePath);
                    hideProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(NewOrganizerActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
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
    private void getLocation() {
        startActivityForResult(new Intent(NewOrganizerActivity.this,SelectLocation.class),REQ_GET_LOCATION);
    }

    private void uploadNewOrganizer(Uri imagePath){
        Organizer organizer = new Organizer(getUid(),
                binding.inputName.getText().toString().trim(),
                binding.inputEmail.getText().toString().trim(),
                binding.inputPhone.getText().toString().trim(),
                binding.inputWebsite.getText().toString().trim(),
                selectedLocation,
                imagePath.toString());
        organizer.setIsValid(true);
        ImageItem imageItem = new ImageItem(getUid(),binding.inputName.getText().toString().trim(),
                imagePath.toString());
        Map<String, Object> postValues = organizer.toMap();
        Map<String,Object> imageValues = imageItem.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.ORGANIZER_IMAGE+"/"+getUid(),imageValues);
        childUpdates.put(Constants.ORGANIZER_KEYWORD+"/"+getUid(),postValues);
        mDatabase.updateChildren(childUpdates);
        Snackbar.make(getContainer(),"You are now an Organizer! You can now start adding new events",Snackbar.LENGTH_LONG);
        startActivity(new Intent(NewOrganizerActivity.this,OrganizerMainActivity.class));
        finish();
    }

    private void submitForm(){
        if(!validateVerification())
            return;
        if(!validateName())
            return;
        if(!validateEmail())
            return;
        if(!validateLocation())
            return;
        if(!validateWebsite())
            return;

        //Upload image to Firebase Storage and also call uploadNewOrganizer when image is successfully uploaded
        if(isStoragePermissionGranted())
            uploadImage(getImageUri(this, mainImageBitmap));


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
    private boolean validateWebsite(){
        if(binding.inputWebsite.getText().toString().trim().isEmpty()){
            binding.inputLayoutWebsite.setError("Please enter a website");
            requestFocus(binding.inputWebsite);
            return false;
        }else if(!Patterns.WEB_URL.matcher(binding.inputWebsite.getText()).matches()){
            binding.inputLayoutWebsite.setError("Please enter a valid website");
            requestFocus(binding.inputWebsite);
            return false;
        }
        return true;
    }
    private boolean validateLocation(){
        return selectedLocation != null;
    }

    private boolean validateEmail(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            binding.inputLayoutEmail.setError(getString(R.string.err_name));
            requestFocus(binding.inputEmail);
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString().trim()).matches()){
            binding.inputLayoutEmail.setError(getString(R.string.err_email));
            requestFocus(binding.inputEmail);
            return false;
        }
        else {
            binding.inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateVerification(){
        if(!binding.inputVerification.getText().toString().trim().equals(Constants.VERIFICATION_CODE)){
            binding.inputLayoutVerification.setError("Please enter the correct verification code");
            requestFocus(binding.inputVerification);
            return false;
        } else {
            binding.inputLayoutVerification.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_log_out:
                logOut();
                Log.d(TAG, "onOptionsItemSelected: logout success");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_PERMISSION_EXTERNAL_STORAGE_FOR_IMAGE ){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //resume the task of uploading image
                Log.d(TAG, "onRequestPermissionsResult: Permission granted, resuming task");
                uploadImage(getImageUri(this, mainImageBitmap));
            }else {
                Toast.makeText(this,"Need to grant permission to use external storage",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
