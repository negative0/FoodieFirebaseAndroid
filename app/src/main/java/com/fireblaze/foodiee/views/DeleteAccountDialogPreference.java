package com.fireblaze.foodiee.views;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.fireblaze.foodiee.UserOperations;


public class DeleteAccountDialogPreference extends DialogPreference {
    public static final String TAG = DeleteAccountDialogPreference.class.getName();
    public DeleteAccountDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            UserOperations.deleteAccount(getContext());
        }
    }
}
