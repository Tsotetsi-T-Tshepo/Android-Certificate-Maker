package com.example.simplecertificategenerator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnLoadCert;
    CheckBox chkBold,chkUnderline,chkItalic;
    EditText txtText, txtXCoordinate, txtYCoordinate;
    ImageView imageView;
    String FullName = "";
    public  static  Bitmap imgBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadCert=findViewById(R.id.btnGenerate);
        chkBold=findViewById(R.id.chbBold);
        chkItalic=findViewById(R.id.chbItalic);
        chkUnderline=findViewById(R.id.chbUnderline);
        imageView=findViewById(R.id.imgCertificate);
        txtText=findViewById(R.id.edtWriteText);
        txtXCoordinate =findViewById(R.id.edtXCoordinates);
        txtYCoordinate =findViewById(R.id.edtYCoordinates);


        btnLoadCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sdkInt = Build.VERSION.SDK_INT;
                if (sdkInt >= Build.VERSION_CODES.S) {
                    // Running on Android Oreo or higher
                    // Redirects to apps permission
                    if (!Environment.isExternalStorageManager())
                    {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                else
                {
                    //Android 10 and Lower
                    //Ask Permission to manage Storage
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
                    }
                }
                int YCoord = Integer.parseInt(txtYCoordinate.getText().toString());
                int XCoord = Integer.parseInt(txtXCoordinate.getText().toString());
                ProcessingBitmap(txtText.getText().toString(),YCoord,XCoord);
            }

        });



    }

    private void DownladCertificate(Bitmap bitmap) {

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth() , bitmap.getHeight(),  1).create();
        PdfDocument.Page page = pdfDocument.startPage(pi);

        Canvas canvas = page.getCanvas();

        bitmap = Bitmap.createScaledBitmap(bitmap , bitmap.getWidth(), bitmap.getHeight() , true);
        canvas.drawBitmap(bitmap , 0, 0, null);
        pdfDocument.finishPage(page);

        //save the bitmap image

        File root = new File(Environment.getExternalStorageDirectory(), "Pdf");
        try {
            root.mkdir();
        }
        catch (Exception k)
        {
            Toast.makeText(this,"Check App's Files Permissions",Toast.LENGTH_SHORT).show();
        }
        File file = new File(root , "Certificate.pdf");
        Toast.makeText(this, "THis: "+file.toString()+" Downloading", Toast.LENGTH_SHORT).show();
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
            fileOutputStream.close();
            Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();

        }catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Download Certificate", Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
    }

    public Bitmap  ProcessingBitmap(String fullName,int YCoord, int XCoord){

        BitmapFactory.Options bitmapOptions =new BitmapFactory.Options();
        Bitmap newBitmap;
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.mipmap.samplecertificate,bitmapOptions);

        Bitmap.Config config = bm1.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);


        newCanvas.drawBitmap(bm1, 0, 0, null);

        if(fullName != null){

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.BLACK);
            paintText.setTextSize(100);
            paintText.setTypeface(Typeface.create("Impact", Typeface.BOLD));
            paintText.setStyle(Paint.Style.FILL);

            Rect rectText = new Rect();
            paintText.getTextBounds(fullName, 0, fullName.length(), rectText);

            newCanvas.drawText(fullName, XCoord, YCoord, paintText);


        }else{
            Toast.makeText(getApplicationContext(),
                    "caption empty!",
                    Toast.LENGTH_LONG).show();
        }
        imageView.setImageBitmap(newBitmap);
        DownladCertificate(newBitmap);
        return newBitmap;
    }
}
