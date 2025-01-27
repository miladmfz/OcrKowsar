package com.kits.ocrkowsar.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfirmActivity;
import com.kits.ocrkowsar.activity.NavActivity;
import com.kits.ocrkowsar.application.Action;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.application.Print;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CollectFragment extends Fragment {

    APIInterface apiInterface;
    APIInterface secendApiInterface;
    DatabaseHelper dbh ;
    ArrayList<String> GoodCodeCheck=new ArrayList<>();
    LinearLayoutCompat ll_main;
    LinearLayoutCompat ll_title;
    LinearLayoutCompat ll_good_body_detail;
    LinearLayoutCompat ll_good_body;
    LinearLayoutCompat ll_factor_summary;
    LinearLayoutCompat ll_send_confirm;
    CallMethod callMethod;

    ViewPager ViewPager;
    int firsttry = 0;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    ArrayList<Good> goods;
    ArrayList<Good> goods_visible=new ArrayList<>();
    ArrayList<String[]> arraygood_shortage = new ArrayList<>();
    ArrayList<String> Array_GoodCodesCheck = new ArrayList<>();

    Factor factor;
    String BarcodeScan;
    Action action;
    Integer lastCunter = 0;

    Intent intent;
    int width=1;
    Handler handler;
    int row_counter;

    Print print;
    View view;
    Dialog dialogProg;

    String state;
    String TcPrintRef;

    LinearLayoutCompat ll_shortage_print;
    TextView tv_company,tv_customername,tv_factorcode,tv_factordate,tv_address,tv_phone;
    TextView tv_total_amount,tv_total_price,tv_appocrfactorexplain,tv_factorexplain;
    Button btn_send,btn_confirm,btn_shortage,btn_set_stack,btn_print;

    public String getTcPrintRef() {
        return TcPrintRef;
    }

    public void setTcPrintRef(String tcPrintRef) {
        TcPrintRef = tcPrintRef;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public ArrayList<Good> getGoods() {
        return goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.goods = goods;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_collect, container, false);
        ll_main = view.findViewById(R.id.collectfragment_layout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            callMethod = new CallMethod(requireActivity());
            action = new Action(requireActivity());
            dbh = new DatabaseHelper(requireActivity(), callMethod.ReadString("DatabaseName"));
            apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
            secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);
            handler=new Handler();
            print=new Print(requireActivity());
            for (final String[] ignored : arraygood_shortage) {
                arraygood_shortage.add(new String[]{"goodcode", "amount "});
            }
            DisplayMetrics metrics = new DisplayMetrics();
            view.getDisplay().getMetrics(metrics);
            width =metrics.widthPixels;
            dialogProg = new Dialog(requireActivity());
            dialogProg.setContentView(R.layout.rep_prog);
            dialogProg.findViewById(R.id.rep_prog_text).setVisibility(View.GONE);
            CreateView_Control();
        }catch (Exception e){
            Log.e("kowsar",e.getMessage());
        }

    }



    @SuppressLint("RtlHardcoded")
    public void CreateView_Control(){
        NewView();
        setLayoutParams();
        setOrientation();
        setLayoutParams();
        setLayoutDirection();
        setGravity();
        setTextSize();
        setBackgroundResource();
        setTextColor();
        setPadding();

        ll_send_confirm.setWeightSum(2);
        ll_shortage_print.setWeightSum(2);


        tv_company.setText(NumberFunctions.PerisanNumber("بخش انبار"));
        tv_appocrfactorexplain.setText(NumberFunctions.PerisanNumber(" انبار :   " + factor.getAppOCRFactorExplain()));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));

        tv_factorcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callMethod.ReadBoolan("ShowSumAmountHint")){
                    action.checkSumAmounthint(factor);
                }
                return false;

            }
        });


        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getExplain()));



        tv_address.setText(NumberFunctions.PerisanNumber(" آدرس : " + factor.getAddress()));
        tv_phone.setText(NumberFunctions.PerisanNumber(" تلفن تماس : " + factor.getPhone()));
        tv_total_amount.setText(NumberFunctions.PerisanNumber(" تعداد کل:   " + factor.getSumAmount()));
        tv_total_price.setText(NumberFunctions.PerisanNumber(" قیمت کل : " + decimalFormat.format(Integer.valueOf(factor.getSumPrice())) + " ریال"));

        btn_confirm.setText("تاییده بخش");
        btn_send.setText("ارسال تاییده");
        btn_set_stack.setText("آغار فرآیند انبار");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_print.setText("پرینت فاکتور");




        if(!factor.getNewSumPrice().equals(factor.getSumPrice())){
            TextView tv_total_newprice = new TextView(requireActivity().getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
            tv_total_newprice.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        row_counter= 0;
        callMethod.Log("goods.size = " + goods.size());
        for (Good single_good : goods) {
            if(callMethod.ReadString("StackCategory").equals("همه")) {
                goods_visible.add(single_good);
                goodshow(single_good);
            }

            // TODO check and replace enother
            else if(single_good.getGoodExplain4().equals(callMethod.ReadString("StackCategory"))){
                goods_visible.add(single_good);
                goodshow(single_good);
            }


            /* // TODO check and replace  baraye gostaresh
            else if (g.getFormNo() != null) {
                int FormNo = Integer.parseInt(g.getFormNo());  // Ensure this value is of type double

                if (callMethod.ReadString("StackCategory").equals("انبار1") && FormNo >= 100000 && FormNo <= 199999) {
                    goods_visible.add(g);
                    goodshow(g);
                } else if (callMethod.ReadString("StackCategory").equals("انبار2") && FormNo > 200000 && FormNo <= 299999) {
                    goods_visible.add(g);
                    goodshow(g);
                } else if (callMethod.ReadString("StackCategory").equals("انبار3") && FormNo > 300000 && FormNo <= 399999) {
                    goods_visible.add(g);
                    goodshow(g);
                }
            }
            */


        }

        callMethod.Log("goods_visible.size = " + goods_visible.size());

        try{
            factor.getAppOCRFactorExplain();
        }catch (Exception e){
            factor.setAppOCRFactorExplain("");
        }

        ll_title.addView(tv_company);
        callMethod.Log("step = " + "00");
        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
            Log.e("CheckString", "AppOCRFactorExplain Dare");
        } else {
            Log.e("CheckString", "AppOCRFactorExplain nadare");

            ll_title.addView(btn_set_stack);
        }
        callMethod.Log("step = " + "0");



        ll_title.addView(tv_appocrfactorexplain);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_factorexplain);
        ll_title.addView(tv_address);
        ll_title.addView(tv_phone);
        ll_title.addView(ViewPager);
        callMethod.Log("step = " + "1");

        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);

        callMethod.Log("step = " + "2");

        ll_good_body.addView(ll_good_body_detail);

        if (callMethod.ReadBoolan("JustScanner")){
            ll_factor_summary.addView(tv_total_amount);
        }
        callMethod.Log("step = " + "3");

        ll_factor_summary.addView(tv_total_price);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);


        callMethod.Log("step = " + "4");



        if (factor.getAppOCRFactorExplain().contains(callMethod.ReadString("StackCategory"))) {
            if (callMethod.ReadString("Category").equals("2")) {
                ll_shortage_print.addView(btn_shortage);
                ll_shortage_print.addView(btn_print);
                ll_main.addView(ll_shortage_print);
            }
            ll_main.addView(ll_factor_summary);
            ll_main.addView(ll_send_confirm);
        }
        callMethod.Log("step = " + "5");



        ConfirmCount_Control();
        callMethod.Log("step = " + "8");

        btn_shortage.setOnClickListener(v -> CreateView_shortage());
        btn_print.setOnClickListener(v -> print.Printing(factor,goods_visible,"0","1"));
        btn_set_stack.setOnClickListener(v -> {

            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call=apiInterface.SetOcrFactorExplain("SetOcrFactorExplain",factor.getAppOCRFactorCode(),callMethod.ReadString("StackCategory"));

            }else{
                call=secendApiInterface.SetOcrFactorExplain("SetOcrFactorExplain",factor.getAppOCRFactorCode(),callMethod.ReadString("StackCategory"));
            }
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()) {
                        dialogProg.dismiss();
                        intent = new Intent(requireActivity(), ConfirmActivity.class);
                        intent.putExtra("ScanResponse", BarcodeScan);
                        intent.putExtra("State", "0");
                        intent.putExtra("FactorImage", "");
                        dialogProg.dismiss();
                        startActivity(intent);
                        requireActivity().finish();


                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("kowsar_onFailure",t.getMessage()); }
            });
        });

        btn_send.setOnClickListener(v -> {


            dialogProg.show();

            Call<RetrofitResponse> call;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call=apiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");

            }else{
                call=secendApiInterface.CheckState("OcrControlled",factor.getAppOCRFactorCode(),"1","");
            }
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    if(response.isSuccessful()) {
                        dialogProg.dismiss();

                        Call<RetrofitResponse> call1;
                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                            call1=apiInterface.OcrDoubleCheck("OcrDoubleCheck",factor.getAppOCRFactorCode());

                        }else{
                            call1=secendApiInterface.OcrDoubleCheck("OcrDoubleCheck",factor.getAppOCRFactorCode());
                        }

                        call1.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                                if(response.isSuccessful()) {
                                    dialogProg.dismiss();

                                    assert response.body() != null;
                                    if (response.body().getText().equals("HasNotDoubleCheck")){

                                        action.Pack_detail(factor);


                                    }else if (response.body().getText().equals("HasDoubleCheck")){
                                        print.Printing(factor,goods_visible,"0","0");
                                    }

                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
                                Log.e("kowsar_onFailure",t.getMessage()); }
                        });



                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("",t.getMessage()); }
            });
        });

        btn_confirm.setOnClickListener(v -> {

            int Array_GoodCodesCheck_count=Array_GoodCodesCheck.size();
            final int[] conter = {0};
            dialogProg.show();
            for (String single_GoodCode_check : Array_GoodCodesCheck) {


                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.OcrControlled(
                            "OcrControlled",
                            single_GoodCode_check,
                            "0",
                            callMethod.ReadString("JobPersonRef")
                    );
                }else{
                    call=secendApiInterface.OcrControlled(
                            "OcrControlled",
                            single_GoodCode_check,
                            "0",
                            callMethod.ReadString("JobPersonRef")
                    );
                }





                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if(response.isSuccessful()) {

                            conter[0] = conter[0] +1;
                            if(conter[0]==Array_GoodCodesCheck_count){

                                assert response.body() != null;
                                intent = new Intent(requireActivity(), ConfirmActivity.class);
                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("State", "0");
                                intent.putExtra("FactorImage", "");
                                dialogProg.dismiss();
                                startActivity(intent);
                                requireActivity().finish();


                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        dialogProg.dismiss();
                        Log.e("",t.getMessage()); }
                });


            }


        });


        if(callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(requireActivity(), NavActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }





    }


    public void NewView(){


        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_shortage_print = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_company = new TextView(requireActivity().getApplicationContext());
        tv_customername = new TextView(requireActivity().getApplicationContext());
        tv_appocrfactorexplain = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        tv_address = new TextView(requireActivity().getApplicationContext());
        tv_phone = new TextView(requireActivity().getApplicationContext());
        tv_total_amount = new TextView(requireActivity().getApplicationContext());
        tv_total_price = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_set_stack = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());
        btn_print = new Button(requireActivity().getApplicationContext());


    }
    public void setLayoutParams(){


        ll_title.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_good_body.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_factor_summary.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_send_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_shortage_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        tv_company.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_appocrfactorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_customername.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorcode.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factordate.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_factorexplain.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_address.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_phone.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        btn_confirm.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_send.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_set_stack.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
        btn_shortage.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,1));
        btn_print.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,1));

        tv_total_amount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_total_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));
    }
    public void setOrientation(){
        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_shortage_print.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }
    public void setLayoutDirection(){
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_shortage_print.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    @SuppressLint("RtlHardcoded")
    public void setGravity(){
        tv_company.setGravity(Gravity.CENTER);
        tv_appocrfactorexplain.setGravity(Gravity.RIGHT);
        tv_customername.setGravity(Gravity.RIGHT);
        tv_factorcode.setGravity(Gravity.RIGHT);
        tv_factordate.setGravity(Gravity.RIGHT);
        tv_factorexplain.setGravity(Gravity.RIGHT);
        tv_address.setGravity(Gravity.RIGHT);
        tv_phone.setGravity(Gravity.RIGHT);
        tv_total_amount.setGravity(Gravity.RIGHT);
        tv_total_price.setGravity(Gravity.RIGHT);
        btn_confirm.setGravity(Gravity.CENTER);
        btn_send.setGravity(Gravity.CENTER);
        btn_set_stack.setGravity(Gravity.CENTER);
        btn_shortage.setGravity(Gravity.CENTER);
        btn_print.setGravity(Gravity.CENTER);
    }
    public void setTextSize(){
        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_appocrfactorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorexplain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_set_stack.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_shortage.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_print.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("TitleSize")));

    }
    public void setBackgroundResource(){

        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_set_stack.setBackgroundResource(R.color.blue_500);
        btn_shortage.setBackgroundResource(R.color.orange_500);
        btn_print.setBackgroundResource(R.color.blue_500);
    }
    public void setTextColor(){
        tv_company.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_appocrfactorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorexplain.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_address.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_phone.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_send.setTextColor(requireActivity().getColor(R.color.white));
        btn_set_stack.setTextColor(requireActivity().getColor(R.color.white));
        btn_shortage.setTextColor(requireActivity().getColor(R.color.Black));
        btn_print.setTextColor(requireActivity().getColor(R.color.Black));
    }

    public void setPadding(){
        tv_company.setPadding(0, 0, 30, 20);
        tv_appocrfactorexplain.setPadding(0, 0, 30, 20);
        tv_customername.setPadding(0, 0, 30, 20);
        tv_factorcode.setPadding(0, 0, 30, 20);
        tv_factordate.setPadding(0, 0, 30, 20);
        tv_factorexplain.setPadding(0, 0, 30, 20);
        tv_address.setPadding(0, 0, 30, 20);
        tv_phone.setPadding(0, 0, 30, 20);
        tv_total_amount.setPadding(0, 0, 30, 20);
        tv_total_price.setPadding(0, 0, 30, 20);
        btn_confirm.setPadding(0, 0, 30, 20);
        btn_send.setPadding(0, 0, 30, 20);
        btn_set_stack.setPadding(0, 0, 30, 20);
        btn_shortage.setPadding(0, 0, 30, 20);
        btn_print.setPadding(0, 0, 30, 20);
    }





    @SuppressLint("RtlHardcoded")
    public void goodshow(Good good_detial){
        row_counter++;

        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager vp_radif_name = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_rows = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(requireActivity().getApplicationContext());
        TextView tv_gap = new TextView(requireActivity().getApplicationContext());
        TextView tv_goodname = new TextView(requireActivity().getApplicationContext());
        TextView tv_amount = new TextView(requireActivity().getApplicationContext());
        TextView tv_price = new TextView(requireActivity().getApplicationContext());

        MaterialCheckBox checkBox = new MaterialCheckBox(requireActivity());

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

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("BodySize"))-10);
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("BodySize"))+3);
        tv_amount.setTypeface(null, Typeface.BOLD);

        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(callMethod.ReadString("BodySize")));

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(row_counter)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(good_detial.getGoodName()));

        // TODO
        tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getFacAmount()));
        tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice()));
        //tv_price.setText(NumberFunctions.PerisanNumber(good_detial.getGoodMaxSellPrice().substring(0,good_detial.getGoodMaxSellPrice().indexOf("."))));


