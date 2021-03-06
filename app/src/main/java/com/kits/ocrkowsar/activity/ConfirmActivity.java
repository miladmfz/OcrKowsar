package com.kits.ocrkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kits.ocrkowsar.Fragment.CollectFragment;
import com.kits.ocrkowsar.Fragment.PackFragment;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.application.App;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {
    APIInterface apiInterface;
    EditText ed_barcode;
    DatabaseHelper dbh ;
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    LinearLayoutCompat ll_main;

    CallMethod callMethod;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    CollectFragment collectFragment;
    PackFragment packFragment;

    ArrayList<Good> goods;
    ArrayList<Good> goods_scan=new ArrayList<>();
    Factor factor;
    String BarcodeScan;
    String State;
    int correctgood=0;
    Intent intent;
    int width=1;
    Action action;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("???? ?????? ???????????? ??????????????");
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
        handler=new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.confirm_layout);
        ed_barcode = findViewById(R.id.confirmActivity_barcode);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        collectFragment = new CollectFragment();
        packFragment = new PackFragment();
        collectFragment.setBarcodeScan(BarcodeScan);
        packFragment.setBarcodeScan(BarcodeScan);
        goods_scan.clear();
    }




    public void init(){



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

                        if (goods.size() > 0) {

                            goods_scan.clear();
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(() -> {
                                ed_barcode.selectAll();
                                String barcode = editable.toString().substring(2).replace("\n", "");
                                Log.e("test_serch",editable.toString());

                                for (Good singlegood : goods) {
                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
                                        goods_scan.add(singlegood);
                                    }

                                }
                                Log.e("test_serch",goods_scan.size()+"");
                                Log.e("test_serch",State);
                                Log.e("test_serch",BarcodeScan);
                                action.GoodScanDetail(goods_scan,State,BarcodeScan);
                            }, 200);
                        }
                    }

//
//
//                                for (Good singlegood : goods) {
//                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
//                                        if (State.equals("0")){
//                                            if (singlegood.getAppRowIsControled().equals("0")){
//                                                goods_scan.add(singlegood);
//                                                TransationFromBarcode(singlegood);
//                                            }else {
//                                                callMethod.showToast("???????? ?????????? ?????? ??????");
//                                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                                intent.putExtra("ScanResponse", BarcodeScan);
//                                                intent.putExtra("State", State);
//                                                finish();
//                                                startActivity(intent);
//                                            }
//
//                                        }else if(State.equals("1")){
//                                            if (singlegood.getAppRowIsPacked().equals("0")){
//                                                State="2";
//                                                TransationFromBarcode(singlegood);
//                                            }else {
//                                                callMethod.showToast("???????? ?????????? ?????? ??????");
//                                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                                intent.putExtra("ScanResponse", BarcodeScan);
//                                                intent.putExtra("State", State);
//                                                finish();
//                                                startActivity(intent);
//                                            }
//                                        }
//                                    } else{
//                                        intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                        intent.putExtra("ScanResponse", BarcodeScan);
//                                        intent.putExtra("State", State);
//                                        finish();
//                                        startActivity(intent);
//                                    }
//                                }
//                                if (correctgood==0){
//                                    callMethod.showToast("???????????? ???????? ??????");
//                                }
//
//                            }, 200);
//                        }
//                    }
//
//



                    //}
//                    @Override
//                    public void afterTextChanged( Editable editable) {
//                        if(goods.size()>0) {
//                            handler.removeCallbacksAndMessages(null);
//                            handler.postDelayed(() -> {
//
//                                String barcode = editable.toString().substring(2);
//                                barcode = barcode.replace("\n", "");
//
//                                for (Good singlegood : goods) {
//                                    if (singlegood.getCachedBarCode().indexOf(barcode) > 0) {
//                                        correctgood++;
//                                        if (State.equals("0")){
//                                            if (singlegood.getAppRowIsControled().equals("0")){
//                                                TransationFromBarcode(singlegood);
//                                            }else {
//                                                callMethod.showToast("???????? ?????????? ?????? ??????");
//                                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                                intent.putExtra("ScanResponse", BarcodeScan);
//                                                intent.putExtra("State", State);
//                                                finish();
//                                                startActivity(intent);
//                                            }
//
//                                        }else if(State.equals("1")){
//                                            if (singlegood.getAppRowIsPacked().equals("0")){
//                                                State="2";
//                                                TransationFromBarcode(singlegood);
//                                            }else {
//                                                callMethod.showToast("???????? ?????????? ?????? ??????");
//                                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                                intent.putExtra("ScanResponse", BarcodeScan);
//                                                intent.putExtra("State", State);
//                                                finish();
//                                                startActivity(intent);
//                                            }
//                                        }
//                                    } else{
//                                        intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
//                                        intent.putExtra("ScanResponse", BarcodeScan);
//                                        intent.putExtra("State", State);
//                                        finish();
//                                        startActivity(intent);
//                                    }
//                                }
//                                if (correctgood==0){
//                                    callMethod.showToast("???????????? ???????? ??????");
//                                }
//
//                            }, 200);
//                        }
//                    }
//



                });


        Call<RetrofitResponse> call =apiInterface.GetFactor("Getocrfactor",BarcodeScan);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    factor = response.body().getFactor();
                    if (factor.getFactorCode().equals("0")) {
                        callMethod.showToast("???????? ?????????? ???????? ????????");
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


}