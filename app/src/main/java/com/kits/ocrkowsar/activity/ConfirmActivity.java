package com.kits.ocrkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
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
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener {
    APIInterface apiInterface;
    String date="";
    TextView ed_pack_h_date;
    DatabaseHelper dbh ;
    ArrayList<String> GoodCodeCheck=new ArrayList<>();
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    Dialog dialog;
    LinearLayoutCompat ll_main;
    LinearLayoutCompat ll_title;
    LinearLayoutCompat ll_good_body_detail;
    LinearLayoutCompat ll_good_body;
    LinearLayoutCompat ll_factor_summary;
    LinearLayoutCompat ll_send_confirm;
    CallMethod callMethod;

    ViewPager ViewPager;
    Button btn_send;
    Button btn_confirm;
    Button btn_shortage;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Good> goods;
    Factor factor;
    String BarcodeScan;
    Integer ConfirmCounter=0;
    Integer lastCunter = 0;
    Intent intent;
    int width=1;
    int j;
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
    ////////////////////////////////////////////////////////////////////////////


    public  void intent(){
        Bundle bundle =getIntent().getExtras();
        assert bundle != null;
        BarcodeScan=bundle.getString("ScanResponse");
    }


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        for (final String[] s : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode","amount "});
        }

        ll_main = findViewById(R.id.confirm_layout);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;


    }


    public void init(){




        Call<RetrofitResponse> call =apiInterface.GetFactor("Getocrfactor",BarcodeScan);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                if(response.isSuccessful()){
                    assert response.body() !=null;
                    factor=response.body().getFactor();
                    if(factor.getFactorCode().equals("0"))
                    {
                        callMethod.showToast("لطفا مجددا اسکن کنید");
                        finish();
                    }else {
                        goods=response.body().getGoods();
                        if(factor.getAppIsControled().equals("0"))
                        {
                            CreateView_Control();
                        }else {
                            if(factor.getAppIsPacked().equals("0")) {
                                CreateView_Pack();
                            }else {
                                finish();
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                callMethod.showToast("Connection fail ...!!!");
                Log.e("123", Objects.requireNonNull(t.getMessage())); }
        });

    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_Control(){


        ll_title = new LinearLayoutCompat(getApplicationContext());
        ll_good_body = new LinearLayoutCompat(getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(getApplicationContext());
        ViewPager = new ViewPager(getApplicationContext());
        TextView tv_company = new TextView(getApplicationContext());
        TextView tv_customername = new TextView(getApplicationContext());
        TextView tv_factorcode = new TextView(getApplicationContext());
        TextView tv_factordate = new TextView(getApplicationContext());
        TextView tv_address = new TextView(getApplicationContext());
        TextView tv_phone = new TextView(getApplicationContext());
        TextView tv_total_amount = new TextView(getApplicationContext());
        TextView tv_total_price = new TextView(getApplicationContext());
        btn_confirm = new Button(getApplicationContext());
        btn_send = new Button(getApplicationContext());
        btn_shortage = new Button(getApplicationContext());

        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_address.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_phone.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

        tv_total_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_total_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));

        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_send_confirm.setWeightSum(2);

        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        tv_company.setGravity(Gravity.CENTER);
        tv_customername.setGravity(Gravity.RIGHT);
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        tv_address.setGravity(Gravity.RIGHT);
        tv_phone.setGravity(Gravity.RIGHT);
        tv_total_amount.setGravity(Gravity.RIGHT);
        tv_total_price.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);

        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        btn_shortage.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_shortage.setBackgroundResource(R.color.orange_500);

        tv_company.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_address.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_phone.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_total_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_total_price.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(getResources().getColor(R.color.white));
        btn_send.setTextColor(getResources().getColor(R.color.white));
        btn_shortage.setTextColor(getResources().getColor(R.color.Black));

        tv_company.setText(NumberFunctions.PerisanNumber("بخش انبار"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));
        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");

        tv_company.setPadding(0, 0, 30, 20);
        tv_customername.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        tv_address.setPadding(0, 0, 30, 20);
        tv_phone.setPadding(0, 0, 30, 20);
        tv_total_amount.setPadding(0, 0, 30, 20);
        tv_total_price.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);


        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){
            TextView tv_total_newprice = new TextView(getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            tv_total_newprice.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        j= 0;
        for (Good g : goods) {
            if(callMethod.ReadString("StackCategory").equals("همه")) {
                goodshow(g);
            }else if(g.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                goodshow(g);
            }
        }

        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_address);
        ll_title.addView(tv_phone);
        ll_title.addView(ViewPager);
        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_factor_summary.addView(tv_total_amount);
        ll_factor_summary.addView(tv_total_price);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);
        ConfirmCount_Control();

        btn_shortage.setOnClickListener(v -> {

        });


        btn_send.setOnClickListener(v -> {
            Call<RetrofitResponse> call =apiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    if(response.isSuccessful()) {

                        finish();

                    }
                }
                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                    Log.e("",t.getMessage()); }
            });            });

        btn_confirm.setOnClickListener(v -> {

            int b=GoodCodeCheck.size();
            final int[] conter = {0};

            for (String goodchecks : GoodCodeCheck) {

                Call<RetrofitResponse> call =apiInterface.CheckState("OcrControlled",goodchecks,"0","");
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                        if(response.isSuccessful()) {

                            conter[0] = conter[0] +1;
                            if(conter[0]==b){
                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("FactorImage", "");
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                        Log.e("",t.getMessage()); }
                });


            }

        });


        if(callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(ConfirmActivity.this, NavActivity.class);
                startActivity(intent);
                finish();
            });
        }





    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_Pack(){
        ll_main.removeAllViews();
        ll_title = new LinearLayoutCompat(getApplicationContext());
        ll_good_body = new LinearLayoutCompat(getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(getApplicationContext());
        ViewPager = new ViewPager(getApplicationContext());
        TextView tv_company = new TextView(getApplicationContext());
        TextView tv_customername = new TextView(getApplicationContext());
        TextView tv_factorcode = new TextView(getApplicationContext());
        TextView tv_factordate = new TextView(getApplicationContext());
        TextView tv_address = new TextView(getApplicationContext());
        TextView tv_phone = new TextView(getApplicationContext());
        TextView tv_total_amount = new TextView(getApplicationContext());
        TextView tv_total_price = new TextView(getApplicationContext());
        btn_confirm = new Button(getApplicationContext());
        btn_send = new Button(getApplicationContext());
        btn_shortage = new Button(getApplicationContext());

        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_address.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_phone.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

        tv_total_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_total_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));

        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_send_confirm.setWeightSum(2);

        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        tv_company.setGravity(Gravity.CENTER);
        tv_customername.setGravity(Gravity.RIGHT);
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        tv_address.setGravity(Gravity.RIGHT);
        tv_phone.setGravity(Gravity.RIGHT);
        tv_total_amount.setGravity(Gravity.RIGHT);
        tv_total_price.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);

        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_shortage.setBackgroundResource(R.color.orange_500);

        tv_company.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_address.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_phone.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_total_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_total_price.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(getResources().getColor(R.color.white));
        btn_send.setTextColor(getResources().getColor(R.color.white));
        btn_shortage.setTextColor(getResources().getColor(R.color.Black));

        tv_company.setText(NumberFunctions.PerisanNumber("بخش بسته بندی"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));
        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_shortage.setText("اعلام کسر موجودی");

        tv_company.setPadding(0, 0, 30, 20);
        tv_customername.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        tv_address.setPadding(0, 0, 30, 20);
        tv_phone.setPadding(0, 0, 30, 20);
        tv_total_amount.setPadding(0, 0, 30, 20);
        tv_total_price.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);


        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){
            TextView tv_total_newprice = new TextView(getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            tv_total_newprice.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        int j = 0;
        for (Good g : goods) {
            j++;

            LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(getApplicationContext());
            LinearLayoutCompat ll_details = new LinearLayoutCompat(getApplicationContext());
            LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(getApplicationContext());
            LinearLayoutCompat ll_name_price = new LinearLayoutCompat(getApplicationContext());
            ViewPager vp_radif_name = new ViewPager(getApplicationContext());
            ViewPager vp_rows = new ViewPager(getApplicationContext());
            ViewPager vp_name_amount = new ViewPager(getApplicationContext());
            ViewPager vp_amount_price = new ViewPager(getApplicationContext());
            TextView tv_gap = new TextView(getApplicationContext());
            TextView tv_goodname = new TextView(getApplicationContext());
            TextView tv_amount = new TextView(getApplicationContext());
            TextView tv_price = new TextView(getApplicationContext());

            MaterialCheckBox checkBox = new MaterialCheckBox(ConfirmActivity.this);

            ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
            ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
            vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)1.5));
            tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)4));
            tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)3.5));

            checkBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 4));

            ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
            ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
            ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
            ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

            ll_details.setWeightSum(9);
            ll_radif_check.setWeightSum(5);
            ll_name_price.setWeightSum(9);

            vp_name_amount.setBackgroundResource(R.color.Black);
            vp_amount_price.setBackgroundResource(R.color.Black);
            vp_rows.setBackgroundResource(R.color.Black);
            vp_radif_name.setBackgroundResource(R.color.Black);

            ll_radif_check.setGravity(Gravity.CENTER);
            checkBox.setGravity(Gravity.CENTER_VERTICAL);
            tv_gap.setGravity(Gravity.CENTER);
            tv_goodname.setGravity(Gravity.RIGHT);
            tv_amount.setGravity(Gravity.CENTER);
            tv_price.setGravity(Gravity.CENTER);

            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);

            checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
            tv_goodname.setText(NumberFunctions.PerisanNumber(g.getGoodName()));
            tv_amount.setText(NumberFunctions.PerisanNumber(g.getFacAmount()));
            tv_price.setText(NumberFunctions.PerisanNumber(g.getGoodMaxSellPrice()));

            tv_gap.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv_goodname.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv_price.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            tv_price.setPadding(0, 10, 0, 10);
            tv_goodname.setPadding(0, 10, 5, 10);

            ll_radif_check.addView(tv_gap);
            ll_radif_check.addView(checkBox);

            ll_name_price.addView(tv_goodname);
            ll_name_price.addView(vp_name_amount);
            ll_name_price.addView(tv_amount);
            ll_name_price.addView(vp_amount_price);
            ll_name_price.addView(tv_price);

            ll_details.addView(ll_radif_check);
            ll_details.addView(vp_radif_name);
            ll_details.addView(ll_name_price);

            ll_factor_row.addView(ll_details);
            ll_factor_row.addView(vp_rows);

            ll_good_body_detail.addView(ll_factor_row);

            int fa=j-1;
            if(goods.get(fa).getAppRowIsPacked().equals("1")){
                checkBox.setChecked(true);
                checkBox.setEnabled(false);
            }else {
                checkBox.setEnabled(true);
            }
            if(callMethod.ReadString("Category").equals("1")) {
                checkBox.setVisibility(View.GONE);
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    goods.get(fa).setAppRowIsPacked("1");
                    GoodCodeCheck.add(goods.get(fa).getAppOCRFactorRowCode());
                }else {
                    goods.get(fa).setAppRowIsPacked("0");
                    int b = 0, c = 0;
                    for (String s : GoodCodeCheck) {
                        if (s.equals(goods.get(fa).getAppOCRFactorRowCode()))
                            b = c;
                        c++;
                    }
                    GoodCodeCheck.remove(b);

                }


            });


            tv_goodname.setOnClickListener(v -> image_zome_view(goods.get(fa).getGoodCode()));

        }

        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_address);
        ll_title.addView(tv_phone);
        ll_title.addView(ViewPager);
        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_factor_summary.addView(tv_total_amount);
        ll_factor_summary.addView(tv_total_price);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
        if(callMethod.ReadString("Category").equals("3")) {
            ll_main.addView(btn_shortage);
        }
        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);
        ConfirmCount_Pack();

        btn_shortage.setOnClickListener(v -> CreateView_shortage());

        btn_send.setOnClickListener(v -> Pack_detail(factor.getAppOCRFactorCode()));


        btn_confirm.setOnClickListener(v -> {

            int b=GoodCodeCheck.size();
            final int[] conter = {0};

            for (String goodchecks : GoodCodeCheck) {

                Call<RetrofitResponse> call =apiInterface.CheckState("OcrControlled",goodchecks,"2","");
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                        if(response.isSuccessful()) {

                            conter[0] = conter[0] +1;
                            if(conter[0]==b){
                                intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("FactorImage", "");
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                        Log.e("",t.getMessage()); }
                });


            }

        });

        if(callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(ConfirmActivity.this, NavActivity.class);
                startActivity(intent);
                finish();
            });
        }



    }

    @SuppressLint("RtlHardcoded")
    public void CreateView_shortage(){
        ll_main.removeAllViews();
        ll_title = new LinearLayoutCompat(getApplicationContext());
        ll_good_body = new LinearLayoutCompat(getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(getApplicationContext());
        ViewPager = new ViewPager(getApplicationContext());
        TextView tv_company = new TextView(getApplicationContext());
        TextView tv_customername = new TextView(getApplicationContext());
        TextView tv_factorcode = new TextView(getApplicationContext());
        TextView tv_factordate = new TextView(getApplicationContext());

        btn_confirm = new Button(getApplicationContext());
        btn_send = new Button(getApplicationContext());
        btn_shortage = new Button(getApplicationContext());

        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));

        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));

        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_send_confirm.setWeightSum(2);

        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        tv_company.setGravity(Gravity.CENTER);
        tv_customername.setGravity(Gravity.RIGHT);
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);

        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_shortage.setBackgroundResource(R.color.orange_500);

        tv_company.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(getResources().getColor(R.color.white));
        btn_send.setTextColor(getResources().getColor(R.color.white));
        btn_shortage.setTextColor(getResources().getColor(R.color.Black));

        tv_company.setText(NumberFunctions.PerisanNumber("بخش بسته بندی"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        btn_confirm.setText("ارسال کسری");
        btn_send.setText("بازگشت");
        btn_shortage.setText("اعلام کسر موجودی");

        tv_company.setPadding(0, 0, 30, 20);
        tv_customername.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);


        int j = 0;
        for (Good g : goods) {
            j++;

            if(g.getAppRowIsPacked().equals("0")) {
                LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(getApplicationContext());
                LinearLayoutCompat ll_details = new LinearLayoutCompat(getApplicationContext());
                LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(getApplicationContext());
                LinearLayoutCompat ll_name_price = new LinearLayoutCompat(getApplicationContext());
                ViewPager vp_radif_name = new ViewPager(getApplicationContext());
                ViewPager vp_rows = new ViewPager(getApplicationContext());
                ViewPager vp_name_amount = new ViewPager(getApplicationContext());
                ViewPager vp_amount_price = new ViewPager(getApplicationContext());
                TextView tv_gap = new TextView(getApplicationContext());
                TextView tv_goodname = new TextView(getApplicationContext());
                TextView tv_amount = new TextView(getApplicationContext());
                EditText et_amountshortage = new EditText(getApplicationContext());

                MaterialCheckBox checkBox = new MaterialCheckBox(ConfirmActivity.this);

                ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
                ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
                vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
                vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.5));
                tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 4));
                et_amountshortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 3.5));

                checkBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 4));

                ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
                ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
                ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
                ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

                ll_details.setWeightSum(9);
                ll_radif_check.setWeightSum(5);
                ll_name_price.setWeightSum(9);

                vp_name_amount.setBackgroundResource(R.color.Black);
                vp_amount_price.setBackgroundResource(R.color.Black);
                vp_rows.setBackgroundResource(R.color.Black);
                vp_radif_name.setBackgroundResource(R.color.Black);

                ll_radif_check.setGravity(Gravity.CENTER);
                checkBox.setGravity(Gravity.CENTER_VERTICAL);
                tv_gap.setGravity(Gravity.CENTER);
                tv_goodname.setGravity(Gravity.RIGHT);
                tv_amount.setGravity(Gravity.CENTER);
                et_amountshortage.setGravity(Gravity.CENTER);

                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                et_amountshortage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
                tv_goodname.setText(NumberFunctions.PerisanNumber(g.getGoodName()));
                tv_amount.setText(NumberFunctions.PerisanNumber(g.getFacAmount()));
                et_amountshortage.setHint(g.getFacAmount());
                et_amountshortage.setInputType(InputType.TYPE_CLASS_NUMBER);

                tv_gap.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_goodname.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                et_amountshortage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                et_amountshortage.setPadding(0, 10, 0, 10);
                tv_goodname.setPadding(0, 10, 5, 10);

                ll_radif_check.addView(tv_gap);
                ll_radif_check.addView(checkBox);

                ll_name_price.addView(tv_goodname);
                ll_name_price.addView(vp_name_amount);
                ll_name_price.addView(tv_amount);
                ll_name_price.addView(vp_amount_price);
                ll_name_price.addView(et_amountshortage);

                ll_radif_check.setVisibility(View.INVISIBLE);
                ll_details.addView(ll_radif_check);
                ll_details.addView(vp_radif_name);
                ll_details.addView(ll_name_price);

                ll_factor_row.addView(ll_details);
                ll_factor_row.addView(vp_rows);

                ll_good_body_detail.addView(ll_factor_row);

                int fa = j - 1;
                if (goods.get(fa).getAppRowIsPacked().equals("1")) {
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false);
                } else {
                    checkBox.setEnabled(true);
                }
                if (callMethod.ReadString("Category").equals("1")) {
                    checkBox.setVisibility(View.GONE);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            goods.get(fa).setAppRowIsPacked("1");
                            GoodCodeCheck.add(goods.get(fa).getAppOCRFactorRowCode());
                        } else {
                            goods.get(fa).setAppRowIsPacked("0");
                            int b = 0, c = 0;
                            for (String s : GoodCodeCheck) {
                                if (s.equals(goods.get(fa).getAppOCRFactorRowCode()))
                                    b = c;
                                c++;
                            }
                            GoodCodeCheck.remove(b);

                        }


                    }
                });


                tv_goodname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image_zome_view(goods.get(fa).getGoodCode());
                    }
                });

                arraygood_shortage.clear();
                et_amountshortage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable text) {
                        try {

                            if (Integer.parseInt(text.toString()) > Integer.parseInt(g.getFacAmount())) {
                                et_amountshortage.setText("");
                                callMethod.showToast("از مقدار فاکتور بیشتر می باشد");
                            }else {
                                arraygood_shortage.add(new String[]{g.getAppOCRFactorRowCode(),text.toString()});

                            }

                        }catch (Exception e){

                        }


                    }
                });

            }
        }

        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(ViewPager);
        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
