package com.kits.ocrkowsar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.application.App;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;

import java.util.ArrayList;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    DatabaseHelper dbhbase;
    Intent intent;
    Handler handler;
    final int PERMISSION_CODE = 1;
    CallMethod callMethod;
    final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

    }



    @SuppressLint("SdCardPath")
    public void init() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        callMethod.EditString("Last_search", "");
        callMethod.EditString("LastTcPrint","0");

        if (callMethod.firstStart()) {
            callMethod.EditString("Deliverer",  "پیش فرض");
            callMethod.EditString("Category", "0");
            callMethod.EditString("StackCategory", "همه");
            callMethod.EditString("ConditionPosition", "0");
            callMethod.EditString("TitleSize", "22");
            callMethod.EditString("LastTcPrint", "0");
            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditBoolan("ArabicText", true);


            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("DatabaseName", "");
            callMethod.saveArrayList(new ArrayList<>(), "ServerURLs");
            callMethod.saveArrayList(new ArrayList<>(), "SQLiteURLs");
            callMethod.saveArrayList(new ArrayList<>(), "PersianCompanyNames");
            callMethod.saveArrayList(new ArrayList<>(), "EnglishCompanyNames");
            dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.ocrkowsar/databases/KowsarDb.sqlite");
            dbhbase.CreateActivationDb();


        }

        requestPermission();

    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else {
                runtimePermission();
            }

        } else {
            runtimePermission();
        }
    }
    private void Startapplication() {

        if (callMethod.ReadString("DatabaseName").equals("")) {
            handler = new Handler();
            handler.postDelayed(() -> {
                intent = new Intent(this, ChoiceDatabaseActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else {


            handler = new Handler();
            handler.postDelayed(() -> {
                try {
                    dbh.DeleteLastWeek();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent = new Intent(this, NavActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        }
    }

    private void runtimePermission() {
        try {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Startapplication();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        }catch (Exception e){
            Log.e("test",e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    runtimePermission();
                    callMethod.showToast("مجوز صادر شد");

                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }, 2000);
                    callMethod.showToast("مجوز مربوطه را فعال نمایید");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod.showToast("permission granted");
            } else {
                callMethod.showToast("permission denied");
            }
            requestPermission();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }



}
