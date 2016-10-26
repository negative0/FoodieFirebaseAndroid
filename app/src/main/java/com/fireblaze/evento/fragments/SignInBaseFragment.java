package com.fireblaze.evento.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fireblaze.evento.R;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by chait on 6/7/2016.
 */

public abstract class SignInBaseFragment extends Fragment {


    public Button mSignInButton;
    public TextView mForgotPasswordText;
    public EditText mEmailField;
    public EditText mPasswordField;
    public SignInButton mGoogleSignInButton;
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
        mEmailField = (EditText) rootView.findViewById(R.id.field_email);
        mPasswordField = (EditText) rootView.findViewById(R.id.field_password);
        mGoogleSignInButton = (SignInButton) rootView.findViewById(R.id.sign_in_google_button);
        mBecomeOrganizerButton = (Button) rootView.findViewById(R.id.become_an_organizer_button);
        return rootView;
    }

    protected boolean validateForm(){
        boolean result = true;
        if(TextUtils.isEmpty(mEmailField.getText())){
            mEmailField.setError("Required");
            result = false;
        } else{
            mPasswordField.setError(null);
        }
        if(TextUtils.isEmpty(mPasswordField.getText())){
            mPasswordField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }
        return result;
    }

    public String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
