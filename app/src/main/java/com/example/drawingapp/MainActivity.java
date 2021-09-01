package com.example.drawingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    int defaultcolor;
    SignatureView signatureView;
    ImageButton btnsave,btnclr,btneraser;
    SeekBar seekBar;
    TextView txtpenSize;

    private static String filename;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Paintings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signatureView = findViewById(R.id.signature_view);
        btnsave = findViewById(R.id.btnsave);
        btnclr = findViewById(R.id.btnclr);
        btneraser = findViewById(R.id.btneraser);
        seekBar = findViewById(R.id.penSize);
        txtpenSize = findViewById(R.id.txtpenSize);

        askpermission();

        SimpleDateFormat format=new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String date= format.format(new Date());
        filename = path + "/" + date + ".png";

        if (!path.exists()){
            path.mkdirs();
        }
        defaultcolor = ContextCompat.getColor(MainActivity.this, R.color.black);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtpenSize.setText(progress +"dp");
                signatureView.setPenSize(progress);
                seekBar.setMax(80);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnclr.setOnClickListener(v -> opencolorpicker());
        btneraser.setOnClickListener(v -> {
            signatureView.clearCanvas();
        });
        btnsave.setOnClickListener(v -> {
            if (!signatureView.isBitmapEmpty()){
                try {
                    saveiamge();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"Coudn't Save!",Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void saveiamge() throws IOException {
        File file = new File(filename);

        Bitmap bitmap = signatureView.getSignatureBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,99,byteArrayOutputStream);

        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bitmapData);
        fileOutputStream.flush();
        fileOutputStream.close();
        Toast.makeText(this,"Painting Saved!",Toast.LENGTH_LONG).show();
    }

    private void opencolorpicker() {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultcolor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                defaultcolor = color;
                signatureView.setPenColor(color);
            }
        });
            ambilWarnaDialog.show();
    }

    private void askpermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener(){

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this,"Granted.",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
}