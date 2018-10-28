package com.fireblaze.foodiee.views;

import android.content.Context;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by fireblaze on 25/12/16.
 */

public class PasswordDialogPreference extends EditTextPreference {
    public PasswordDialogPreference(Context context, AttributeSet attrs){
        super(context,attrs);
        getEditText().setInputType( InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult) {
            Log.d("PasswordDialog", "onDialogClosed: Password changed");
            EditText editText = getEditText();
            String password = editText.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.updatePassword(password)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Log.d("PasswordDialog", "onDialogClosed: Password updated");
                            }
                        });

            }
        }


    }


}
