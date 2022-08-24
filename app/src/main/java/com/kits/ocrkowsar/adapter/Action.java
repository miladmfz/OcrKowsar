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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfigActivity;
import com.kits.ocrkowsar.activity.LocalFactorListActivity;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.model.Utilities;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity {
    APIInterface apiInterface ;
    Call<String> call;
    DatabaseHelper dbh;
    private final Context mContext;
    CallMethod callMethod;

    public Action(Context mcontxt) {
        this.mContext = mcontxt;
        callMethod = new CallMethod(mContext);
        dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
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
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Good> goods= response.body().getGoods();

                    tv_good_1.setText(goods.get(0).getTotalAvailable());
                    tv_good_2.setText(goods.get(0).getSize());
                    tv_good_3.setText(goods.get(0).getCoverType());
                    tv_good_4.setText(goods.get(0).getPageNo());

                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                Log.e("retrofit_fail",t.getMessage());
            }
        });

        Call<RetrofitResponse> call2 = apiInterface.GetImage("getImage", GoodCode,0,400);
        call2.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    byte[] imageByteArray1;
                    imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                    iv_good.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                Log.e("onFailure", "" + t);
            }
        });

        dialog.show();
    }

    public void GoodScanDetail(ArrayList<Good> goodspass,String state,String barcodescan){

        ArrayList<Good> Currctgoods=new ArrayList<>();

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.goods_scan);
        RecyclerView goodscan_recycler =  dialog.findViewById(R.id.goods_scan_recyclerView);
        Button goodscan_btn =  dialog.findViewById(R.id.goods_scan_btn);
        TextView goodscan_tvstatus =  dialog.findViewById(R.id.goods_scan_status);


        if (goodspass.size()>0){
            for (Good good:goodspass){
                if(state.equals("0"))
                if (good.getAppRowIsControled().equals("0")) {
                    Currctgoods.add(good);
                }
                if(state.equals("1"))
                if (good.getAppRowIsPacked().equals("0")) {
                    Currctgoods.add(good);
                }
            }
            Log.e("test_size",Currctgoods.size()+"");
            if(Currctgoods.size()>0){
                GoodScan_Adapter goodscanadapter=new GoodScan_Adapter(Currctgoods,mContext,state,barcodescan);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);//grid
                goodscan_recycler.setLayoutManager(gridLayoutManager);
                goodscan_recycler.setAdapter(goodscanadapter);
                goodscan_recycler.setItemAnimator(new DefaultItemAnimator());
                //Currctgoods.clear();
            }else {
                goodscan_tvstatus.setText("اسکن شده");
            }

        }else {
            goodscan_tvstatus.setText("در این فکتور وجود ندارد");
        }

        goodscan_btn.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


    public void LoginSetting(){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loginconfig);
        EditText ed_password =  dialog.findViewById(R.id.edloginconfig);
        MaterialButton btn_login =  dialog.findViewById(R.id.btnloginconfig);

        Intent intent = new Intent(mContext, ConfigActivity.class);
        mContext.startActivity(intent);
//        btn_login.setOnClickListener(v -> {
//            if(NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals("1401"))
//            {
//                Intent intent = new Intent(mContext, ConfigActivity.class);
//                mContext.startActivity(intent);
//            }
//
//        });
        dialog.show();
    }


    public void sendfactor(final String factor_code, String signatureimage) {

        app_info();
        call=apiInterface.getImageData("SaveOcrImage",signatureimage,factor_code);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                callMethod.showToast("فاکتور ارسال گردید");

                dbh.Insert_IsSent(factor_code);

                Intent bag = new Intent(mContext, LocalFactorListActivity.class);
                bag.putExtra("IsSent", "0");
                bag.putExtra("signature", "0");
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(bag);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Log.e(",", t.getMessage());
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




}
