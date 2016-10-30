package com.fireblaze.evento.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LoginFragment extends SignInBaseFragment {

    private onLoginListener loginListener;


    public interface onLoginListener{
        void onLogin(String email, String password);
        void onGoogleSignIn();
        void onForgotPassword(@Nullable String userName);
    }
    public LoginFragment(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mBecomeOrganizerButton.setVisibility(View.GONE);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                   loginListener.onLogin(mEmailField.getText().toString(),mPasswordField.getText().toString());
                }
            }
        });
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onGoogleSignIn();
            }
        });

        mForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEmailField.getText())){
                    loginListener.onForgotPassword(null);
                } else {
                    loginListener.onForgotPassword(mEmailField.getText().toString());
                }
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            loginListener = (onLoginListener) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "must implement onLoginListener");
        }
    }
}
