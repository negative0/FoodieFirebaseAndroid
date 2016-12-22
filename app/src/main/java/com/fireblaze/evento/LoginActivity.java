package com.fireblaze.evento;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fireblaze.evento.fragments.LoginFragment;
import com.fireblaze.evento.fragments.SignUpFragment;
import com.fireblaze.evento.models.NotificationToken;
import com.fireblaze.evento.models.Organizer;
import com.fireblaze.evento.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by chait on 6/7/2016.
 */

public class LoginActivity extends BaseActivity implements LoginFragment.onLoginListener,
        SignUpFragment.onSignUpListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LoginActivity";

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private boolean isOrganizer = false;


    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    public void isOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private Fragment[] mFragments = {
                    new LoginFragment(),
                    new SignUpFragment()
            };
            private String[] mFragmentTitles = {
                    "Login",
                    "Sign Up"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitles[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        //Firebase Auth change Listener
        mAuthListener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User Signed in
                    onAuthSuccess(user,true);
                    Log.d(TAG,"onAuthChanged:signed_In" + user.getUid());
                } else {
                    Log.d(TAG,"onAuthChanged:signed_out");
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check Auth
        Log.d(TAG,"onStart:getCurrentUser="+mAuth.getCurrentUser());
        if(mAuth.getCurrentUser() != null){
            onAuthSuccess(mAuth.getCurrentUser(),true);
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void onAuthSuccess(FirebaseUser user,boolean isLogin){
        if(!isLogin){
            String username = usernameFromEmail(user.getEmail());
           
            
            writeNewUser(user.getUid(),username,isOrganizer);
            if(isOrganizer) signUpAsOrganizer(); else loginAsUser();
        } else {
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.isOrganizer) loginAsOrganizer(); else loginAsUser();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        SharedPreferences prefs = getSharedPreferences("INSTANCE", MODE_PRIVATE);
        String token = prefs.getString("TOKEN",null);
        if(token != null){
            Log.d(TAG, "onAuthSuccess: Token set");
            NotificationToken notificationToken = new NotificationToken(token);
            mDatabase.child(Constants.NOTIFICATION_TOKENS).child(user.getUid()).setValue(notificationToken);
        }

    }
    private void signUpAsOrganizer(){
        if(getUid()==null){
            return;
        }
        Organizer organizer = new Organizer(getUid());
        mDatabase.child(Constants.ORGANIZER_KEYWORD).child(getUid()).setValue(organizer);
        startActivity(new Intent(LoginActivity.this,NewOrganizerActivity.class));
        finish();
    }
    private void loginAsOrganizer(){

        mDatabase.child(Constants.ORGANIZER_KEYWORD).child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Create an intent for activity launch and set it as a separate one by clear top
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(LoginActivity.this,OrganizerMainActivity.class);

                Organizer organizer = dataSnapshot.getValue(Organizer.class);
                if(organizer != null) {
                    //Check if the user is a valid organizer
                    if (organizer.getIsValid()) {
                        intent.setClass(LoginActivity.this,OrganizerMainActivity.class);
                    } else {
                        intent.setClass(LoginActivity.this,NewOrganizerActivity.class);
                    }
                } else {
                    intent.setClass(LoginActivity.this,NewOrganizerActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"loginAsOrganizer: onError",databaseError.toException());
            }
        });

        finish();
    }
    private void loginAsUser(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String usernameFromEmail(String email){
        if(email.contains("@"))
            return email.split("@")[0];
        else
            return email;
    }

    @Override
    public void onLogin(String email, String password) {
        Log.d(TAG,"onLogin");
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete" + task.isSuccessful());
                        hideProgressDialog();

                        if(task.isSuccessful()){
                            onAuthSuccess(task.getResult().getUser(),true);
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeNewUser(String userId, String userName,boolean isOrganizer){
        User user = new User(userId,userName,isOrganizer);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onSignUp(String email, String password) {
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"createUser:OnComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if(task.isSuccessful()){
                            onAuthSuccess(task.getResult().getUser(),false);
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignIn(result);
        }
    }
    private void handleGoogleSignIn(GoogleSignInResult result){
        Log.d(TAG,"FirebaseAuthWithGoogle" );
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
            Log.d(TAG,"tokenID="+acct.getIdToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Log.w(TAG,"signInWithGoogle",task.getException());
                                Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else{
            Toast.makeText(this,"Sign In failed",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"signInWithGoogle:onFail "+result.getSignInAccount());
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"connectionFailed"+connectionResult.getErrorMessage());
    }

    @Override
    public void onForgotPassword(@Nullable String userName) {
        showForgotPasswordDialog(userName);
    }

    private void showForgotPasswordDialog(@Nullable final String email){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPasswordResetEmail(email);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_forgot_password,null);

        dialog.setView(v);
        dialog.setTitle("Forgot Password?");

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                EditText emailField = (EditText) dialog.findViewById(R.id.field_email);
                if(emailField != null)
                    emailField.setText(email);
            }
        });
        dialog.show();

    }

    private void sendPasswordResetEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(getContainer(),"Check your email for further instructions",Snackbar.LENGTH_SHORT).show();
                            Log.d(TAG,"sendPasswordResetEmail:successful");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public void signUp(View v){
        mViewPager.setCurrentItem(1);
    }
}
