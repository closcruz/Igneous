package com.example.jcgc.igneous;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //private static final int REQUEST_CAMERA = 1;
    //private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //scannerView = new ZXingScannerView(this);
        //setContentView(scannerView);
        onScan();
    }

    public void onScan(){

        Toast.makeText(MainActivity.this, "Calling ScanActivity!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Start Scan");
        startActivity(intent);
    }

}
