package com.kits.ocrkowsar.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfigActivity;
import com.kits.ocrkowsar.activity.FactorHeaderActivity;
import com.kits.ocrkowsar.activity.NavActivity;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.model.Utilities;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity {
    String date;
    APIInterface apiInterface ;
    Call<String> call;
    DatabaseHelper dbh;
    private final Context mContext;
    CallMethod callMethod;

    public Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        dbh = new DatabaseHelper(mContext, callMethod.ReadString("UseSQLiteURL"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
    }


    public void good_detail(String GoodCode){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_zoom);
        ImageView iv_good =  dialog.findViewById(R.id.image_zoom_view);
        TextView tv_good_1 =  dialog.findViewById(R.id.imagezoome_tv1);
        TextView tv_good_2 =  dialog.findViewById(R.id.imagezoome_tv2);
        TextView tv_good_3 =  dialog.findViewById(R.id.imagezoome_tv3);
        TextView tv_good_4 =  dialog.findViewById(R.id.imagezoome_tv4);

        Call<RetrofitResponse> call = apiInterface.GetGoodDetail("GetOcrGoodDetail",GoodCode);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                   ArrayList<Good> goods= response.body().getGoods();

                    tv_good_1.setText(goods.get(0).getTotalAvailable());
                    tv_good_2.setText(goods.get(0).getSize());
                    tv_good_3.setText(goods.get(0).getCoverType());
                    tv_good_4.setText(goods.get(0).getPageNo());

                }
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                Log.e("retrofit_fail",t.getMessage());
            }
        });







        Call<String> call2 = apiInterface.GetImage("getImage", GoodCode,0,400);
        call2.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call2, Response<String> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    Glide.with(iv_good)
                            .asBitmap()
                            .load(Base64.decode(response.body(), Base64.DEFAULT))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .fitCenter()
                            .into(iv_good);
                }
            }
            @Override
            public void onFailure(Call<String> call2, Throwable t) {
                Log.e("onFailure", "" + t.toString());
            }
        });

        dialog.show();
    }


    public void LoginSetting(){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loginconfig);
        EditText ed_password =  dialog.findViewById(R.id.edloginconfig);
        MaterialButton btn_login =  dialog.findViewById(R.id.btnloginconfig);


        btn_login.setOnClickListener(v -> {
            if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals("1401"))
            {
                Intent intent = new Intent(mContext, ConfigActivity.class);
                mContext.startActivity(intent);
            }

        });
        dialog.show();
    }


    public void sendfactor(final String factor_code, String signatureimage) {

        app_info();
        call=apiInterface.getImageData("SaveOcrImage",signatureimage,factor_code);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                callMethod.showToast("فاکتور ارسال گردید");

                dbh.Insert_IsSent(factor_code);

                ((Activity) mContext).recreate();
                Intent bag = new Intent(mContext, FactorHeaderActivity.class);
                bag.putExtra("IsSent", "0");
                bag.putExtra("signature", "0");
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(bag);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Log.e(",",t.getMessage());
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
        cl.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("ocrkowsar_onResponse", "" + response.body());
                Log.e("1","0");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ocrkowsar_onFailure", "" + t.toString());
            }
        });

    }


    public String arabicToenglish(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }


}
