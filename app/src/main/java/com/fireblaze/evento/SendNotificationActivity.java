package com.fireblaze.evento;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Map;



public class SendNotificationActivity extends BaseActivity {
    Button sendButton;
    TextView reponseText;

    final static String TAG = SendNotificationActivity.class.getSimpleName();


    @Override
    public View getContainer() {
        return null;
    }

    class NotifyThread implements Runnable{
        JSONArray array;
        NotifyThread(JSONArray array){
            this.array = array;
        }
        @Override
        public void run() {
            try {
                pushFCMNotification(array);
            }catch (Exception e){
                Log.d(TAG, "run: "+ e.toString());
            }
        }
    }
    public void sendNotification(){
        final JSONArray array = new JSONArray();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.ORGANIZER_KEYWORD).child(getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Organizer o = dataSnapshot.getValue(Organizer.class);
                Map<String , String > map = o.getBookmarks();
                for(Map.Entry<String, String> e : map.entrySet()){
                    String value = e.getValue();
                    array.put(value);
                }
                NotifyThread nt = new NotifyThread(array);
                Thread t = new Thread(nt);
                t.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sendButton = (Button) findViewById(R.id.btn_send);
        reponseText = (TextView) findViewById(R.id.txt_response);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void pushFCMNotification(JSONArray recipients) throws Exception {

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
        info.put("title", "FCM Notificatoin Title"); // Notification title
        info.put("body", "Hello First Test notification"); // Notification body
        info.put("icon", "firebase-icon.png");


        data.put("notification", info);
        data.put("registration_ids", recipients);
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
