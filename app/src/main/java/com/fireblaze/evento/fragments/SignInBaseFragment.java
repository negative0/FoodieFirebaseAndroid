package com.fireblaze.evento.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.UserOperations;

/**
 * Created by chait on 6/7/2016.
 */

public abstract class SignInBaseFragment extends Fragment {


    public Button mSignInButton;
    public TextView mForgotPasswordText;
    public TextInputEditText mEmailField;
    public TextInputEditText mPasswordField, mOrganizerVerification;
    public Button mGoogleSignInButton;
    public Button mBecomeOrganizerButton;
    public boolean isOrganizer = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_layout_login,container,false);
        //Get Views
        mSignInButton = (Button) rootView.findViewById(R.id.btn_sign_in);
        mForgotPasswordText = (TextView) rootView.findViewById(R.id.text_forgot_password);
        mEmailField = (TextInputEditText) rootView.findViewById(R.id.field_email);
        mPasswordField = (TextInputEditText) rootView.findViewById(R.id.field_password);
        mGoogleSignInButton = (Button) rootView.findViewById(R.id.sign_in_google_button);
        mBecomeOrganizerButton = (Button) rootView.findViewById(R.id.become_an_organizer_button);
        mOrganizerVerification = (TextInputEditText) rootView.findViewById(R.id.field_organizer_verification);
        return rootView;
    }

    protected boolean validateForm(){
        boolean result = true;
        String email = mEmailField.getText().toString().trim();
        String password = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Required");
            result = false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Please enter a valid email");
            result = false;
        }
        else{
            mEmailField.setError(null);
        }
        if(TextUtils.isEmpty(password)){
            mPasswordField.setError("Required");
            result = false;
        } else if(password.length() < 5){
            mPasswordField.setError("Enter a password with at least 5 characters");
            result = false;
        }
        else {
            mPasswordField.setError(null);
        }
        if(isOrganizer) {
            if (!validateOrganizer()) {
                result = false;
            }
        }
        return result;
    }
    public boolean validateOrganizer(){
        String verification = mOrganizerVerification.getText().toString().trim();
        if(verification.isEmpty()){
            mOrganizerVerification.setError("Please enter the code provided to you");
            return false;
        } else if(!verification.equals(Constants.VERIFICATION_CODE)){
            mOrganizerVerification.setError("Plese enter the correct code");
            return false;
        }
        mOrganizerVerification.setError(null);
        return true;
    }

    public String getUid(){
        return UserOperations.getUid();
    }
}
