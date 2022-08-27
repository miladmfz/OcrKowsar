package com.kits.ocrkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.OcrFactorList_Adapter;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OcrFactorListActivity extends AppCompatActivity {

    APIInterface apiInterface;
    OcrFactorList_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView factor_list_recycler;
    AppCompatEditText edtsearch;
    Handler handler;
    ArrayList<Factor> factors=new ArrayList<>();
    String srch="";
    TextView textView_Count;
    String state="0",StateEdited="0",StateShortage="0";

    Dialog dialog1;
    SwitchMaterial RadioEdited;
    SwitchMaterial RadioShortage;
    Spinner spinnerPath;
    String filter="0";
    String path="همه";
    ArrayList<String> customerpath=new ArrayList<>();
    Intent intent;
    NotificationManager notificationManager;
    String channel_id = "Kowsarmobile";
    String channel_name = "home";
    CallMethod callMethod;
    DatabaseHelper dbh;
    int recallcount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_factor_list);

        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();




        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }



    }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        state = bundle.getString("State");
        StateEdited ="0";
        StateShortage ="0";

        if(state.equals("5"))
        {
            state = "0";
            StateEdited = bundle.getString("StateEdited");
            StateShortage = bundle.getString("StateShortage");
        }
    }

    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        handler=new Handler();

        Toolbar toolbar = findViewById(R.id.factor_listActivity_toolbar);
        setSupportActionBar(toolbar);

        factor_list_recycler=findViewById(R.id.factor_listActivity_recyclerView);
        textView_Count=findViewById(R.id.factorlistActivity_count);
        edtsearch = findViewById(R.id.factorlistActivity_edtsearch);
        RadioEdited= findViewById(R.id.factorlistActivity_edited);
        RadioShortage= findViewById(R.id.factorlistActivity_shortage);
        spinnerPath= findViewById(R.id.factorlistActivity_path);

    }

    public void SetViews(){
        spinnerPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                path=customerpath.get(position);
                callMethod.EditString("ConditionPosition",String.valueOf(position));
                callrecycle();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        RadioShortage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) StateShortage="1"; else  StateShortage="0";
            if(StateEdited.equals("0")){
                if(StateShortage.equals("0")) filter="0"; else  filter="1";
            }else {
                if(StateShortage.equals("0")) filter="2"; else  filter="3";
            }
            callrecycle();
        });
        RadioEdited.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) StateEdited="1"; else  StateEdited="0";
            if(StateEdited.equals("0")){
                if(StateShortage.equals("0")) filter="0"; else  filter="1";
            }else {
                if(StateShortage.equals("0")) filter="2"; else  filter="3";
            }
            callrecycle();
        });

        if(StateEdited.equals("0")){
            if(StateShortage.equals("0")) {
                filter="0";
            }else {
                filter="1";
            }
        }else {
            if(StateShortage.equals("0")) {
                filter="2";
            }else {
                filter="3";
            }
        }
    }



    public void init(){


        customerpath.add("همه");

        if(!state.equals("0")){
            RadioEdited.setVisibility(View.GONE);
            RadioShortage.setVisibility(View.GONE);
        }

        srch=callMethod.ReadString("Last_search");

        edtsearch.setText(srch);
        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged( Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            srch = NumberFunctions.EnglishNumber(dbh.GetRegionText(editable.toString()));
                            callMethod.EditString("Last_search", srch);
                            retrofitrequset();
                        }, 1000);

                    }
                });


        RadioEdited.setChecked(StateEdited.equals("1"));
        RadioShortage.setChecked(StateShortage.equals("1"));


        retrofitrequset();
    }




    public void callrecycle() {

        adapter = new OcrFactorList_Adapter(factors,state, filter,path,OcrFactorListActivity.this);
        if (adapter.getItemCount()==0){
            callMethod.showToast("فاکتوری یافت نشد");
        }

        textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(adapter.getItemCount())));
        gridLayoutManager = new GridLayoutManager(this, 1);//grid
        factor_list_recycler.setLayoutManager(gridLayoutManager);
        factor_list_recycler.setAdapter(adapter);
        factor_list_recycler.setItemAnimator(new DefaultItemAnimator());

        if (Integer.parseInt(callMethod.ReadString("LastTcPrint"))>0){
            for (Factor singlefactor :factors) {
                if(singlefactor.getAppTcPrintRef().equals(callMethod.ReadString("LastTcPrint")))
                    factor_list_recycler.scrollToPosition(factors.indexOf(singlefactor));
            }

        }

        dialog1.dismiss();


    }

    public void retrofitpath() {

        Call<RetrofitResponse> call =apiInterface.GetCustomerPath("GetCustomerPath");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                if (response.isSuccessful()) {
                    recallcount=0;
                    assert response.body() != null;
                    for (Factor factor : response.body().getFactors()) {
                        customerpath.add(factor.getCustomerPath());
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(OcrFactorListActivity.this,
                            android.R.layout.simple_spinner_item, customerpath);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPath.setAdapter(spinner_adapter);

                    try {
                        if (customerpath.size() < Integer.parseInt(callMethod.ReadString("ConditionPosition"))) {
                            callMethod.EditString("ConditionPosition", "0");
                        }
                        spinnerPath.setSelection(Integer.parseInt(callMethod.ReadString("ConditionPosition")));
                    } catch (Exception e) {
                        spinnerPath.setSelection(0);
                    }

                    int ShortageCount=0;
                    int EditedCount=0;
                    String Titlequery="";
                    String Bodyquery="";
                    for(Factor factor:factors){
                        if(factor.getHasShortage().equals("1")){
                            ShortageCount++;
                        }
                        if(factor.getIsEdited().equals("1")){
                            EditedCount++;
                        }
                    }

                    if (ShortageCount>0){
                        Titlequery=Titlequery+"  کسری  ";
                        Bodyquery=Bodyquery+"(دارای "+NumberFunctions.PerisanNumber(String.valueOf(ShortageCount))+" فکتور کسری)";
                    }
                    if (EditedCount>0){
                        Titlequery=Titlequery+"  اصلاحی  ";
                        Bodyquery=Bodyquery+"(دارای "+NumberFunctions.PerisanNumber(String.valueOf(EditedCount))+" فکتور اصلاحی)";
                    }
                    if(!Titlequery.equals(""))
                        noti_Messaging(Titlequery, Bodyquery,"0");
                    SetViews();
                }

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                recallcount++;
                if(recallcount<6){
                    retrofitrequset();
                }else{
                    finish();
                    callMethod.showToast("فاکتوری موجود نمی باشد");
                    Log.e("",t.getMessage());
                }

            }
        });
    }

    public void retrofitrequset() {
        Call<RetrofitResponse> call;

        if (state.equals("0")){
            if (!callMethod.ReadString("StackCategory").equals("همه")) {
                call = apiInterface.GetOcrFactorList("GetFactorList", state, srch, callMethod.ReadString("StackCategory"));
            }else {
                call = apiInterface.GetOcrFactorList("GetFactorList", state, srch, "");
            }
        }else{
            call =apiInterface.GetOcrFactorList("GetFactorList",state,srch,"");
        }


        if(state.equals("0")){

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if(response.isSuccessful()) {

                        assert response.body() != null;
                        factors= response.body().getFactors();
                        if(factors.size()>0){
                            Log.e("test11","0");

                            callrecycle();
                            retrofitpath();
                            Log.e("test11","1");



                        }else {
                            finish();
                            callMethod.showToast("فاکتوری موجود نمی باشد");
                        }

                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    recallcount++;
                    if(recallcount<2){
                        retrofitrequset();
                    }else if (recallcount==2){

                        callMethod.EditString("Last_search", "");
                        srch=callMethod.ReadString("Last_search");
                        edtsearch.setText(srch);
                        retrofitrequset();
                    }else {
                        finish();
                        callMethod.showToast("فاکتوری موجود نمی باشد");
                        Log.e("",t.getMessage());
                    }
                }
            });
        }else {

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                    if(response.isSuccessful()) {
                        assert response.body() != null;
                        recallcount=0;
                        factors= response.body().getFactors();
                        if(factors.size()>0){

                            callrecycle();
                            retrofitpath();
                        }else {

                            finish();
                            callMethod.showToast("فاکتوری موجود نمی باشد");
                        }

                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    recallcount++;
                    if(recallcount<2){
                        retrofitrequset();
                    }else if (recallcount==2){
                        callMethod.EditString("Last_search", "");
                        srch=callMethod.ReadString("Last_search");
                        edtsearch.setText(srch);
                        retrofitrequset();
                    }else {
                        finish();
                        callMethod.showToast("فاکتوری موجود نمی باشد");
                        Log.e("",t.getMessage());
                    }
                }
            });


        }


    }





    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, OcrFactorListActivity.class);
        intent.putExtra("State", state);
        startActivity(intent);
        finish();

    }


    public void noti_Messaging(String title, String message,String flag) {

        notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel Channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(Channel);
        }
        Intent notificationIntent = new Intent(this, OcrFactorListActivity.class);
        notificationIntent.putExtra("State", "5");
        if(flag.equals("0")){
            notificationIntent.putExtra("StateEdited", "0");
            notificationIntent.putExtra("StateShortage", "1");
        }else {
            notificationIntent.putExtra("StateEdited", "1");
            notificationIntent.putExtra("StateShortage", "0");

        }


        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notcompat = new NotificationCompat.Builder(this, channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(false)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(contentIntent);

        notificationManager.notify(1, notcompat.build());
    }


}