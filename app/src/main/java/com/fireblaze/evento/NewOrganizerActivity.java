package com.fireblaze.evento;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fireblaze.evento.models.ImageItem;
import com.fireblaze.evento.models.Location;
import com.fireblaze.evento.models.Organizer;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewOrganizerActivity extends BaseActivity{

    public final String TAG = NewOrganizerActivity.class.getName();
    private EditText inputName, inputEmail, inputPhone, inputWebsite,inputVerification;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone, inputLayoutWebsite,inputLayoutVerification;
    private Button btnSubmit, btnSetLocation, btnUploadImage;
    private DatabaseReference mDatabase;
    private Location selectedLocation = null;

    public static final int REQ_GET_LOCATION = 1001;


    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }
    
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail =(EditText) findViewById(R.id.input_email);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputWebsite = (EditText) findViewById(R.id.input_website);
        inputVerification = (EditText) findViewById(R.id.input_verification);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail =(TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutWebsite = (TextInputLayout) findViewById(R.id.input_layout_website);
        inputLayoutVerification = (TextInputLayout) findViewById(R.id.input_layout_verification);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSetLocation = (Button) findViewById(R.id.button_location);
        btnUploadImage = (Button) findViewById(R.id.button_upload_image);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organizer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getViews();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
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
        }
    }

    private void getLocation() {
        startActivityForResult(new Intent(NewOrganizerActivity.this,SelectLocation.class),REQ_GET_LOCATION);
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

        Organizer organizer = new Organizer(getUid(),
                inputName.getText().toString().trim(),
                inputEmail.getText().toString().trim(),
                inputPhone.getText().toString().trim(),
                inputWebsite.getText().toString().trim(),
                selectedLocation,
                "http://design.ubuntu.com/wp-content/uploads/logo-ubuntu_st_no%C2%AE-black_orange-hex.png");
        organizer.setIsValid(true);
        ImageItem imageItem = new ImageItem(getUid(),inputName.getText().toString().trim(),
                "http://design.ubuntu.com/wp-content/uploads/logo-ubuntu_st_no%C2%AE-black_orange-hex.png");
        Map<String, Object> postValues = organizer.toMap();
        Map<String,Object> imageValues = imageItem.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.ORGANIZER_IMAGE+"/"+getUid(),imageValues);
        childUpdates.put(Constants.ORGANIZER_KEYWORD+"/"+getUid(),postValues);
        mDatabase.updateChildren(childUpdates);
        Snackbar.make(getContainer(),"You are now done! You can now start adding new events",Snackbar.LENGTH_LONG);
        startActivity(new Intent(NewOrganizerActivity.this,OrganizerMainActivity.class));
        finish();
    }
    private boolean validateName(){
        if(inputName.getText().toString().trim().isEmpty()){
            inputLayoutName.setError(getString(R.string.err_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateWebsite(){
        if(inputWebsite.getText().toString().trim().isEmpty()){
            inputLayoutWebsite.setError("Please enter a website");
            requestFocus(inputWebsite);
            return false;
        }else if(!Patterns.WEB_URL.matcher(inputWebsite.getText()).matches()){
            inputLayoutWebsite.setError("Please enter a valid website");
            requestFocus(inputWebsite);
            return false;
        }
        return true;
    }
    private boolean validateLocation(){
        return selectedLocation != null;
    }
    private boolean validateEmail(){
        if(inputEmail.getText().toString().trim().isEmpty()){
            inputLayoutEmail.setError(getString(R.string.err_name));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateVerification(){
        if(!inputVerification.getText().toString().trim().equals(Constants.VERIFICATION_CODE)){
            inputLayoutVerification.setError("Please enter the correct verification code");
            requestFocus(inputVerification);
            return false;
        } else {
            inputLayoutVerification.setErrorEnabled(false);
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
