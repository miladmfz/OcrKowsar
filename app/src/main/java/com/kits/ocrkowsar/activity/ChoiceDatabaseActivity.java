package com.kits.ocrkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.application.App;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.Activation;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ChoiceDatabaseActivity extends AppCompatActivity {

    APIInterface apiInterface ;
    CallMethod callMethod;
    Activation activation;
    boolean getdb=true;
    DatabaseHelper dbh;
    ArrayList<String> servers;
    ArrayList<String> sqlsurl;
    ArrayList<String> persiancompanynames;
    ArrayList<String> englishcompanynames;

    TextView tv_rep;
    TextView tv_step;
    Dialog dialog;

    LinearLayoutCompat active_line;
    TextView active_edt;
    Button active_btn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_database);

        Config();
        init();


    }

    //*****************************************************************************************
    public void Config() {
        apiInterface = APIClient.getCleint("http://87.107.78.234:60005/login/").create(APIInterface.class);
        callMethod = new CallMethod(this);
        dialog = new Dialog(this);
        active_line = findViewById(R.id.activition_line);
        active_edt = findViewById(R.id.activition_edittext);
        active_btn = findViewById(R.id.activition_btn);
        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);

        servers = callMethod.getArrayList("ServerURLs");
        sqlsurl = callMethod.getArrayList("SQLiteURLs");
        persiancompanynames = callMethod.getArrayList("PersianCompanyNames");
        englishcompanynames = callMethod.getArrayList("EnglishCompanyNames");

    }

    @SuppressLint("SdCardPath")
    public void init() {


        if (!callMethod.ReadString("PersianCompanyNames").equals("[]")) {

            for (String string : callMethod.getArrayList("PersianCompanyNames")) {

                MaterialButton Button = new MaterialButton(this);
                Button.setText(string);
                Button.setBackgroundResource(R.color.white);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 10, 30, 10);

                Button.setTextSize(30);
                Button.setGravity(Gravity.CENTER);
                Button.setOnClickListener(v -> {
                    int count=persiancompanynames.indexOf(string);

                    callMethod.EditString("PersianCompanyNameUse", persiancompanynames.get(count));
                    callMethod.EditString("EnglishCompanyNameUse", englishcompanynames.get(count));
                    callMethod.EditString("ServerURLUse", servers.get(count));

                    File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                    File databasefile = new File(databasedir, "/KowsarDb.sqlite");
                    //Create Output file in Main File
                    if (!databasefile.exists()) {
                        DownloadRequest(sqlsurl.get(count),databasedir,databasefile);
                    } else {
                        callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.ocrkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                        intent = new Intent(this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                active_line.addView(Button, layoutParams);
            }
        }


        active_btn.setOnClickListener(v -> {

            Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", active_edt.getText().toString());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        if (callMethod.ReadString("PersianCompanyNames").equals("")) {
                            servers = new ArrayList<>();
                            sqlsurl = new ArrayList<>();
                            persiancompanynames = new ArrayList<>();
                            englishcompanynames = new ArrayList<>();
                        }else {
                            for (String string : englishcompanynames) {
                                if (string.equals(activation.getEnglishCompanyName())) {
                                    callMethod.showToast("این کد ثبت شده است");
                                    getdb = false;
                                    break;
                                }
                            }
                        }
                        if(getdb){
                            saveactivation();
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("test",t.getMessage());
                }
            });

        });


    }

    public void DownloadRequest(String url,File databasedir, File databasefile) {

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config1 = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config1);

        PRDownloader.download(url, databasedir.getPath(), databasefile.getName())
                .build()
                .setOnStartOrResumeListener(() -> {
                    dialog.show();
                    dialog.setCancelable(false);
                })
                .setOnPauseListener(() -> {})
                .setOnCancelListener(() -> {})
                .setOnProgressListener(progress -> {
                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setText(NumberFunctions.PerisanNumber((((progress.currentBytes)*100)/progress.totalBytes)+"/100"));
                })
                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override
                    public void onDownloadComplete() {
                        dialog.dismiss();
                        callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.ocrkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                        dbh = new DatabaseHelper(App.getContext(), callMethod.ReadString("UseSQLiteURL"));
                        dbh.DatabaseCreate();
                        intent = new Intent(App.getContext(), SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("test",error.toString());
                    }


                });
    }


    public void saveactivation() {

        servers.add(activation.getServerURL());
        sqlsurl.add(activation.getSQLiteURL());
        persiancompanynames.add(activation.getPersianCompanyName());
        englishcompanynames.add(activation.getEnglishCompanyName());
        callMethod.saveArrayList(servers, "ServerURLs");
        callMethod.saveArrayList(sqlsurl, "SQLiteURLs");
        callMethod.saveArrayList(persiancompanynames, "PersianCompanyNames");
        callMethod.saveArrayList(englishcompanynames, "EnglishCompanyNames");
        finish();
        startActivity(getIntent());

    }


}