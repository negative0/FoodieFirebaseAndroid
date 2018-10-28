package com.fireblaze.foodiee.activities;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.databinding.ActivityChangeEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ChangeEmailActivity extends BaseActivity implements View.OnClickListener {

    ActivityChangeEmailBinding binding;

    @Override
    public View getContainer() {
        return binding.activityChangeEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_change_email);
        binding.btnOk.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }
    private void submitForm(){
        if(!validateName())
            return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            showProgressDialog();
            String currentName = user.getDisplayName()!=null?user.getDisplayName():"";
            String enteredName = binding.inputEmail.getText().toString().trim();

            if (currentName.equals(enteredName)) {
                Toast.makeText(ChangeEmailActivity.this, "Enter a new name", Toast.LENGTH_LONG).show();
                binding.inputEmail.setText("");
            }
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(enteredName)
                    .build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangeEmailActivity.this, "Name Changed", Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                        finish();
                    }

                }
            });
        } else {
            logOut();
        }


    }
    private boolean validateName(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            binding.inputLayoutEmail.setError("Please Enter a name");
            requestFocus(binding.inputEmail);
            return false;
        } else {
            binding.inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                submitForm();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
