package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityNewEventBinding;
import com.fireblaze.evento.fragments.DatePickerFragment;
import com.fireblaze.evento.fragments.TimePickerFragment;
import com.fireblaze.evento.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewEventActivity extends BaseActivity implements DatePickerFragment.dateSetListener, TimePickerFragment.timeSetListener, View.OnClickListener {
    public static final String TAG = NewEventActivity.class.getName();
    private DatabaseReference mDatabase;
    public ActivityNewEventBinding binding;
    private final int REQ_UPLOAD_IMAGE = 4;
    private static final String EVENT_ID = "eventID";
    private Event myEvent;
    private boolean isEdit;
    private long dateSchedule = 0;
    Date date;
    private String imagePath;
    Calendar c = null;


    @Override
    public void dateSet(int year, int month, int day, int hour, int min) {
        if(c==null)
            c = Calendar.getInstance();
        if(year!=0&&month!=0&&day!=0)
            c.set(year,month,day);
        if(hour!=0&&min!=0) {
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, min);
        }
        dateSchedule = c.getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        binding.inputDate.setText(simpleDateFormat.format(new Date(dateSchedule)));
    }
    public void dateSet(long dateSchedule){
        this.dateSchedule = dateSchedule;
    }

    @Override
    public void timeSet(int hour, int min) {
        dateSet(0,0,0,hour,min);
    }

    public static void navigate(Context context, String eventID){
        if(eventID == null || eventID.isEmpty()){
            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context,NewEventActivity.class);
        intent.putExtra(EVENT_ID,eventID);
        intent.putExtra("isEdit",true);
        context.startActivity(intent);

    }
    public static void navigate(Context context){
        Intent intent = new Intent(context,NewEventActivity.class);
        intent.putExtra("isEdit",false);
        context.startActivity(intent);
    }

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_event);
        getViews();
        isEdit = getIntent().getBooleanExtra("isEdit",false);
        if(isEdit){
            editEvent(getIntent().getStringExtra(EVENT_ID));
        }
        if(getSupportActionBar() != null ){
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.btnSubmit.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);
        binding.btnSetDate.setOnClickListener(this);
        binding.inputDate.setOnClickListener(this);
        binding.btnSetTime.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_submit:
                submitForm();
                break;
            case R.id.btn_set_date:
                showDatePicker();
                break;
            case R.id.btn_set_time:
                showTimePicker();
                break;
            case R.id.input_date:
                showTimePicker();
                break;
            case R.id.btn_upload_image:
                getImageFromGallery();
                break;

        }
    }
    private void showTimePicker(){
        DialogFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(),"TimePicker");
    }
    private void getImageFromGallery(){
        Intent i = new Intent(NewEventActivity.this,UploadImageActivity.class);
        String key = mDatabase.child(Constants.EVENTS_KEYWORD).push().getKey();

        i.putExtra(UploadImageActivity.UPLOAD_PATH_KEYWORD,
                "/"+Constants.EVENT_IMAGE+"/"+getUid()+"/"+key);
        startActivityForResult(i,REQ_UPLOAD_IMAGE);
    }
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

     }
    private void lockOkButton(boolean isLocked){
        if(isLocked){
            binding.btnSubmit.setClickable(false);
            binding.btnUploadImage.setClickable(false);
        } else {
            binding.btnSubmit.setClickable(false);
            binding.btnUploadImage.setClickable(true);
        }
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
        if(!validateImage())
            return;
        if(!validateDate())
            return;

        lockOkButton(true);
        uploadNewEvent(imagePath);
    }

    private void showDatePicker(){
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(),"DatePicker");
    }

    private void uploadNewEvent(String imagePath){
        String key;
        if(isEdit){
            key = myEvent.getEventID();
        } else
           key = mDatabase.child(Constants.EVENTS_KEYWORD).push().getKey();
        Event event = new Event(key,getUid(),binding.inputName.getText().toString().trim(),
                binding.inputDescription.getText().toString().trim(),
                binding.inputCategory.getText().toString().trim(),
                imagePath,
                binding.inputVenue.getText().toString().trim().toLowerCase(),
                "NA",
                Double.parseDouble(binding.inputFees.getText().toString()),
                Double.parseDouble(binding.inputPrize.getText().toString()),
                binding.inputDuration.getText().toString().trim()
        );
        event.setDateScheduleStartTimestamp(dateSchedule);
        mDatabase.child(Constants.EVENTS_KEYWORD).child(key).setValue(event)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setResult(RESULT_OK);
                    finish();
                } else {
                    lockOkButton(false);
                    Toast.makeText(NewEventActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void editEvent(@NonNull String  eventID){
        showProgressDialog();
        mDatabase.child(Constants.EVENTS_KEYWORD).child(eventID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        if(event != null)
                            setupViewForEdit(event);
                        else
                            Toast.makeText(NewEventActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setupViewForEdit(Event e){
        myEvent = e;
        binding.inputName.setText(e.getName());
        binding.inputDescription.setText(e.getDescription());
        binding.inputCategory.setText(e.getCategory());
        imagePath = e.getImage();
        binding.inputVenue.setText(e.getVenue());
        binding.inputFees.setText(String.valueOf(e.getParticipationFees()));
        binding.inputPrize.setText(String.valueOf(e.getPrizeAmount()));
        binding.inputDuration.setText(e.getDuration());
        dateSet(e.getDateScheduleStartTimestamp());
    }

    private boolean validateDate(){
        return dateSchedule != 0;
    }
    private boolean validateImage(){
        return imagePath!=null;
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
        } else if(Double.parseDouble(input)==0){
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
        } else if(Double.parseDouble(input)==0){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_UPLOAD_IMAGE:
                if(resultCode == RESULT_OK && data != null){
                    imagePath = data.getStringExtra(UploadImageActivity.DOWNLOAD_URL_RESULT);
                    Toast.makeText(NewEventActivity.this,"Upload Complete",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
