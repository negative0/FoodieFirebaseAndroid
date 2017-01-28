package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRCodeActivity extends AppCompatActivity {

    private final static int WIDTH=500;
    private ImageView QRImage;
    private String userID;

    public static void navigate(Context context, String userID){
        if(userID == null || userID.isEmpty()){
            return;
        }
        Intent i = new Intent(context,QRCodeActivity.class);
        i.putExtra(Constants.USER_ID,userID);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        QRImage = (ImageView) findViewById(R.id.image_qr_code);

        Bundle b = getIntent().getExtras();
        if(b!= null){
            userID = b.getString(Constants.USER_ID);
            generateQRCode(Constants.USER_ID +"="+ userID);
        }
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("My User Code");
        }


    }
    private void generateQRCode(String message){
        Bitmap bt;
        try {
            bt = encodeAsBitmap(message);
            QRImage.setImageBitmap(bt);
        }catch (WriterException e){
            Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap encodeAsBitmap(String str) throws WriterException{

        BitMatrix result;
        try{
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,WIDTH,WIDTH);

        }catch (IllegalArgumentException e){
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w*h];
        for(int y=0;y<h;y++){
            int offset= y * w;
            for(int x=0;x<w;x++){
                pixels[offset+x] = result.get(x,y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,w,0,0,w,h);
        return bitmap;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

