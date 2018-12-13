package com.example.jcgc.igneous;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    public Decrypt DecryptObj = new Decrypt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        /*Toast.makeText(ScanActivity.this, message, Toast.LENGTH_LONG).show();*/

        ScanAction();

    }

    public void ScanAction() {

        //scannerView = new ZXingScannerView(this);

        //setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(ScanActivity.this, "Permission is granted!", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }


    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA)) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestCode, String permission[], int grantResults[]) {
        switch (requestCode) {
            case REQUEST_CAMERA :
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(ScanActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ScanActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                displayAlertMessage("You need to allow access for both permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ScanActivity.this)
                .setMessage(msg)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {

        Boolean decryptRun;
        String scanResult = result.getText();

        //Check if Base64 String
        decryptRun = isBase64Encoded(scanResult);
        //If true then execute decryption
        if(decryptRun == true)
        {
            scanResult = DecryptScan(scanResult);
        }

        String[] data = scanResult.split("\\^");
        String out = "";
        for (int i = 0; i < data.length; i++) {
            out += data[i];
        }

        final String finalResult = scanResult;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalResult));
                startActivity(intent);
            }
        });
        builder.setMessage(finalResult);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String DecryptScan(String EncryptedString)
    {
        try {
            System.out.println(EncryptedString);
            String DecryptedString = DecryptObj.decrypt(EncryptedString, "simba");
            System.out.println(DecryptedString);
            return DecryptedString;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  null;
    }

    public Boolean isBase64Encoded(String str)
    {
        try
        {
            // If no exception is caught, then it is possibly a base64 encoded string
            byte[] data = str.getBytes();
            // The part that checks if the string was properly padded to the
            // correct length was borrowed from d@anish's solution
            return (str.replace(" ","").length() % 4 == 0);
        }
        catch(Exception e)
        {
            // If exception is caught, then it is not a base64 encoded string
            return false;
        }
    }
}
