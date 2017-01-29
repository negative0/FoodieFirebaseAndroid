package com.fireblaze.evento.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireblaze.evento.R;

/**
 * Created by chait on 6/7/2016.
 */

public class SignUpFragment extends SignInBaseFragment {
    onSignUpListener signUpListener;

    public interface onSignUpListener{
        void isOrganizer(boolean isOrganizer);
        void onSignUp(String email, String password);
        void onGoogleSignIn();
    }
    public SignUpFragment(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    signUpListener.onSignUp(mEmailField.getText().toString(),mPasswordField.getText().toString());
                }
            }
        });
        mSignInButton.setText(getString(R.string.sign_up));
        mForgotPasswordText.setVisibility(View.GONE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOrganizer) {
                    if (validateOrganizer()) {
                        signUpListener.onGoogleSignIn();
                    }
                } else {
                    signUpListener.onGoogleSignIn();
                }
            }
        });
        mBecomeOrganizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAOrganizer();
            }
        });

        return rootView;
    }


    private void isAOrganizer(){
        if(!isOrganizer){
            isOrganizer = true;
            signUpListener.isOrganizer(true);
            mOrganizerVerification.setVisibility(View.VISIBLE);
            mBecomeOrganizerButton.setText("Congrats! Click to cancel");
        } else {
            isOrganizer = false;
            signUpListener.isOrganizer(false);
            mOrganizerVerification.setVisibility(View.GONE);
            mBecomeOrganizerButton.setText("Become a Organizer");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            signUpListener = (onSignUpListener) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "must implement onLoginListener");
        }
    }


}