//        // TODO
//        tv_price.setText(NumberFunctions.PerisanNumber(g.getFormNo()));

        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

        tv_price.setPadding(0, 10, 0, 10);
        tv_goodname.setPadding(0, 10, 5, 10);


        if(good_detial.getShortageAmount()==null){
            callMethod.Log("ShortageAmount is null");
        }else {
            if(good_detial.getShortageAmount()>0) {
                tv_amount.setText(NumberFunctions.PerisanNumber(good_detial.getShortageAmount() + ""));
                tv_amount.setTextColor(requireActivity().getColor(R.color.red_800));
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




        try {
            if (good_detial.getMinAmount().equals("1.000")){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
            }
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());

        }
        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);

        ll_good_body_detail.addView(ll_factor_row);

        int correct_row=row_counter-1;
        if(goods_visible.get(correct_row).getAppRowIsControled().equals("1")){
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }else {
            checkBox.setEnabled(!callMethod.ReadBoolan("JustScanner"));
        }

        if(callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){

                goods_visible.get(correct_row).setAppRowIsControled("1");
                if (!Array_GoodCodesCheck.contains(goods_visible.get(correct_row).getAppOCRFactorRowCode())) {
                    Array_GoodCodesCheck.add(goods_visible.get(correct_row).getAppOCRFactorRowCode());
                }
            }else {
                goods_visible.get(correct_row).setAppRowIsControled("0");
                Array_GoodCodesCheck.remove(goods_visible.get(correct_row).getAppOCRFactorRowCode());
            }
        });


        tv_goodname.setOnClickListener(v -> image_zome_view(goods_visible.get(correct_row).getGoodCode()));
        tv_amount.setOnClickListener(v -> amount_zome_view(goods_visible.get(correct_row).getFacAmount(),goods_visible.get(correct_row).getShortageAmount()+""));

    }
    public void Newview() {
        ll_title = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_good_body_detail = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_factor_summary = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ll_send_confirm = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager = new ViewPager(requireActivity().getApplicationContext());
        tv_company = new TextView(requireActivity().getApplicationContext());
        tv_customername = new TextView(requireActivity().getApplicationContext());
        tv_factorcode = new TextView(requireActivity().getApplicationContext());
        tv_factordate = new TextView(requireActivity().getApplicationContext());
        tv_factorexplain = new TextView(requireActivity().getApplicationContext());
        tv_address = new TextView(requireActivity().getApplicationContext());
        tv_phone = new TextView(requireActivity().getApplicationContext());
        tv_total_amount = new TextView(requireActivity().getApplicationContext());
        tv_total_price = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_set_stack = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());
        btn_print = new Button(requireActivity().getApplicationContext());


    }

    public void ConfirmCount_Control(){
        callMethod.Log("step = " + "6");

        int ConfirmCounter_stack = 0;
        int ConfirmCounter = 0;

        for (Good g : goods) {
            if(g.getAppRowIsControled().equals("1")){
                ConfirmCounter++;
            }
        }
        for (Good g : goods_visible) {
            if(g.getAppRowIsControled().equals("1")){
                ConfirmCounter_stack++;
            }
        }
        if(goods.size() == ConfirmCounter){
            if(callMethod.ReadBoolan("AutoSend")) {
                dialogProg.show();

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                    call = apiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                } else {
                    call = secendApiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "1", callMethod.ReadString("Deliverer"));
                }


                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            callMethod.showToast("تاییده ارسال شد.");
                            dialogProg.dismiss();


                            Call<RetrofitResponse> call1;
                            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))) {
                                call1 = apiInterface.OcrDoubleCheck("OcrDoubleCheck", factor.getAppOCRFactorCode());

                            } else {
                                call1 = secendApiInterface.OcrDoubleCheck("OcrDoubleCheck", factor.getAppOCRFactorCode());
                            }

                            call1.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<RetrofitResponse> call1, @NonNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        dialogProg.dismiss();

                                        if (response.body().getText().equals("HasNotDoubleCheck")) {
                                            action.checkSumAmount(factor);


                                        } else if (response.body().getText().equals("HasDoubleCheck")) {
                                            print.Printing(factor, goods_visible, "0", "0");
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RetrofitResponse> call1, @NonNull Throwable t) {
                                    Log.e("kowsar_onFailure", t.getMessage());
                                }
                            });

                            // print.Printing(factor,goods_visible,"0");

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        dialogProg.dismiss();
                        callMethod.Log(t.getMessage());
                    }
                });

            }
            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(requireActivity().getColor(R.color.Black));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        }else{
            if(callMethod.ReadBoolan("AutoSend")){
                if(goods_visible.size() == ConfirmCounter_stack){
                    print.Printing(factor,goods_visible,"0","0");
                }
            }
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(requireActivity().getColor(R.color.Black));
            btn_send.setEnabled(false);
        }
        callMethod.Log("step = " + "7");

    }















    @SuppressLint("RtlHardcoded")
    public void CreateView_shortage() {
        ll_main.removeAllViews();
        lastCunter=0;
        Newview();
        setLayoutParams();
        setOrientation();
        setLayoutDirection();
        setGravity();
        setTextSize();
        setBackgroundResource();
        setTextColor();
        setPadding();

        ll_send_confirm.setWeightSum(2);
        ll_shortage_print.setWeightSum(2);


        tv_company.setText(NumberFunctions.PerisanNumber("کسری صورت جمع کن"));
        tv_appocrfactorexplain.setText(NumberFunctions.PerisanNumber(" اتبار  :   " + factor.getAppOCRFactorExplain()));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));
        tv_factorexplain.setText(NumberFunctions.PerisanNumber(" توضیحات :   " + factor.getFactorDate()));

        btn_confirm.setText("ارسال کسری");
        btn_send.setText("بازگشت");
        btn_shortage.setText("اعلام کسر موجودی");
        btn_shortage.setTextSize(20);


        int countergood = 0;
        for (Good singlegood : goods) {
            countergood++;

            if (singlegood.getAppRowIsControled().equals("0")) {
                ll_good_body_detail.addView(CreateGoodViewForshortage(singlegood, countergood));

            }
        }
        ll_title.addView(tv_company);
        ll_title.addView(tv_customername);
        ll_title.addView(tv_factorcode);
        ll_title.addView(tv_factordate);
        ll_title.addView(tv_factorexplain);
        ll_title.addView(ViewPager);
        ll_send_confirm.addView(btn_confirm);
        ll_send_confirm.addView(btn_send);


        ll_good_body.addView(ll_good_body_detail);

        ll_main.addView(ll_title);
        ll_main.addView(ll_good_body);