//        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);

        btn_shortage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        btn_confirm.setBackgroundResource(R.color.red_500);
        btn_confirm.setTextColor(getResources().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_send.setBackgroundResource(R.color.green_500);
        btn_confirm.setTextColor(getResources().getColor(R.color.white));
        btn_confirm.setEnabled(true);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (String[] goodchecks : arraygood_shortage) {
                    Log.e("123","start");

                    Call<RetrofitResponse> call =apiInterface.GoodShortage("ocrShortage",goodchecks[0],goodchecks[1]);
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                            if(response.isSuccessful()) {
                                lastCunter++;

                                Log.e("123","---------");
                                Log.e("123",""+lastCunter);
                                Log.e("123",""+arraygood_shortage.size());

                                if(lastCunter ==arraygood_shortage.size()){
                                    finish();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                            Log.e("123","222");

                            Log.e("123",t.getMessage()); }
                    });
                }







            }
        });

        if(callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(ConfirmActivity.this, NavActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }



    }


    public void goodshow(Good g){
        j++;

        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(getApplicationContext());
        LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(getApplicationContext());
        ViewPager vp_radif_name = new ViewPager(getApplicationContext());
        ViewPager vp_rows = new ViewPager(getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(getApplicationContext());
        TextView tv_gap = new TextView(getApplicationContext());
        TextView tv_goodname = new TextView(getApplicationContext());
        TextView tv_amount = new TextView(getApplicationContext());
        TextView tv_price = new TextView(getApplicationContext());

        MaterialCheckBox checkBox = new MaterialCheckBox(ConfirmActivity.this);

        ll_factor_row.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_details.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_radif_check.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 7.7));
        ll_name_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 1.3));
        vp_rows.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
        vp_radif_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_name_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        vp_amount_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(20, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)1.5));
        tv_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)4));
        tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float)3.5));

        checkBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 4));

        ll_details.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_radif_check.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_name_price.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ll_factor_row.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_details.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_radif_check.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_name_price.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ll_details.setWeightSum(9);
        ll_radif_check.setWeightSum(5);
        ll_name_price.setWeightSum(9);

        vp_name_amount.setBackgroundResource(R.color.Black);
        vp_amount_price.setBackgroundResource(R.color.Black);
        vp_rows.setBackgroundResource(R.color.Black);
        vp_radif_name.setBackgroundResource(R.color.Black);

        ll_radif_check.setGravity(Gravity.CENTER);
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
        tv_gap.setGravity(Gravity.CENTER);
        tv_goodname.setGravity(Gravity.RIGHT);
        tv_amount.setGravity(Gravity.CENTER);
        tv_price.setGravity(Gravity.CENTER);

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(g.getGoodName()));
        tv_amount.setText(NumberFunctions.PerisanNumber(g.getFacAmount()));
        tv_price.setText(NumberFunctions.PerisanNumber(g.getGoodMaxSellPrice()));

        tv_gap.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        tv_price.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);


        if(g.getShortageAmount()==null){
            Log.e("","1");
        }else {
            if(g.getShortageAmount()>0) {
                tv_amount.setText(NumberFunctions.PerisanNumber(g.getShortageAmount() + ""));
                tv_amount.setTextColor(getResources().getColor(R.color.red_800));
            }

        }


        ll_radif_check.addView(tv_gap);
        ll_radif_check.addView(checkBox);

        ll_name_price.addView(tv_goodname);
        ll_name_price.addView(vp_name_amount);
        ll_name_price.addView(tv_amount);
        ll_name_price.addView(vp_amount_price);
        ll_name_price.addView(tv_price);

        ll_details.addView(ll_radif_check);
        ll_details.addView(vp_radif_name);
        ll_details.addView(ll_name_price);

        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);

        ll_good_body_detail.addView(ll_factor_row);

        int fa=j-1;
        if(goods.get(fa).getAppRowIsControled().equals("1")){
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }else {
            checkBox.setEnabled(true);
        }
        if(callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                goods.get(fa).setAppRowIsControled("1");
                GoodCodeCheck.add(goods.get(fa).getAppOCRFactorRowCode());
            }else {
                goods.get(fa).setAppRowIsControled("0");
                int b = 0, c = 0;
                for (String s : GoodCodeCheck) {
                    if (s.equals(goods.get(fa).getAppOCRFactorRowCode()))
                        b = c;
                    c++;
                }
                GoodCodeCheck.remove(b);
            }
        });
        tv_goodname.setOnClickListener(v -> image_zome_view(goods.get(fa).getGoodCode()));
    }


    public void ConfirmCount_Control(){
        int ConfirmCounter = 0;
        for (Good g : goods) {
            if(g.getAppRowIsControled().equals("1")){
                ConfirmCounter++;
            }
        }
        if(goods.size() == ConfirmCounter){

            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(getResources().getColor(R.color.Black));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        }else{
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(getResources().getColor(R.color.Black));
            btn_send.setEnabled(false);
        }
    }

    public void ConfirmCount_Pack(){
        int ConfirmCounter = 0;
        for (Good g : goods) {
            if(g.getAppRowIsPacked().equals("1")){
                ConfirmCounter++;
            }
        }
        if(goods.size() == ConfirmCounter){
            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(getResources().getColor(R.color.Black));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        }else{
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(getResources().getColor(R.color.Black));
            btn_send.setEnabled(false);
        }
    }


    public void image_zome_view(String GoodCode) {

        Action action=new Action(ConfirmActivity.this);
        action.good_detail(GoodCode);

    }

    public void Pack_detail(String FactorOcrCode){

        dialog = new Dialog(ConfirmActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pack_header);

        ArrayList<String> arrayList1,arrayList2,arrayList3;
        arrayList1=dbh.Packdetail("Reader");
        arrayList2=dbh.Packdetail("Controler");
        arrayList3=dbh.Packdetail("pack");
        MaterialButton btn_pack_h_send =  dialog.findViewById(R.id.pack_header_send);
        MaterialButton btn_pack_h_1 =  dialog.findViewById(R.id.pack_header_btn1);
        MaterialButton btn_pack_h_2 =  dialog.findViewById(R.id.pack_header_btn2);
        MaterialButton btn_pack_h_3 =  dialog.findViewById(R.id.pack_header_btn3);
        MaterialButton btn_pack_h_5 =  dialog.findViewById(R.id.pack_header_btn5);
        Spinner sp_pack_h_1 = dialog.findViewById(R.id.pack_header_spinner1);
        Spinner sp_pack_h_2 = dialog.findViewById(R.id.pack_header_spinner2);
        Spinner sp_pack_h_3 = dialog.findViewById(R.id.pack_header_spinner3);
        ArrayAdapter<String> sp_adapter_1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,arrayList1);
        ArrayAdapter<String> sp_adapter_2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,arrayList2);
        ArrayAdapter<String> sp_adapter_3 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,arrayList3);
        sp_adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_pack_h_1.setAdapter(sp_adapter_1);
        sp_pack_h_2.setAdapter(sp_adapter_2);
        sp_pack_h_3.setAdapter(sp_adapter_3);

        LinearLayoutCompat ll_pack_h_new_1 = dialog.findViewById(R.id.pack_new_reader);
        LinearLayoutCompat ll_pack_h_new_2 = dialog.findViewById(R.id.pack_new_control);
        LinearLayoutCompat ll_pack_h_new_3 = dialog.findViewById(R.id.pack_new_pack);
        MaterialButton btn_pack_h_new_1 = dialog.findViewById(R.id.pack_new_btn1);
        MaterialButton btn_pack_h_new_2 = dialog.findViewById(R.id.pack_new_btn2);
        MaterialButton btn_pack_h_new_3 = dialog.findViewById(R.id.pack_new_btn3);
        EditText ed_pack_h_new_1 = dialog.findViewById(R.id.pack_new_ed1);
        EditText ed_pack_h_new_2 = dialog.findViewById(R.id.pack_new_ed2);
        EditText ed_pack_h_new_3 = dialog.findViewById(R.id.pack_new_ed3);
        EditText ed_pack_h_amount = dialog.findViewById(R.id.pack_header_packamount);
        ed_pack_h_date = dialog.findViewById(R.id.pack_header_senddate);



        PersianCalendar persianCalendar = new PersianCalendar();
        String tyear="",tmonthOfYear = "",tdayOfMonth="";
        tmonthOfYear="0"+String.valueOf(persianCalendar.getPersianMonth());
        tdayOfMonth ="0"+String.valueOf(persianCalendar.getPersianDay());
        date = persianCalendar.getPersianYear()+"-"
                + tmonthOfYear.substring(tmonthOfYear.length()-2, tmonthOfYear.length())+"-"
                + tdayOfMonth.substring(tdayOfMonth.length()-2, tdayOfMonth.length());

        ed_pack_h_date.setText(date);

        final String[] reader_s = {""};
        final String[] coltrol_s = {""};
        final String[] pack_s = {""};
        final String[] packCount = {""};

        btn_pack_h_send.setOnClickListener(v -> {

            int pack_r =sp_pack_h_1.getSelectedItemPosition();
            int pack_c =sp_pack_h_2.getSelectedItemPosition();
            int pack_d =sp_pack_h_3.getSelectedItemPosition();


            reader_s[0] =arrayList1.get(pack_r);
            coltrol_s[0] =arrayList2.get(pack_c);
            pack_s[0] =arrayList3.get(pack_d);
            packCount[0] =ed_pack_h_amount.getText().toString();

            if(reader_s[0].length()<1){
                reader_s[0] =" ";
            }
            if(coltrol_s[0].length()<1){
                coltrol_s[0] =" ";
            }
            if(pack_s[0].length()<1){
                pack_s[0] =" ";
            }
            if(packCount[0].length()<1){
                packCount[0] ="1";
            }

            Call<RetrofitResponse> call =apiInterface.CheckState("OcrControlled",FactorOcrCode,"3","");
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    dialog.dismiss();
                    finish();
                }
                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                    Log.e("",t.getMessage()); }
            });
            Call<RetrofitResponse> call2 =apiInterface.SetPackDetail("SetPackDetail",FactorOcrCode, reader_s[0], coltrol_s[0], pack_s[0],date, packCount[0]);
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    dialog.dismiss();
                    finish();
                }
                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                    Log.e("",t.getMessage()); }
            });

        });
        btn_pack_h_new_1.setOnClickListener(v -> {
            dbh.Insert_Packdetail("Reader",ed_pack_h_new_1.getText().toString());
            dialog.dismiss();
            Pack_detail(FactorOcrCode);
        });
        btn_pack_h_new_2.setOnClickListener(v -> {
            dbh.Insert_Packdetail("Controler",ed_pack_h_new_2.getText().toString());
            dialog.dismiss();
            Pack_detail(FactorOcrCode);
        });
        btn_pack_h_new_3.setOnClickListener(v -> {
            dbh.Insert_Packdetail("pack",ed_pack_h_new_3.getText().toString());
            dialog.dismiss();
            Pack_detail(FactorOcrCode);
        });


        btn_pack_h_1.setOnClickListener(v -> ll_pack_h_new_1.setVisibility(View.VISIBLE));
        btn_pack_h_2.setOnClickListener(v -> ll_pack_h_new_2.setVisibility(View.VISIBLE));
        btn_pack_h_3.setOnClickListener(v -> ll_pack_h_new_3.setVisibility(View.VISIBLE));


        btn_pack_h_5.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    ConfirmActivity.this::onDateSet,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );
            datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
        });

        dialog.show();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String tyear="",tmonthOfYear = "",tdayOfMonth="";
        tmonthOfYear="0"+String.valueOf(monthOfYear);
        tdayOfMonth ="0"+String.valueOf(dayOfMonth);

        //date = year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
        date = year+"-"
                + tmonthOfYear.substring(tmonthOfYear.length()-2, tmonthOfYear.length())+"-"
                + tdayOfMonth.substring(tdayOfMonth.length()-2, tdayOfMonth.length());

        ed_pack_h_date.setText(date);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(getApplicationContext(), ConfirmActivity.class);
        intent.putExtra("ScanResponse", BarcodeScan);
        startActivity(intent);
        finish();

    }

}