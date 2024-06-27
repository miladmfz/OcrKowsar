package com.kits.ocrkowsar.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfigActivity;
import com.kits.ocrkowsar.activity.LocalFactorListActivity;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.application.Print;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.Job;
import com.kits.ocrkowsar.model.JobPerson;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.model.Utilities;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIClientSecond;
import com.kits.ocrkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity implements DatePickerDialog.OnDateSetListener {

    APIInterface apiInterface;
    APIInterface secendApiInterface;
    DatabaseHelper dbh;
    private final Context mContext;
    CallMethod callMethod;
    String coltrol_s = "";
    String reader_s = "";
    String pack_s = "";
    String sendtime = "";
    String packCount = "";
    ArrayList<Job> jobs;
    String date = "";
    TextView ed_pack_h_date;
    Dialog dialog, dialogProg;
    ArrayList<String> sendtimearray = new ArrayList<>();
    TextView tv_rep;
    Print print;
    public Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        Log.e("kowsar",callMethod.ReadString("ServerURLUse"));
        Log.e("kowsar",callMethod.ReadString("SecendServerURL"));
        Log.e("kowsar","");
        Log.e("kowsar","");
        Log.e("kowsar","");


        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);

        dialog = new Dialog(mcontxt);
        dialogProg = new Dialog(mContext);
        print = new Print(mContext);

    }
    public void dialogProg() {
        dialogProg.setContentView(R.layout.rep_prog);
        tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
        tv_rep.setVisibility(View.GONE);
        dialogProg.show();
    }

    public void factor_detail(Factor factor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_factor_detail);


        TextView tv_AppOCRFactorCode = dialog.findViewById(R.id.dialog_factor_AppOCRFactorCode);
        TextView tv_AppTcPrintRef = dialog.findViewById(R.id.dialog_factor_AppTcPrintRef);
        TextView tv_AppControlDate = dialog.findViewById(R.id.dialog_factor_AppControlDate);
        TextView tv_AppPackDate = dialog.findViewById(R.id.dialog_factor_AppPackDate);
        TextView tv_AppReader = dialog.findViewById(R.id.dialog_factor_AppReader);
        TextView tv_AppControler = dialog.findViewById(R.id.dialog_factor_AppControler);
        TextView tv_AppPacker = dialog.findViewById(R.id.dialog_factor_AppPacker);
        TextView tv_AppPackDeliverDate = dialog.findViewById(R.id.dialog_factor_AppPackDeliverDate);
        TextView tv_AppPackCount = dialog.findViewById(R.id.dialog_factor_AppPackCount);
        TextView tv_AppDeliverer = dialog.findViewById(R.id.dialog_factor_AppDeliverer);
        TextView tv_IsEdited = dialog.findViewById(R.id.dialog_factor_IsEdited);
        TextView tv_HasSignature = dialog.findViewById(R.id.dialog_factor_HasSignature);


        TextView tv_FactorPrivateCode = dialog.findViewById(R.id.dialog_factor_FactorPrivateCode);
        TextView tv_FactorDate = dialog.findViewById(R.id.dialog_factor_FactorDate);
        TextView tv_CustName = dialog.findViewById(R.id.dialog_factor_CustName);
        TextView tv_customercode = dialog.findViewById(R.id.dialog_factor_customercode);
        TextView tv_Ersall = dialog.findViewById(R.id.dialog_factor_Ersall);
        TextView tv_BrokerName = dialog.findViewById(R.id.dialog_factor_BrokerName);
        TextView tv_AppFactorState = dialog.findViewById(R.id.dialog_factor_AppFactorState);
        Button btn_1 = dialog.findViewById(R.id.dialog_factor_btn1);
        Button btn_2 = dialog.findViewById(R.id.dialog_factor_btn2);

        tv_AppOCRFactorCode.setText(NumberFunctions.PerisanNumber(factor.getAppOCRFactorCode()));
        tv_AppTcPrintRef.setText(NumberFunctions.PerisanNumber(factor.getAppTcPrintRef()));
        tv_AppControlDate.setText(NumberFunctions.PerisanNumber(factor.getAppControlDate()));
        tv_AppPacker.setText(NumberFunctions.PerisanNumber(factor.getAppPacker()));
        tv_AppPackDeliverDate.setText(NumberFunctions.PerisanNumber(factor.getAppPackDeliverDate()));
        tv_AppPackCount.setText(NumberFunctions.PerisanNumber(factor.getAppPackCount()));
        tv_AppDeliverer.setText(NumberFunctions.PerisanNumber(factor.getAppDeliverer()));

        tv_FactorPrivateCode.setText(NumberFunctions.PerisanNumber(factor.getFactorPrivateCode()));
        tv_FactorDate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));
        tv_CustName.setText(NumberFunctions.PerisanNumber(factor.getCustName()));
        tv_customercode.setText(NumberFunctions.PerisanNumber(factor.getCustomercode()));
        tv_Ersall.setText(NumberFunctions.PerisanNumber(factor.getErsall()));
        Log.e("kowsar",factor.getBrokerName());
        if (factor.getBrokerName().length() > 20)
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName().substring(0, 20) + "..."));
        else
            tv_BrokerName.setText(NumberFunctions.PerisanNumber(factor.getBrokerName()));

        tv_AppFactorState.setText(NumberFunctions.PerisanNumber(factor.getAppFactorState()));


        tv_AppPackDate.setText(NumberFunctions.PerisanNumber(factor.getAppPackDate()));
        tv_AppReader.setText(NumberFunctions.PerisanNumber(factor.getAppReader()));
        tv_AppControler.setText(NumberFunctions.PerisanNumber(factor.getAppControler()));


        if (factor.getIsEdited().equals("1")) {
            tv_IsEdited.setText("دارد");
        } else {
            tv_IsEdited.setText("ندارد");
        }
        if (factor.getIsEdited().equals("1")) {
            tv_HasSignature.setText("دارد");
        } else {
            tv_HasSignature.setText("ندارد");
        }

        if (factor.getAppIsDelivered().equals("0")) {
            btn_1.setVisibility(View.GONE);
        } else {
            btn_1.setVisibility(View.VISIBLE);

        }

        btn_1.setOnClickListener(v -> {

            dialogProg();

            Call<RetrofitResponse> call1;
            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call1=apiInterface.ExitDelivery("ExitDelivery", factor.getAppOCRFactorCode());
            }else{
                call1=secendApiInterface.ExitDelivery("ExitDelivery", factor.getAppOCRFactorCode());
            }







            call1.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    if (response.body().getText().equals("Done")) {
                        dialog.dismiss();
                        dialogProg.dismiss();
                    }


                }

                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                }
            });


        });

        btn_2.setOnClickListener(v -> {
            Pack_detail(factor);
            dialog.dismiss();
        });


        dialog.show();


    }


    public void Pack_detail(Factor factor) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pack_header);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton btn_pack_h_send = dialog.findViewById(R.id.pack_header_send);
        MaterialButton btn_pack_h_5 = dialog.findViewById(R.id.pack_header_btn5);
        EditText ed_pack_h_amount = dialog.findViewById(R.id.pack_header_packamount);

        ed_pack_h_date = dialog.findViewById(R.id.pack_header_senddate);

        PersianCalendar persianCalendar = new PersianCalendar();
        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (persianCalendar.getPersianMonth() + 1);
        tdayOfMonth = "0" + persianCalendar.getPersianDay();
        date = persianCalendar.getPersianYear() + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));

        LinearLayoutCompat ll_pack_h_main = dialog.findViewById(R.id.packheader_linejob);

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetJob("TestJob", "Ocr3");
        }else{
            call=secendApiInterface.GetJob("TestJob", "Ocr3");
        }

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    jobs = response.body().getJobs();

                    for (Job job : jobs) {

                        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                70
                        );
                        params.setMargins(30, 30, 30, 30);
                        LinearLayoutCompat ll_new = new LinearLayoutCompat(mContext.getApplicationContext());
                        ll_new.setLayoutParams(params);
                        ll_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        ll_new.setOrientation(LinearLayoutCompat.HORIZONTAL);
                        ll_new.setWeightSum(2);


                        TextView Tv_new = new TextView(mContext.getApplicationContext());
                        Tv_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                        Tv_new.setText(job.getTitle());
                        Tv_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                        ll_new.addView(Tv_new);


                        Call<RetrofitResponse> call1;
                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                            call1=apiInterface.GetJobPerson("TestJobPerson", job.getTitle());
                        }else{
                            call1=secendApiInterface.GetJobPerson("TestJobPerson", job.getTitle());
                        }

                        call1.enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    ArrayList<JobPerson> jobPersons = response.body().getJobPersons();
                                    ArrayList<String> jobpersonsstr_new = new ArrayList<>();

                                    jobpersonsstr_new.add("برای انتخاب کلیک کنید");

                                    for (JobPerson jobPerson : jobPersons) {
                                        jobpersonsstr_new.add(jobPerson.getName());
                                    }

                                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                                            android.R.layout.simple_spinner_item, jobpersonsstr_new);
                                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner spinner_new = new Spinner(mContext.getApplicationContext());
                                    spinner_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                                    spinner_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                    spinner_new.setAdapter(spinner_adapter);

                                    try {
                                        spinner_new.setSelection(Integer.parseInt(callMethod.ReadString(job.getTitle())));
                                    } catch (Exception e) {
                                        spinner_new.setSelection(0);

                                    }

                                    spinner_new.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            callMethod.EditString(job.getTitle(), String.valueOf(position));
                                            job.setText(jobpersonsstr_new.get(position));
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                    ll_new.addView(spinner_new);

                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                        ll_pack_h_main.addView(ll_new);
                    }

                    sendtimearray.clear();
                    sendtimearray.add("");
                    sendtimearray.add("صبح");

                    LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                            70
                    );

                    params.setMargins(30, 30, 30, 30);
                    LinearLayoutCompat ll_new = new LinearLayoutCompat(mContext.getApplicationContext());
                    ll_new.setLayoutParams(params);
                    ll_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_new.setOrientation(LinearLayoutCompat.HORIZONTAL);
                    ll_new.setWeightSum(2);


                    TextView Tv_new = new TextView(mContext.getApplicationContext());
                    Tv_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                    Tv_new.setText("نحوه ارسال :");
                    Tv_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);


                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, sendtimearray);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinner_sendtime = new Spinner(mContext.getApplicationContext());
                    spinner_sendtime.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                    spinner_sendtime.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    spinner_sendtime.setAdapter(spinner_adapter);
                    spinner_sendtime.setSelection(0);

                    spinner_sendtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            sendtime = sendtimearray.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    ll_new.addView(Tv_new);
                    ll_new.addView(spinner_sendtime);
                    ll_pack_h_main.addView(ll_new);

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });

        btn_pack_h_5.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );


            datePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");
        });


        btn_pack_h_send.setOnClickListener(v -> {
            coltrol_s = "";
            reader_s = "";
            pack_s = "";

            if (ed_pack_h_amount.getText().toString().equals("")) {
                packCount = "1";
            } else
                packCount = NumberFunctions.EnglishNumber(ed_pack_h_amount.getText().toString());

            boolean falt = false;
            String falt_message = "";

            for (Job job : jobs) {

                 if (!job.getText().equals("برای انتخاب کلیک کنید")) {
                    if (job.getJobCode().equals("1")) {
                        coltrol_s = job.getText();
                    }
                    if (job.getJobCode().equals("2")) {
                        reader_s = job.getText();
                    }
                    if (job.getJobCode().equals("3")) {
                        pack_s = job.getText();
                    }
                } else {
                    falt = true;
                    falt_message = job.getTitle();
                    break;
                }
            }


            if (!falt) {
                dialogProg();

                Call<RetrofitResponse> call3;
                if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                    call3=apiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "3", "");
                }else{
                    call3=secendApiInterface.CheckState("OcrControlled", factor.getAppOCRFactorCode(), "3", "");
                }

                call3.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        assert response.body() != null;

                        Call<RetrofitResponse> call2;
                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                            call2=apiInterface.SetPackDetail(
                                    "SetPackDetail",
                                    factor.getAppOCRFactorCode(),
                                    reader_s,
                                    coltrol_s,
                                    pack_s,
                                    NumberFunctions.EnglishNumber(date),
                                    packCount,
                                    sendtime
                            );
                        }else{
                            call2=secendApiInterface.SetPackDetail(
                                    "SetPackDetail",
                                    factor.getAppOCRFactorCode(),
                                    reader_s,
                                    coltrol_s,
                                    pack_s,
                                    NumberFunctions.EnglishNumber(date),
                                    packCount,
                                    sendtime
                            );
                        }

                        call2.enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                dialog.dismiss();
                                if (!callMethod.ReadString("Category").equals("5")) {
                                    print.Printing(factor,packCount);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("", t.getMessage());
                    }
                });


            } else {
                callMethod.showToast(falt_message + " را تکمیل کنید");
            }


        });


        dialog.show();


    }

    public void good_detail(String GoodCode) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_zoom);
        ImageView iv_good = dialog.findViewById(R.id.image_zoom_view);
        TextView tv_good_1 = dialog.findViewById(R.id.imagezoome_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.imagezoome_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.imagezoome_tv3);
        TextView tv_good_4 = dialog.findViewById(R.id.imagezoome_tv4);

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetGoodDetail("GetOcrGoodDetail", GoodCode);
        }else{
            call=secendApiInterface.GetGoodDetail("GetOcrGoodDetail", GoodCode);
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Good> goods = response.body().getGoods();

                    tv_good_1.setText(goods.get(0).getTotalAvailable());
                    tv_good_2.setText(goods.get(0).getSize());
                    tv_good_3.setText(goods.get(0).getCoverType());
                    tv_good_4.setText(goods.get(0).getPageNo());

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                Log.e("retrofit_fail", t.getMessage());
            }
        });
        byte[] BaseImageByte;
        BaseImageByte = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length), BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getWidth() * 2, BitmapFactory.decodeByteArray(BaseImageByte, 0, BaseImageByte.length).getHeight() * 2, false));

        Call<RetrofitResponse> call2;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call2=apiInterface.GetImage("getImage", GoodCode, 0, 400);
        }else{
            call2=secendApiInterface.GetImage("getImage", GoodCode, 0, 400);
        }

        call2.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        byte[] imageByteArray1;
                        imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                        iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {

                Log.e("onFailure", "" + t);
            }
        });

        dialog.show();
    }

    public void GoodScanDetail(ArrayList<Good> goodspass, String state, String barcodescan) {

        ArrayList<Good> Currctgoods = new ArrayList<>();

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.goods_scan);
        RecyclerView goodscan_recycler = dialog.findViewById(R.id.goods_scan_recyclerView);
        Button goodscan_btn = dialog.findViewById(R.id.goods_scan_btn);
        TextView goodscan_tvstatus = dialog.findViewById(R.id.goods_scan_status);


        if (goodspass.size() > 0) {
            for (Good good : goodspass) {
                if (state.equals("0"))
                    if (good.getAppRowIsControled().equals("0")) {
                        Currctgoods.add(good);
                    }
                if (state.equals("1"))
                    if (good.getAppRowIsPacked().equals("0")) {
                        Currctgoods.add(good);
                    }
            }
            if (Currctgoods.size() > 0) {
                GoodScan_Adapter goodscanadapter = new GoodScan_Adapter(Currctgoods, mContext, state, barcodescan);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);//grid
                goodscan_recycler.setLayoutManager(gridLayoutManager);
                goodscan_recycler.setAdapter(goodscanadapter);
                goodscan_recycler.setItemAnimator(new DefaultItemAnimator());
            } else {
                goodscan_tvstatus.setText("اسکن شده");
            }

        } else {
            goodscan_tvstatus.setText("در این فکتور وجود ندارد");
        }

        goodscan_btn.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


    public void LoginSetting() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loginconfig);
        EditText ed_password = dialog.findViewById(R.id.edloginconfig);
        MaterialButton btn_login = dialog.findViewById(R.id.btnloginconfig);
        btn_login.setOnClickListener(v -> {
            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {
                Intent intent = new Intent(mContext, ConfigActivity.class);
                mContext.startActivity(intent);
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }
        });
        dialog.show();
    }

    public void GetOcrFactorDetail(Factor factor) {

        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetOcrFactorDetail(
                    "GetOcrFactorDetail",
                    factor.getAppOCRFactorCode()
            );
        }else{
            call=secendApiInterface.GetOcrFactorDetail(
                    "GetOcrFactorDetail",
                    factor.getAppOCRFactorCode()
            );
        }

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    Factor Factor=response.body().getFactors().get(0);
                    factor_detail(Factor);
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {}
        });

        
    }


    public void sendfactor(final String factor_code, String signatureimage) {

        app_info();
        dialogProg();

        Call<String> call;



        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call =apiInterface.getImageData("SaveOcrImage", signatureimage, factor_code);
        }else {
            call =secendApiInterface.getImageData("SaveOcrImage", signatureimage, factor_code);
        }

        Log.e("kowsar",call.request().url().toString());

        Log.e("kowsar",signatureimage);
        Log.e("kowsar",factor_code);


        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                callMethod.showToast("فاکتور ارسال گردید");

                dbh.Insert_IsSent(factor_code);

                Intent bag = new Intent(mContext, LocalFactorListActivity.class);
                bag.putExtra("IsSent", "0");
                bag.putExtra("signature", "0");
                dialogProg.dismiss();
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(bag);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("kowsar",t.getMessage());

                Log.e("test","2");
                Log.e("test",t.getMessage());
            }
        });

    }

    public void app_info() {

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext
                .getContentResolver(), Settings.Secure.ANDROID_ID);
        String Date = Utilities.getCurrentShamsidate();
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(c.getTime());


        APIInterface apiInterface = APIClient.getCleint("http://87.107.78.234:60005/login/").create(APIInterface.class);
        Call<String> cl = apiInterface.Kowsar_log("Log_report", android_id, mContext.getString(R.string.SERVERIP), mContext.getString(R.string.app_name), "", Date + "--" + strDate, callMethod.ReadString("Deliverer"), "");
        cl.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e("ocrkowsar_onResponse", "" + response.body());
                Log.e("1", "0");

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("ocrkowsar_onFailure", "" + t);
            }
        });

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear + 1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));
    }
}