//        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);


        btn_shortage.setOnClickListener(v -> CreateView_shortage());

        btn_send.setOnClickListener(v -> requireActivity().finish());

        btn_confirm.setBackgroundResource(R.color.red_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_send.setBackgroundResource(R.color.green_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_confirm.setOnClickListener(v -> {


            for (String[] goodchecks : arraygood_shortage) {

                Call<RetrofitResponse> call;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call=apiInterface.GoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
                }else{
                    call=secendApiInterface.GoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
                }


                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            lastCunter++;

                            if (lastCunter == arraygood_shortage.size()) {
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                        Log.e("kowsar_onFailure", t.getMessage());
                    }
                });
            }


        });

        if (callMethod.ReadString("Category").equals("1")) {
            btn_send.setVisibility(View.GONE);
            btn_confirm.setText("بازگشت به صفحه اصلی");
            btn_confirm.setOnClickListener(v -> {
                intent = new Intent(requireActivity(), NavActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }


    }

    @SuppressLint("RtlHardcoded")
    public View CreateGoodViewForshortage(@NonNull Good good, int countergood) {
        arraygood_shortage.add(new String[]{good.getAppOCRFactorRowCode(), good.getFacAmount()});
        LinearLayoutCompat ll_factor_row = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_details = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_radif_check = new LinearLayoutCompat(requireActivity().getApplicationContext());
        LinearLayoutCompat ll_name_price = new LinearLayoutCompat(requireActivity().getApplicationContext());
        ViewPager vp_radif_name = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_rows = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_name_amount = new ViewPager(requireActivity().getApplicationContext());
        ViewPager vp_amount_price = new ViewPager(requireActivity().getApplicationContext());
        TextView tv_gap = new TextView(requireActivity().getApplicationContext());
        TextView tv_goodname = new TextView(requireActivity().getApplicationContext());
        TextView tv_amount = new TextView(requireActivity().getApplicationContext());
        EditText et_amountshortage = new EditText(requireActivity().getApplicationContext());

        CheckBox checkBox = new MaterialCheckBox(requireActivity());


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

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));
        et_amountshortage.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("BodySize")));

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(countergood)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(good.getGoodName()));
        tv_amount.setText(NumberFunctions.PerisanNumber(good.getFacAmount()));
        et_amountshortage.setHint(good.getFacAmount());
        et_amountshortage.setInputType(InputType.TYPE_CLASS_NUMBER);

        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        et_amountshortage.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

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


        try {
            if (good.getMinAmount().equals("1.000")){
                ll_details.setBackgroundColor(requireActivity().getColor(R.color.red_100));
            }
        }catch (Exception e){

            callMethod.Log(e.getMessage());
        }



        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);


        int correct_row = countergood - 1;
        if (goods.get(correct_row).getAppRowIsPacked().equals("1")) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {
            checkBox.setEnabled(true);
        }
        if (callMethod.ReadString("Category").equals("1")) {
            checkBox.setVisibility(View.GONE);
        }




        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String goodCode = goods_visible.get(correct_row).getAppOCRFactorRowCode();
            if (isChecked) {
                goods_visible.get(correct_row).setAppRowIsControled("1");
                if (!Array_GoodCodesCheck.contains(goodCode)) {
                    Array_GoodCodesCheck.add(goodCode);
                }
            } else {
                goods_visible.get(correct_row).setAppRowIsControled("0");
                Array_GoodCodesCheck.remove(goodCode);
            }
        });


        tv_goodname.setOnClickListener(v -> image_zome_view(goods.get(correct_row).getGoodCode()));

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
                    if (firsttry==0){
                        arraygood_shortage.clear();
                        firsttry = firsttry +1;
                    }

                    if (Integer.parseInt(text.toString()) > Integer.parseInt(good.getFacAmount())) {
                        et_amountshortage.setText("");
                        callMethod.showToast("از مقدار فاکتور بیشتر می باشد");
                    } else {
                        arraygood_shortage.add(new String[]{good.getAppOCRFactorRowCode(), text.toString()});

                    }

                } catch (Exception ignored) {

                }
//                handler.removeCallbacksAndMessages(null);
//                handler.postDelayed(() -> {
//
//                },  1000);

            }
        });
        return ll_factor_row;
    }

    public void image_zome_view(String GoodCode) {

        Action action=new Action(requireActivity());
        action.good_detail(GoodCode);

    }
    public void amount_zome_view(String Facamount,String shortage) {

        Action action=new Action(requireActivity());
        action.goodamount_detail(Facamount,shortage);

    }

    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}