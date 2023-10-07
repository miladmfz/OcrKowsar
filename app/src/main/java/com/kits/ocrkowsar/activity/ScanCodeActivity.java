package com.kits.ocrkowsar.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.kits.ocrkowsar.application.CallMethod;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    CallMethod callMethod;
    Intent intent;
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        callMethod=new CallMethod(this);

        String scan_result = result.getText();

        if(callMethod.ReadString("Category").equals("1")){
            intent = new Intent(ScanCodeActivity.this, ConfirmActivity.class);
            intent.putExtra("ScanResponse", scan_result);
        }else {
            intent = new Intent(ScanCodeActivity.this, FactorActivity.class);
            intent.putExtra("ScanResponse", scan_result);
            intent.putExtra("FactorImage", "");
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();


    }
}
