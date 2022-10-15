package com.kits.ocrkowsar.Fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfigActivity;
import com.kits.ocrkowsar.activity.ConfirmActivity;
import com.kits.ocrkowsar.activity.NavActivity;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.application.CallMethod;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    APIInterface apiInterface;
    String date = "";
    TextView ed_pack_h_date;
    DatabaseHelper dbh;
    ArrayList<String> GoodCodeCheck = new ArrayList<>();
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
    Integer lastCunter = 0;
    Intent intent;
    int width = 1;
    Handler handler;
    TextView tv_company;
    TextView tv_customername;
    TextView tv_factorcode;
    TextView tv_factordate;
    TextView tv_address;
    TextView tv_phone;
    TextView tv_total_amount;
    TextView tv_total_price;
    ArrayList<Job> jobs;

    View view;

    String coltrol_s = "";
    String reader_s = "";
    String pack_s = "";
    String packCount = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pack, container, false);
        ll_main = view.findViewById(R.id.packfragment_layout);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callMethod = new CallMethod(requireActivity());
        dbh = new DatabaseHelper(requireActivity(), callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        handler = new Handler();
        for (final String[] ignored : arraygood_shortage) {
            arraygood_shortage.add(new String[]{"goodcode", "amount "});
        }
        DisplayMetrics metrics = new DisplayMetrics();
        view.getDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        CreateView_Pack();
    }


    @SuppressLint("RtlHardcoded")
    public void CreateView_Pack() {
        ll_main.removeAllViews();
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


        if (!factor.getNewSumPrice().equals(factor.getSumPrice())) {
            TextView tv_total_newprice = new TextView(requireActivity().getApplicationContext());
            tv_total_newprice.setText(NumberFunctions.PerisanNumber(" قیمت کل(جدید) : " + decimalFormat.format(Integer.valueOf(factor.getNewSumPrice())) + " ریال"));
            tv_total_newprice.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            tv_total_newprice.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            tv_total_newprice.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
            tv_total_newprice.setGravity(Gravity.RIGHT);

            ll_factor_summary.addView(tv_total_newprice);
        }

        int countergood = 0;
        for (Good Singlegood : goods) {
            countergood++;
            ll_good_body_detail.addView(CreateGoodViewForPack(Singlegood, countergood));

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

        if (callMethod.ReadString("Category").equals("3")) {
            ll_main.addView(btn_shortage);
        }

        ll_main.addView(ll_factor_summary);
        ll_main.addView(ll_send_confirm);
        ConfirmCount_Pack();

        btn_shortage.setOnClickListener(v -> CreateView_shortage());

        btn_send.setOnClickListener(v -> Pack_detail(factor.getAppOCRFactorCode()));


        btn_confirm.setOnClickListener(v -> {

            int b = GoodCodeCheck.size();
            final int[] conter = {0};

            for (String goodchecks : GoodCodeCheck) {


                Call<RetrofitResponse> call = apiInterface.CheckState("OcrControlled", goodchecks, "2", "");
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            conter[0] = conter[0] + 1;
                            if (conter[0] == b) {
                                intent = new Intent(requireActivity(), ConfirmActivity.class);

                                intent.putExtra("ScanResponse", BarcodeScan);
                                intent.putExtra("State", "1");
                                intent.putExtra("FactorImage", "");
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("", t.getMessage());
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
    public View CreateGoodViewForPack(Good good, int j) {
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

        CheckBox checkBox = new CheckBox(requireActivity());

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
        tv_price.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 3.5));

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

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
        tv_goodname.setText(NumberFunctions.PerisanNumber(good.getGoodName()));
        tv_amount.setText(NumberFunctions.PerisanNumber(good.getFacAmount()));
        tv_price.setText(NumberFunctions.PerisanNumber(good.getGoodMaxSellPrice()));

        tv_gap.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        checkBox.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_goodname.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));

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
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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


        });


        tv_goodname.setOnClickListener(v -> image_zome_view(goods.get(fa).getGoodCode()));


        return ll_factor_row;
    }


    @SuppressLint("RtlHardcoded")
    public void CreateView_shortage() {
        ll_main.removeAllViews();

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


        tv_company.setText(NumberFunctions.PerisanNumber("بخش بسته بندی"));
        tv_customername.setText(NumberFunctions.PerisanNumber(" نام مشتری :   " + factor.getCustName()));
        tv_factorcode.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + factor.getFactorPrivateCode()));
        tv_factordate.setText(NumberFunctions.PerisanNumber(" تارخ فاکتور :   " + factor.getFactorDate()));

        btn_confirm.setText("ارسال کسری");
        btn_send.setText("بازگشت");
        btn_shortage.setText("اعلام کسر موجودی");


        int countergood = 0;
        for (Good singlegood : goods) {
            countergood++;

            if (singlegood.getAppRowIsPacked().equals("0")) {
                ll_good_body_detail.addView(CreateGoodViewForshortage(singlegood, countergood));

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

        btn_shortage.setOnClickListener(v -> {

        });

        btn_send.setOnClickListener(v -> requireActivity().finish());

        btn_confirm.setBackgroundResource(R.color.red_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_send.setBackgroundResource(R.color.green_500);
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_confirm.setEnabled(true);

        btn_confirm.setOnClickListener(v -> {


            for (String[] goodchecks : arraygood_shortage) {
                Log.e("123", "start");

                Call<RetrofitResponse> call = apiInterface.GoodShortage("ocrShortage", goodchecks[0], goodchecks[1]);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            lastCunter++;

                            Log.e("123", "---------");
                            Log.e("123", "" + lastCunter);
                            Log.e("123", "" + arraygood_shortage.size());

                            if (lastCunter == arraygood_shortage.size()) {
                                requireActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                        Log.e("123", t.getMessage());
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
    public View CreateGoodViewForshortage(Good good, int j) {
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

        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_goodname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        et_amountshortage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        checkBox.setText(NumberFunctions.PerisanNumber(String.valueOf(j)));
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

        ll_factor_row.addView(ll_details);
        ll_factor_row.addView(vp_rows);


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
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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


        });


        tv_goodname.setOnClickListener(v -> image_zome_view(goods.get(fa).getGoodCode()));

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

                    if (Integer.parseInt(text.toString()) > Integer.parseInt(good.getFacAmount())) {
                        et_amountshortage.setText("");
                        callMethod.showToast("از مقدار فاکتور بیشتر می باشد");
                    } else {
                        arraygood_shortage.add(new String[]{good.getAppOCRFactorRowCode(), text.toString()});

                    }

                } catch (Exception ignored) {

                }


            }
        });
        return ll_factor_row;
    }


    @SuppressLint("RtlHardcoded")
    public void setGravity() {
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
    }

    public void setTextSize() {

        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_customername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factorcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_factordate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        btn_confirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        btn_send.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
    }

    public void setBackgroundResource() {
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        btn_confirm.setBackgroundResource(R.color.green_800);
        btn_send.setBackgroundResource(R.color.red_700);
        btn_shortage.setBackgroundResource(R.color.orange_500);
    }

    public void setTextColor() {
        tv_company.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_customername.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factorcode.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_factordate.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_address.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_phone.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_amount.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        tv_total_price.setTextColor(requireActivity().getColor(R.color.colorPrimaryDark));
        btn_confirm.setTextColor(requireActivity().getColor(R.color.white));
        btn_send.setTextColor(requireActivity().getColor(R.color.white));
        btn_shortage.setTextColor(requireActivity().getColor(R.color.Black));

    }

    public void setPadding() {
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
    }

    public void setLayoutParams() {
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
        tv_address = new TextView(requireActivity().getApplicationContext());
        tv_phone = new TextView(requireActivity().getApplicationContext());
        tv_total_amount = new TextView(requireActivity().getApplicationContext());
        tv_total_price = new TextView(requireActivity().getApplicationContext());
        btn_confirm = new Button(requireActivity().getApplicationContext());
        btn_send = new Button(requireActivity().getApplicationContext());
        btn_shortage = new Button(requireActivity().getApplicationContext());


    }

    public void setOrientation() {

        ll_title.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_good_body.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_good_body_detail.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_factor_summary.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_send_confirm.setOrientation(LinearLayoutCompat.HORIZONTAL);
    }

    public void setLayoutDirection() {
        ll_title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_good_body_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_factor_summary.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ll_send_confirm.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    public void ConfirmCount_Pack() {
        int ConfirmCounter = 0;
        for (Good g : goods) {
            if (g.getAppRowIsPacked().equals("1")) {
                ConfirmCounter++;
            }
        }
        if (goods.size() == ConfirmCounter) {
            btn_confirm.setBackgroundResource(R.color.grey_60);
            btn_confirm.setTextColor(requireActivity().getColor(R.color.Black));
            btn_confirm.setEnabled(false);
            callMethod.showToast("اماده ارسال می باشد");
        } else {
            btn_send.setBackgroundResource(R.color.grey_60);
            btn_send.setTextColor(requireActivity().getColor(R.color.Black));
            btn_send.setEnabled(false);
        }
    }


    public void image_zome_view(String GoodCode) {

        Action action = new Action(requireActivity());
        action.good_detail(GoodCode);

    }

    @SuppressLint("NewApi")
    public void Pack_detail(String FactorOcrCode) {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pack_header);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);


        MaterialButton btn_pack_h_send = dialog.findViewById(R.id.pack_header_send);
        MaterialButton btn_pack_h_5 = dialog.findViewById(R.id.pack_header_btn5);
        EditText ed_pack_h_amount = dialog.findViewById(R.id.pack_header_packamount);

        ed_pack_h_date = dialog.findViewById(R.id.pack_header_senddate);

        PersianCalendar persianCalendar = new PersianCalendar();
        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (persianCalendar.getPersianMonth()+1);
        tdayOfMonth = "0" + persianCalendar.getPersianDay();
        date = persianCalendar.getPersianYear() + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));


        LinearLayoutCompat ll_pack_h_main = dialog.findViewById(R.id.packheader_linejob);
        Call<RetrofitResponse> call = apiInterface.GetJob("TestJob", "Ocr" + callMethod.ReadString("Category"));
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
                        LinearLayoutCompat ll_new = new LinearLayoutCompat(requireActivity().getApplicationContext());
                        ll_new.setLayoutParams(params);
                        ll_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        ll_new.setOrientation(LinearLayoutCompat.HORIZONTAL);
                        ll_new.setWeightSum(2);


                        TextView Tv_new = new TextView(requireActivity().getApplicationContext());
                        Tv_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                        Tv_new.setText(job.getTitle());
                        Tv_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                        ll_new.addView(Tv_new);

                        Call<RetrofitResponse> call1 = apiInterface.GetJobPerson("TestJobPerson", job.getTitle());
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

                                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(requireActivity(),
                                            android.R.layout.simple_spinner_item, jobpersonsstr_new);
                                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner spinner_new = new Spinner(requireActivity().getApplicationContext());
                                    spinner_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1));
                                    spinner_new.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                    spinner_new.setAdapter(spinner_adapter);
                                    spinner_new.setSelection(0);

                                    spinner_new.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (position > 0) {
                                                job.setText(jobpersonsstr_new.get(position));


                                            }
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
                                Log.e("test_", t.getMessage());

                            }
                        });


                        ll_pack_h_main.addView(ll_new);
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


        btn_pack_h_send.setOnClickListener(v -> {


            if (ed_pack_h_amount.getText().toString().equals("")) {
                packCount = "1";
            }

            jobs.forEach(job -> {
                if (job.getJobCode().equals("1")) {
                    coltrol_s = job.getText();
                }
                if (job.getJobCode().equals("2")) {
                    reader_s = job.getText();
                }
                if (job.getJobCode().equals("3")) {
                    pack_s = job.getText();
                }
            });

//            Call<RetrofitResponse> call3 = apiInterface.CheckState("OcrControlled", FactorOcrCode, "3", "");
//            call3.enqueue(new Callback<>() {
//                @Override
//                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                    Call<RetrofitResponse> call2 = apiInterface.SetPackDetail("SetPackDetail", FactorOcrCode, reader_s, coltrol_s, pack_s, date, packCount[0]);
//                    call2.enqueue(new Callback<>() {
//                        @Override
//                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                            dialog.dismiss();
//                            requireActivity().finish();
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                            Log.e("", t.getMessage());
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                    Log.e("", t.getMessage());
//                }
//            });


        });


        btn_pack_h_5.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );

            datePickerDialog.show(requireActivity().getFragmentManager(),"Datepickerdialog");
        });


        dialog.show();


    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear+1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        ed_pack_h_date.setText(NumberFunctions.PerisanNumber(date));
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

    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}