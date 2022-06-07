package com.example.tricoins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tricoins.admin.AllWinningItems;
import com.google.zxing.WriterException;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class ItemDetails extends AppCompatActivity {
    String TAG = "GenerateQRCode";
    ImageView qrImage;
    Button start, save;
    String inputValue;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    static Bitmap bitmap;
    QRGEncoder qrgEncoder;
    TextView textdate,textdigit,texttimecap,texttype;


    private Printing printing = null;
    PrintingCallback printingCallback=null;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        sharedPreferences=this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String edtValue=sharedPreferences.getString("qrcode", "0");

        qrImage = findViewById(R.id.QR_Image);
        start = findViewById(R.id.start);
        save = findViewById(R.id.save);
        textdate= findViewById(R.id.text_date);
        textdigit= findViewById(R.id.text_digit);
        texttimecap= findViewById(R.id.text_timecap);
        texttype=findViewById(R.id.text_type);
        inputValue = edtValue.trim();
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.getBitmap();
                qrImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
        }

        textdate.setText(sharedPreferences.getString("Sysddate", "0"));
        textdigit.setText(sharedPreferences.getString("Digits", "0"));
        texttimecap.setText(sharedPreferences.getString("TimeCap", "0"));
        texttype.setText(sharedPreferences.getString("Types", "0"));
        Log.d("xxx", "onCreate ");

        Printooth.INSTANCE.init(this);
        if (Printooth.INSTANCE.hasPairedPrinter())
            printing = Printooth.INSTANCE.printer();
        initViews();
        initListeners();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* boolean save;
                String result;
                try {
                    QRGSaver qrgSaver = new QRGSaver();
                    save=qrgSaver.save(savePath, edtValue.trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                if(!Printooth.INSTANCE.hasPairedPrinter())
                {
                    startActivityForResult(new Intent(ItemDetails.this, ScanningActivity.class ),ScanningActivity.SCANNING_FOR_PRINTER);
                }
                else
                {
                    printImages();
                }
            }
        });


    }
    private void initViews() {
        if (!Printooth.INSTANCE.hasPairedPrinter()) {
            Printooth.INSTANCE.removeCurrentPrinter();
        }

    }

    private void printImages() {
        if (printing != null) {
            Log.d("xxx", "printSomeImages ");
            ArrayList<Printable> al = new ArrayList<>();
            Resources resources = getResources();
            //getContext();
            Bitmap image = BitmapFactory.decodeResource(resources,R.drawable.ic_baseline_arrow_downward_24);
            al.add(new ImagePrintable.Builder(image).build());
            printing.print(al);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("xxx", "onActivityResult "+requestCode);

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initListeners();
        }
        initViews();
    }

    private void initListeners() {
        if (printing!=null && printingCallback==null) {
            Log.d("xxx", "initListeners ");
            printingCallback = new PrintingCallback() {

                public void connectingWithPrinter() {
                    Toast.makeText(getApplicationContext(), "Connecting with printer", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "Connecting");
                }
                public void printingOrderSentSuccessfully() {
                    Toast.makeText(getApplicationContext(), "printingOrderSentSuccessfully", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "printingOrderSentSuccessfully");
                }
                public void connectionFailed(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "connectionFailed :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "connectionFailed : "+error);
                }
                public void onError(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "onError :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onError : "+error);
                }
                public void onMessage(@NonNull String message) {
                    Toast.makeText(getApplicationContext(), "onMessage :" +message, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onMessage : "+message);
                }
            };

            Printooth.INSTANCE.printer().setPrintingCallback(printingCallback);
        }
    }




    //on press back button
    public void onBackEmail(View view)
    {
        if(sharedPreferences.getString("1detailsback1", "0").equals("1")) {
            Intent i = new Intent(getApplicationContext(), AllWinningItems.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
            finish();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), AllItems.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
            finish();
        }
    }
}