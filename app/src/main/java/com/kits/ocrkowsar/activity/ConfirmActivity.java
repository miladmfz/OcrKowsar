package com.kits.ocrkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.ocrkowsar.Fragment.CollectFragment;
import com.kits.ocrkowsar.Fragment.PackFragment;
import com.kits.ocrkowsar.Fragment.StackFragment;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {
    Integer state_category;
    public String searchtarget = "";
    APIInterface apiInterface;
    APIInterface secendApiInterface;
    EditText ed_barcode;
    DatabaseHelper dbh ;
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    LinearLayoutCompat ll_main;
    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    CollectFragment collectFragment;
    StackFragment stackFragment;

    PackFragment packFragment;
    ArrayList<Good> goods=new ArrayList<>();
    ArrayList<Good> goods_scan=new ArrayList<>();
    Factor factor;
    String BarcodeScan;
    String OrderBy;
    String State;
    int width=1;
    Action action;
    Call<RetrofitResponse> call;
    Handler handler;
    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }


    }
    ////////////////////////////////////////////////////


    public  void intent(){

        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
        State=bundle.getString("State");


    }


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        action = new Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);

        handler=new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.confirm_layout);
        ed_barcode = findViewById(R.id.confirmActivity_barcode);
        progressBar = findViewById(R.id.stackfragment_good_prog);
        img_lottiestatus = findViewById(R.id.stackfragment_good_lottie);
        tv_lottiestatus = findViewById(R.id.stackfragment_good_tvstatus);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        collectFragment = new CollectFragment();
        packFragment = new PackFragment();
        stackFragment = new StackFragment();
        collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);
        stackFragment.setBarcodeScan(BarcodeScan);
        goods_scan.clear();

    }


    public void Collect_Pack(){

        Log.e("kowsar","Collect_Pack");

        ed_barcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {
                        //String barcode1 = editable.toString().substring(2).replace("\n", "");
                        if (goods.size() > 0) {

                            goods_scan.clear();
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(() -> {
                                String barcode = NumberFunctions.EnglishNumber(editable.toString().substring(2,editable.toString().length()-2).replace("\n", ""));

                                ed_barcode.selectAll();

                                for (Good singlegood : goods) {
                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
                                        goods_scan.add(singlegood);
                                    }

                                }

                                action.GoodScanDetail(goods_scan,State,BarcodeScan);
                            },  Integer.parseInt(callMethod.ReadString("Delay")));
                        }
                    }

                }
        );





        if(State.equals("0")){
            OrderBy="GoodExplain1";
        }else{
            OrderBy="GoodName";
        }


        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }else{
            call=secendApiInterface.GetFactor("Getocrfactor",BarcodeScan,OrderBy);
        }

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    factor = response.body().getFactor();
                    if (factor.getFactorCode().equals("0")) {
                        callMethod.showToast("لطفا مجددا اسکن کنید");
                        finish();
                    } else {
                        goods = response.body().getGoods();
                        if (factor.getAppIsControled().equals("0")) {
                            collectFragment.setFactor(factor);
                            collectFragment.setGoods(goods);
                            fragmentTransaction.replace(R.id.confirm_framelayout, collectFragment);
                            fragmentTransaction.commit();
                        } else if (factor.getAppIsPacked().equals("0")) {
                            packFragment.setFactor(factor);
                            packFragment.setGoods(goods);
                            fragmentTransaction.replace(R.id.confirm_framelayout, packFragment);
                            fragmentTransaction.commit();
                        } else {
                            finish();
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.showToast("Connection fail ...!!!");
            }
        });

        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }


    public void StackLocation(){

        tv_lottiestatus.setText("اسکن کنید");
        tv_lottiestatus.setVisibility(View.VISIBLE);
        if (BarcodeScan.length()>0){
            Log.e("kowsar","0");

            progressBar.setVisibility(View.VISIBLE);
            tv_lottiestatus.setText("در حال جستجو");
            tv_lottiestatus.setVisibility(View.VISIBLE);
            ed_barcode.setText(BarcodeScan);
            ed_barcode.selectAll();
            Search_call();
        }
        Log.e("kowsar","1");
        ed_barcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 100));
        ed_barcode.setPadding(5, 5, 5, 5);



        Log.e("kowsar","StackLocation");
        img_lottiestatus.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        ed_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_barcode.setFocusable(true);
                ed_barcode.requestFocus();
                ed_barcode.selectAll();
            }
        });


        tv_lottiestatus.setOnClickListener(view -> {
             Intent intent = new Intent(this, ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });
        ed_barcode.addTextChangedListener(
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


                                if (ed_barcode.getText().toString().length()>0){

                                    Search_call();

                                }else {
                                    if (goods.size()> 0) {
                                        goods.clear();
                                    }

                                    img_lottiestatus.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    tv_lottiestatus.setText("اسکن کنید");
                                    tv_lottiestatus.setVisibility(View.VISIBLE);
                                }



                            },  Integer.parseInt(callMethod.ReadString("Delay")));




                    }
                }
        );



        ed_barcode.setFocusable(true);
        ed_barcode.requestFocus();
    }

    public void Search_call(){
        searchtarget = NumberFunctions.EnglishNumber(ed_barcode.getText().toString());
        searchtarget = searchtarget.replaceAll(" ", "%");




        call=apiInterface.GetOcrGoodList("GetOcrGoodList",searchtarget);
        Log.e("kowsar","searchtarget = "+searchtarget);


        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("kowsar","StackLocation = "+response.body().getGoods().size());

                    goods = response.body().getGoods();

                    if (goods.size()> 0) {
                        try {
                            Log.e("kowsar","0 ");
                            img_lottiestatus.setVisibility(View.GONE);
                            tv_lottiestatus.setText("اسکن کنید");
                            tv_lottiestatus.setVisibility(View.VISIBLE);

                            stackFragment.setGoods(goods);

                            Log.e("kowsar","1 ");

                            fragmentTransaction.replace(R.id.confirm_framelayout, stackFragment);
                            fragmentTransaction.commit();
                            Log.e("kowsar","2 ");

                            progressBar.setVisibility(View.GONE);
                            Log.e("kowsar","3 ");

                        }catch (Exception e){
                            Log.e("kowsar","4 " +e.getMessage());


                        }


                    } else {

                        tv_lottiestatus.setText("موردی یافت نشد");
                        img_lottiestatus.setVisibility(View.VISIBLE);
                        tv_lottiestatus.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                callMethod.showToast("Connection fail ...!!!");
                tv_lottiestatus.setText("موردی یافت نشد");
                img_lottiestatus.setVisibility(View.VISIBLE);
                tv_lottiestatus.setVisibility(View.VISIBLE);
            }
        });
    }



        public void init(){

            try {
                state_category=Integer.parseInt(callMethod.ReadString("Category"));
            }catch (Exception e){
                state_category=0;
            }




            if(state_category==2){
                Collect_Pack();
            }else if(state_category==3){
                Collect_Pack();
            }else if(state_category==6){
                StackLocation();
            }







    }


}