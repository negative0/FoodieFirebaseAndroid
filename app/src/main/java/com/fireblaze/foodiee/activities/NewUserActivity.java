package com.fireblaze.foodiee.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.UserOperations;
import com.fireblaze.foodiee.databinding.ActivityNewUserBinding;
import com.fireblaze.foodiee.models.MyLocation;
import com.fireblaze.foodiee.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class NewUserActivity extends BaseActivity implements View.OnClickListener{
    public static final int REQ_GET_LOCATION = 1001;
    private ActivityNewUserBinding binding;
    private DatabaseReference mDatabase;

    private MyLocation selectedMyLocation = null;


    @Override
    public View getContainer() {
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_user);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        binding.submit.setOnClickListener(this);
        binding.btnSelectLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                submitForm();
                break;
            case R.id.btn_select_location:
                getLocation();
                break;
        }
    }

    private void getLocation() {
        startActivityForResult(new Intent(NewUserActivity.this,SelectLocationActivity.class),REQ_GET_LOCATION);
    }

    private void submitForm(){
        if(!validateName())
            return;
        if(!validatePhone())
            return;
        if(!validateCollege())
            return;
        if(!validateLocation())
            return;

        writeNewUser();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();


    }

    private boolean validateLocation(){
        return selectedMyLocation != null;
    }
    private boolean validateCollege(){
        String college = binding.textCollege.getText().toString().trim();
        if(college.isEmpty()){
            binding.textInputCollege.setError("Please enter a college name");
            requestFocus(binding.textCollege);
            return false;
        } else if(college.length() <5) {
            binding.textInputCollege.setError("Please enter atleast 5 characters");
            requestFocus(binding.textCollege);
            return false;
        } else {
            binding.textInputCollege.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePhone(){
        String phone = binding.textPhone.getText().toString().trim();

        if(phone.isEmpty()){
            binding.textInputPhone.setError("Please enter a phone number");
            requestFocus(binding.textPhone);
            return false;
        } else if(!Patterns.PHONE.matcher(phone).matches()){
            binding.textInputPhone.setError("Please enter a valid number");
            requestFocus(binding.textPhone);
            return false;
        }else if(phone.length() != 10){
            binding.textInputPhone.setError("Please enter exactly 10 digits");
            requestFocus(binding.textPhone);
            return false;
        }
        else {
            binding.textInputPhone.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateName(){
        String name = binding.textName.getText().toString().trim();

        if(name.isEmpty()){
            binding.textInputName.setError("Enter a name");
            requestFocus(binding.textName);
            return false;
        }else if(name.length() < 5){
            binding.textInputName.setError("Enter a name more than 5 characters");
            requestFocus(binding.textName);
            return false;
        } else if(name.length() > 30){
            binding.textInputName.setError("Enter a name with less than 30 characters");
            requestFocus(binding.textName);
        } else {
            binding.textInputName.setErrorEnabled(false);
        }
        return true;

    }
    private void writeNewUser(){
        mDatabase.child(Constants.USERS_KEYWORD).child(getUid())
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User user = mutableData.getValue(User.class);
                        if(user == null){
                            return Transaction.success(mutableData);
                        }
                        String name = binding.textName.getText().toString();
                        user.setName(name);
                        UserOperations.updateName(name);
                        user.setPhone(binding.textPhone.getText().toString());
                        user.setCollegeName(binding.textCollege.getText().toString());
                        user.setValid(true);
                        user.setMyLocation(selectedMyLocation);
                        mutableData.setValue(user);
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_GET_LOCATION:
                LatLng point = (LatLng) data.getExtras().get("point");
                selectedMyLocation = new MyLocation(point);
                Toast.makeText(this,"point:"+point.toString(),Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
