package com.fireblaze.evento.activities;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewOrganizerActivity extends BaseActivity implements View.OnClickListener{

    public final String TAG = NewOrganizerActivity.class.getName();
    private ActivityNewOrganizerBinding binding;
    private DatabaseReference mDatabase;
    private Location selectedLocation = null;
    private String imagePath;

    public static final int REQ_GET_LOCATION = 1001;
    private final int REQ_UPLOAD_IMAGE = 4;


    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }
    
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mStorage = FirebaseStorage.getInstance().getReference();

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
            case R.id.btn_set_location:
                getLocation();
                break;
            case R.id.btn_upload_image:
                getImageFromGallery();
                break;
        }
    }

    private void getImageFromGallery(){
        Intent i = new Intent(NewOrganizerActivity.this,UploadImageActivity.class);
        i.putExtra(UploadImageActivity.UPLOAD_PATH_KEYWORD,
                "/"+Constants.ORGANIZER_IMAGE+"/"+getUid());
        startActivityForResult(i,REQ_UPLOAD_IMAGE);
//        Intent galleryIntent = new  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent,REQ_SELECT_IMAGE);
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
            case REQ_UPLOAD_IMAGE:
                if(resultCode == RESULT_OK && data != null){
                    Bundle b = data.getExtras();
                    imagePath = b.getString(UploadImageActivity.DOWNLOAD_URL_RESULT);
                    Toast.makeText(NewOrganizerActivity.this,"imagePath="+imagePath,Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getLocation() {
        startActivityForResult(new Intent(NewOrganizerActivity.this,SelectLocation.class),REQ_GET_LOCATION);
    }

    private void uploadNewOrganizer(String imagePath){
        Organizer organizer = new Organizer(getUid(),
                binding.inputName.getText().toString().trim(),
                binding.inputEmail.getText().toString().trim(),
                binding.inputPhone.getText().toString().trim(),
                binding.inputWebsite.getText().toString().trim(),
                selectedLocation,
                imagePath);

        ImageItem imageItem = new ImageItem(getUid(),binding.inputName.getText().toString().trim(),
                imagePath);
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
//        if(isStoragePermissionGranted())
//            uploadImage(getImageUri(this, mainImageBitmap));
        uploadNewOrganizer(imagePath);

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


}
