package com.fireblaze.evento.activities;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.NotificationToken;
import com.fireblaze.evento.models.Organizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendNotificationActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{
    String content;
    private TextInputEditText textTitle, textContent;
    private TextInputLayout textLayoutTitle, textLayoutContent;
    private Spinner spinner;
    private int userType = 1;
    final static String TAG = SendNotificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getViews();
        setupView();

    }
    private void setupView(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
                Snackbar.make(view, "Sending Notifications", Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show();
            }
        });
        spinner.setOnItemSelectedListener(this);
        List<String> cat = new ArrayList<>();
        cat.add("Users");
        cat.add("Volunteers");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    private void getViews(){
        textTitle = (TextInputEditText) findViewById(R.id.input_title);
        textContent = (TextInputEditText) findViewById(R.id.input_content);
        textLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        textLayoutContent = (TextInputLayout) findViewById(R.id.input_layout_content);
        spinner = (Spinner) findViewById(R.id.spinner);
    }

    private boolean validateTitle(){
        if(textTitle.getText().toString().trim().isEmpty()){
            textLayoutTitle.setError(getString(R.string.err_title));
            requestFocus(textTitle);
            return false;
        } else {
            textLayoutTitle.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateContent(){
        if(textContent.getText().toString().trim().isEmpty()){
            textLayoutContent.setError(getString(R.string.err_content));
            requestFocus(textContent);
            return false;
        } else {
            textLayoutContent.setErrorEnabled(false);
        }
        return true;
    }
    @Override
    public View getContainer() {
        return null;
    }

    class NotifyThread implements Runnable{
        JSONArray array;
        public NotifyThread(JSONArray array) {
            this.array = array;

        }

        @Override
        public void run() {
            try {
                pushFCMNotification(array,textTitle.getText().toString(),textContent.getText().toString());
            }catch (Exception e){
                Log.d(TAG, "run: "+ e.toString());
            }
        }
    }
    public void sendNotification(){
        if(!validateTitle())
            return;
        if(!validateContent())
            return;

        final List<NotificationToken> tokens = new ArrayList<>();
        final DatabaseReference mOrganizerReference = FirebaseDatabase.getInstance().getReference().child(Constants.ORGANIZER_KEYWORD).child(getUid());
        final DatabaseReference mTokensReference = FirebaseDatabase.getInstance().getReference().child(Constants.NOTIFICATION_TOKENS);
        mOrganizerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organizer o = dataSnapshot.getValue(Organizer.class);
                Map<String, Boolean> map;
                if(userType == 1){
                    map = o.getBookmarks();
                }else {
                    map = o.getVolunteers();
                }

                final int mapSize = map.size();

                for(Map.Entry<String, Boolean> e: map.entrySet()){
                    String key = e.getKey();

                    mTokensReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                NotificationToken token = dataSnapshot.getValue(NotificationToken.class);
                                tokens.add(token);
                                if(tokens.size()== mapSize){
                                    initThread(tokens);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void initThread(List<NotificationToken> list){

        JSONArray array = new JSONArray();
        for(NotificationToken token : list){
            if(token.isReceiveNotifications())
                array.put(token.getToken());
        }
        if(array.length() >= 0) {
            NotifyThread nt = new NotifyThread(array);
            Thread t = new Thread(nt);
            t.start();
        } else {
            Toast.makeText(SendNotificationActivity.this,"No Recipients",Toast.LENGTH_LONG).show();
        }
    }




    public void pushFCMNotification(JSONArray recipients, String title, String content) throws Exception {


        String authKey = "AIzaSyCmhrSM9A8BOZvrRX9EwdRKOPWYHWwFf4A"; // You FCM AUTH key



        String FMCurl = "https://fcm.googleapis.com/fcm/send";

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + authKey);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject data = new JSONObject();
        JSONObject info = new JSONObject();
        JSONObject extraData = new JSONObject();

        info.put("title", title.trim()); // Notification title
        info.put("body", content.trim()); // Notification body

        data.put("notification", info);
        data.put("registration_ids", recipients);

        extraData.put(Constants.ORGANIZER_KEYWORD,getUid());

        data.put("data",extraData);

        Log.d(TAG, "pushFCMNotification: request "+ data.toString());

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data.toString());
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Log.d(TAG, "pushFCMNotification: "+ response.toString());

    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        userType = i+1;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Auto generated
    }
}
 //   private void sendNotification(){
//        HttpURLConnection connection;
//        URL url;
//        try{
//            url= new URL("https://fcm.googleapis.com/fcm/send");
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Authorization","key=AIzaSyCmhrSM9A8BOZvrRX9EwdRKOPWYHWwFf4A");
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            JSONObject main = new JSONObject();
//            JSONObject notification = new JSONObject();
//
//            notification.put("title","My test title");
//            notification.put("body", "body content");
//
//            main.put("notification",notification.toString());
//            main.put("to","fXuTUc3M8lI:APA91bHPVbWhkJ2Z8Ab9wZib2OIkuVwJEb-Be_22dCyaQbO6hfbxkzQm5jfv1-AJL1edh-qj8Pavg8qRpg7GIWj7A-eQHAWottWf97SlgosBFcI5AE4N9UQdsIMObatlBULpR_m1jZLt");
//            Log.d(TAG, "sendNotification: "+ main.toString());
//            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
//            wr.write(main.toString());
//            wr.flush();
//
//            StringBuilder sb = new StringBuilder();
//            int HttpResult = connection.getResponseCode();
//            if(HttpResult == HttpURLConnection.HTTP_OK){
//                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
//                String line = null;
//                while ((line = br.readLine())!= null){
//                    sb.append(line);
//                    sb.append("\n");
//                }
//                br.close();
//                Log.d(TAG, "sendNotification: "+sb.toString());
//            } else {
//                Log.d(TAG, "sendNotification: "+connection.getResponseMessage());
//            }
//        }catch (Exception e){
//            Log.d(TAG, "sendNotification: error:"+e.toString());
//        }
//    }
