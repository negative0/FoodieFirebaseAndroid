package com.fireblaze.evento;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.fireblaze.evento.models.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewEventActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private EditText inputName,inputDescription,inputCategory,inputVenue,inputFees,inputPrize;
    private TextInputLayout inputLayoutName,inputLayoutDescription,inputLayoutCategory,inputLayoutVenue,inputLayoutFees,inputLayoutPrize;
    private Button btnSubmit;
    private Toolbar toolbar;

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getViews();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    private void getViews(){
        inputName = (EditText) findViewById(R.id.input_name);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputCategory = (EditText) findViewById(R.id.input_category);
        inputVenue = (EditText) findViewById(R.id.input_venue);
        inputFees = (EditText) findViewById(R.id.input_fees);
        inputPrize = (EditText) findViewById(R.id.input_prize);
        
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutCategory = (TextInputLayout) findViewById(R.id.input_layout_category);
        inputLayoutVenue = (TextInputLayout) findViewById(R.id.input_layout_venue);
        inputLayoutFees = (TextInputLayout) findViewById(R.id.input_layout_fees);
        inputLayoutPrize = (TextInputLayout) findViewById(R.id.input_layout_prize);
        
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        
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

        String key = mDatabase.child(Constants.EVENTS_KEYWORD).child(getUid()).push().getKey();
        Event event = new Event(key,inputName.getText().toString().trim(),
                inputDescription.getText().toString().trim(),
                inputCategory.getText().toString().trim(),
                0,"http://design.ubuntu.com/wp-content/uploads/logo-ubuntu_st_no%C2%AE-black_orange-hex.png",
                inputVenue.getText().toString().trim().toLowerCase(),
                "NA", null,
                Double.parseDouble(inputFees.getText().toString()),
                Double.parseDouble(inputPrize.getText().toString()),"1 hr"
                );
        mDatabase.child(Constants.EVENTS_KEYWORD).child(getUid()).child(key).setValue(event);
        setResult(OrganizerMainActivity.REQ_NEW_ACTIVITY);
        finish();
    }
    private boolean validateCategory(){
        if(inputCategory.getText().toString().trim().isEmpty()){
            inputLayoutCategory.setError(getString(R.string.err_category));
            requestFocus(inputCategory);
            return false;
        } else {
            inputLayoutCategory.setErrorEnabled(false);
        }
        return true;
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
    private boolean validateDescription(){
        if(inputDescription.getText().toString().trim().isEmpty()){
            inputLayoutDescription.setError(getString(R.string.err_description));
            requestFocus(inputDescription);
            return false;
        } else {
            inputLayoutDescription.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateVenue(){
        if(inputVenue.getText().toString().trim().isEmpty()){
            inputLayoutVenue.setError(getString(R.string.err_venue));
            requestFocus(inputVenue);
            return false;
        } else {
            inputLayoutVenue.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePrize(){
        String input = inputPrize.getText().toString().trim();
        if(input.isEmpty()){
            inputLayoutPrize.setError(getString(R.string.err_prize));
            requestFocus(inputVenue);
            return false;
        } else if(Integer.parseInt(input)==0){
            inputLayoutPrize.setError("Enter a non-zero value");
            requestFocus(inputPrize);
            return false;
        } else {
            inputLayoutPrize.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateFees(){
        String input = inputFees.getText().toString().trim();
        if(input.isEmpty()){
            inputLayoutFees.setError(getString(R.string.err_prize));
            requestFocus(inputVenue);
            return false;
        } else if(Integer.parseInt(input)==0){
            inputLayoutFees.setError("Enter a non-zero value");
            requestFocus(inputFees);
            return false;
        } else {
            inputLayoutFees.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
