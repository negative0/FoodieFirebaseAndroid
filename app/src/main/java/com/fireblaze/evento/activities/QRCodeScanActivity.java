package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String eventID;
    private Vibrator vibrator;

    public static void navigate(@NonNull Context context, String eventID){
        if(eventID == null){
            return;
        }
        Intent i = new Intent(context, QRCodeScanActivity.class);
        i.putExtra("eventID",eventID);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scan a Student's QR Code");
        }

        mScannerView = new ZXingScannerView(this);
        mScannerView.setResultHandler(this);
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(list);
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            eventID = b.getString("eventID");
        }else {
            Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mScannerView.startCamera();
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        vibrator.vibrate(500);
        String resultText = result.getText();
        String[] split;
        boolean flag;
        if(resultText.contains("=")){
            split = resultText.split("=");
            if(split.length == 2) {
                if (split[0].equals(Constants.USER_ID)){
                    final String userID = split[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Result");
                    builder.setMessage(result.getText());
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Event.markPresent(eventID,userID);
                            Toast.makeText(QRCodeScanActivity.this,"Added",Toast.LENGTH_SHORT).show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           resumeCamera();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    flag = true;
                } else {
                    flag = false;
                }
            } else flag = false;
        } else flag = false;

        if(!flag){
            Toast.makeText(this,"Invalid QR Code",Toast.LENGTH_SHORT).show();
            resumeCamera();
        }


    }
    private void resumeCamera(){
        mScannerView.resumeCameraPreview(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
