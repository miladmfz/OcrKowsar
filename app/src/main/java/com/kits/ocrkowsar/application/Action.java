package com.kits.ocrkowsar.application;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import com.kits.ocrkowsar.activity.ConfirmActivity;
import com.kits.ocrkowsar.activity.LocalFactorListActivity;
import com.kits.ocrkowsar.adapter.GoodScan_Adapter;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.application.Print;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.Job;
import com.kits.ocrkowsar.model.JobPerson;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity implements DatePickerDialog.OnDateSetListener {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    ArrayList<Good> goods = new ArrayList<>();
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

    Handler handler;



    public Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);

        handler=new Handler();
        dialog = new Dialog(mContext);
        dialogProg = new Dialog(mContext);
        print = new Print(mContext);


    }
    public void dialogProg() {
        dialogProg.setContentView(R.layout.rep_prog);
        tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
        tv_rep.setVisibility(View.GONE);
        dialogProg.show();
    }
    public void dialogProg_dismiss() {
        dialogProg.dismiss();
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


            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                    assert response.body() != null;
                    if (response.body().getText().equals("Done")) {
                        dialog.dismiss();
                        dialogProg.dismiss();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });
        });

        btn_2.setOnClickListener(v -> {
            Pack_detail(factor);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void checkSumAmounthint(Factor factor) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkamount);
        EditText edamount = dialog.findViewById(R.id.edamount);
        MaterialButton btncheckamount = dialog.findViewById(R.id.btncheckamount);

        edamount.setText(factor.getSumAmount());
        edamount.setEnabled(false);
        btncheckamount.setVisibility(View.GONE);

        dialog.show();


    }
    public void checkSumAmount(Factor factor) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkamount);
        EditText edamount = dialog.findViewById(R.id.edamount);
        MaterialButton btncheckamount = dialog.findViewById(R.id.btncheckamount);


        btncheckamount.setOnClickListener(v -> {
            if (NumberFunctions.EnglishNumber(edamount.getText().toString()).equals(factor.getSumAmount())) {
                Pack_detail(factor);
            }else {
                callMethod.showToast("تعداد وارد شده صحیح نیست");
            }
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

        call.enqueue(new Callback<RetrofitResponse>() {
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

                        call1.enqueue(new Callback<RetrofitResponse>() {
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

                    if (callMethod.ReadBoolan("SendTimeType")) {

                        sendtimearray.clear();
                        sendtimearray.add("");
                        sendtimearray.add("صبح");
                        sendtimearray.add("ظهر");
                        sendtimearray.add("شب");

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

                Log.e("kowsarr22",job.getText());
                Log.e("kowsarr22_jobcode=",job.getJobCode());
                Log.e("kowsarr22_jobcode=",job.getText());
                Log.e("kowsarr22_jobcode=",job.getTitle());


                //vase qoqnos shod 1-2-3
                //vase gostaresh shod 3-4-5



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

                call3.enqueue(new Callback<RetrofitResponse>() {
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

                        call2.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                dialog.dismiss();
                                //print.Printing(factor,goods,packCount,"0");

                                if (!callMethod.ReadString("Category").equals("5")) {
                                    print.Printing(factor,goods,packCount,"0");
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

    public void goodamount_detail(String amount,String shortage) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.amount_zoom);
        TextView tv_good_1 = dialog.findViewById(R.id.amountzoome_tv1);
        TextView tv_good_2 = dialog.findViewById(R.id.amountzoome_tv2);
        TextView tv_good_3 = dialog.findViewById(R.id.amountzoome_tv3);

        tv_good_1.setText(NumberFunctions.PerisanNumber(amount));

        int finalShortage = (shortage != null||shortage != "null") ? Integer.parseInt(shortage) : 0;

        tv_good_2.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(amount) - finalShortage)));

        tv_good_3.setText(NumberFunctions.PerisanNumber(shortage));

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
        TextView tv_good_5 = dialog.findViewById(R.id.imagezoome_tv5);
        LinearLayoutCompat ll_amonut = dialog.findViewById(R.id.imagezoome_ll1_tv1);


        Call<RetrofitResponse> call;
        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
            call=apiInterface.GetGoodDetail("GetOcrGoodDetail", GoodCode);
        }else{
            call=secendApiInterface.GetGoodDetail("GetOcrGoodDetail", GoodCode);
        }
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Good> goods = response.body().getGoods();


                    if (!callMethod.ReadBoolan("ShowAmount")){
                        ll_amonut.setVisibility(View.GONE);
                    }

                    tv_good_1.setText(goods.get(0).getTotalAvailable());
                    tv_good_2.setText(goods.get(0).getSize());
                    tv_good_3.setText(goods.get(0).getCoverType());
                    tv_good_4.setText(goods.get(0).getPageNo());
                    tv_good_5.setText(goods.get(0).getGoodExplain2());

                    //tv_good_5.setText(NumberFunctions.PerisanNumber(goods.get(0).getFormNo()));


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
            call2=apiInterface.GetImage("getImage", GoodCode, 0, 250);
        }else{
            call2=secendApiInterface.GetImage("getImage", GoodCode, 0, 250);
        }

        call2.enqueue(new Callback<RetrofitResponse>() {
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
        ArrayList<Good> CurrctgoodsForBarcode = new ArrayList<>();

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.goods_scan);
        RecyclerView goodscan_recycler = dialog.findViewById(R.id.goods_scan_recyclerView);
        Button goodscan_btn = dialog.findViewById(R.id.goods_scan_btn);
        TextView goodscan_tvstatus = dialog.findViewById(R.id.goods_scan_status);
        EditText ed_goodscan = dialog.findViewById(R.id.goods_scan_barcode);


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
        ed_goodscan.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }



                    @Override
                    public void afterTextChanged( Editable editable) {

                        dialogProg();
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {


                            CurrctgoodsForBarcode.clear();
                            if (goodspass.size() > 0) {
                                for (Good good : goodspass) {
                                    if (state.equals("0"))
                                        if (good.getAppRowIsControled().equals("0")) {
                                            CurrctgoodsForBarcode.add(good);
                                        }
                                    if (state.equals("1"))
                                        if (good.getAppRowIsPacked().equals("0")) {
                                            CurrctgoodsForBarcode.add(good);
                                        }
                                }
                                if (CurrctgoodsForBarcode.size() == 1) {

                                    if (state.equals("0")){

                                        Call<RetrofitResponse> call;
                                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                            call=apiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "0", "");
                                        }

                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, ConfirmActivity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "0");
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                                Log.e("kowsar_onFailure", t.getMessage());
                                            }
                                        });

                                    }else if (state.equals("1"))
                                    {

                                        Call<RetrofitResponse> call;
                                        if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                                            call=apiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }else{
                                            call=secendApiInterface.CheckState("OcrControlled", CurrctgoodsForBarcode.get(0).getAppOCRFactorRowCode(), "2", "");
                                        }
                                        call.enqueue(new Callback<RetrofitResponse>() {
                                            @Override
                                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                                if (response.isSuccessful()) {

                                                    Intent intent = new Intent(mContext, ConfirmActivity.class);
                                                    intent.putExtra("ScanResponse", barcodescan);
                                                    intent.putExtra("State", "1");
                                                    ((Activity) mContext).finish();
                                                    mContext.startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                                                Log.e("kowsar_onFailure", t.getMessage());
                                            }
                                        });

                                    }
                                }
                            }

                        },  Integer.parseInt(callMethod.ReadString("Delay")));






                    }

                }
        );

        dialog.show();
    }


    public void LoginSetting() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loginconfig);
        EditText ed_password = dialog.findViewById(R.id.edloginconfig);
        MaterialButton btn_login = dialog.findViewById(R.id.btnloginconfig);

        ed_password.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {

                        if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).length()>5) {
                            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                                Intent intent = new Intent(mContext, ConfigActivity.class);
                                mContext.startActivity(intent);
                            } else {
                                callMethod.showToast("رمز عبور صیحیح نیست");
                            }

                        }
                    }
                });
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

        call.enqueue(new Callback<RetrofitResponse>() {
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


        call.enqueue(new Callback<String>() {
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


    public void GoodStackLocation(Good good) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.stacklocation);


        Button explain_btn = dialog.findViewById(R.id.stacklocation_explain_btn);
        final TextView goodname_tv = dialog.findViewById(R.id.stacklocation_goodname_tv);
        final EditText stacklocation_et = dialog.findViewById(R.id.stacklocation_explain_et);


        goodname_tv.setText(good.getGoodName());
        stacklocation_et.setText(good.getStackLocation());
        stacklocation_et.selectAll();



        dialog.show();
        stacklocation_et.requestFocus();
        stacklocation_et.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(stacklocation_et, InputMethodManager.SHOW_IMPLICIT);
        }, 500);




        explain_btn.setOnClickListener(view -> {
            String safeInput = stacklocation_et.getText().toString().replaceAll("[;'\"--#/*]", "");

            dialogProg();
            tv_rep.setText("در حال ارسال اطلاعات");
            Call<RetrofitResponse> call = apiInterface.SetStackLocation(
                    "SetStackLocation",
                    good.getGoodCode(),
                    NumberFunctions.EnglishNumber(safeInput)
            );

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        dialog.dismiss();
                        dialogProg.dismiss();
                        callMethod.showToast("ثبت گردید");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                    dialog.dismiss();
                    dialogProg.dismiss();
                    callMethod.showToast("ثبت نگردید");

                }
            });
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
