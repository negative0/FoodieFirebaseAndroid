package com.fireblaze.evento;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fireblaze.evento.activities.LoginActivity;
import com.fireblaze.evento.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by fireblaze on 26/12/16.
 */

public class UserOperations {
    public static final String TAG = UserOperations.class.getName();
    public static void deleteAccount(final Context context){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if(user != null){
            final String UID = user.getUid();
            mDatabase.child(Constants.USERS_KEYWORD).child(UID).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user1 = dataSnapshot.getValue(User.class);
                            if(user1 != null){
                                performDeletion(context, user, user1.isOrganizer(), mDatabase);
                            } else
                                Toast.makeText(context,"Failed!",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );

        }
        
    }
    private static void performDeletion(final Context context, final FirebaseUser user,final boolean isOrganizer,final DatabaseReference ref){
        final String UID = user.getUid();
        if(isOrganizer){
            ref.child(Constants.ORGANIZER_KEYWORD).child(UID).setValue(null);
            ref.child(Constants.ORGANIZER_IMAGE).child(UID).setValue(null);
        }
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("UserOperations", "onComplete: Account Deleted");
                            ref.child(Constants.USERS_KEYWORD).child(user.getUid()).setValue(null)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: User removed from Database");
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        }
                                    });
                        }
                    }
                });
    }
    public static String getUid(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null) {
            return user.getUid();
        }
        return null;
    }
    public static void updateProfileImage(String imagePath){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imagePath))
                    .build();
            user.updateProfile(request);

        }
    }


}
