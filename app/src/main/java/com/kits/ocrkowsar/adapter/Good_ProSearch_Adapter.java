package com.kits.ocrkowsar.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIClientSecond;
import com.kits.ocrkowsar.webService.APIInterface;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.model.Good;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Good_ProSearch_Adapter extends RecyclerView.Adapter<Good_ProSearch_Adapter.GoodViewHolder>{
    private final Context mContext;
    DecimalFormat decimalFormat= new DecimalFormat("0,000");
    private List<Good> goods;
    Call<RetrofitResponse> call2;
    APIInterface apiInterface ;
    APIInterface secendApiInterface ;


    private final Action action;
    CallMethod callMethod;



    public Good_ProSearch_Adapter(List<Good> goods, Context context)
    {
        this.mContext = context;
        this.goods = goods;
        this.action = new Action(context);
        this.callMethod = new CallMethod(context);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);


    }
    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_prosearch, parent, false);
        return new GoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        Log.e("kowsar","10 start"+ position );


        holder.goodnameTextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));
        holder.sellprice_tv.setText(NumberFunctions.PerisanNumber(goods.get(position).getMaxSellPrice().substring(0,goods.get(position).getMaxSellPrice().indexOf("."))));
        holder.amount_tv.setText(NumberFunctions.PerisanNumber(goods.get(position).getStackAmount().substring(0,goods.get(position).getStackAmount().indexOf("."))));
        holder.stacklocation_tv.setText(NumberFunctions.PerisanNumber(goods.get(position).getStackLocation()));
        Log.e("kowsar","10 getGoodImageName"+ position );

        if (!goods.get(position).getGoodImageName().equals("")) {


            Glide.with(holder.img)
                    .asBitmap()
                    .load(Base64.decode(goods.get(position).getGoodImageName(), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.img);


        } else
        {


            if (callMethod.ReadString("FactorDbName").equals(callMethod.ReadString("DbName"))){
                call2=apiInterface.GetImage("getImage", goods.get(position).getGoodCode(),0,400);
            }else{
                call2=secendApiInterface.GetImage("getImage", goods.get(position).getGoodCode(),0,400);
            }


            call2.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        try {
                        if (!response.body().getText().equals("no_photo")) {
                            goods.get(position).setGoodImageName(response.body().getText());
                        } else {
                            goods.get(position).setGoodImageName(String.valueOf(R.string.no_photo));

                        }

                            Glide.with(holder.img)
                                    .asBitmap()
                                    .load(Base64.decode(goods.get(position).getGoodImageName(), Base64.DEFAULT))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .fitCenter()
                                    .into(holder.img);
                        }catch (Exception e){
                            Log.e("kowsar","10 Exception"+ e.getMessage() );
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    Log.e("kowsar","10 Throwable"+ t.getMessage() );

                }
            });
        }







        holder.btnadd.setOnClickListener(view -> {
            action.GoodStackLocation(goods.get(position));


        });

    }

    @Override
    public int getItemCount()
    {
        return goods.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull GoodViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (call2.isExecuted()) {
            call2.cancel();
        }
    }


    class GoodViewHolder extends RecyclerView.ViewHolder
    {
        private TextView goodnameTextView;
        private TextView sellprice_tv;
        private TextView amount_tv;
        private TextView stacklocation_tv;
        private TextView totalstate;
        private Button btnadd;
        private ImageView img ;
        private LinearLayoutCompat ggg ;
        MaterialCardView rltv;

        GoodViewHolder(View itemView)
        {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_prosearch_name);

            sellprice_tv = itemView.findViewById(R.id.good_prosearch_sellprice);
            amount_tv = itemView.findViewById(R.id.good_prosearch_amount);
            stacklocation_tv = itemView.findViewById(R.id.good_prosearch_stacklocation);

            totalstate = itemView.findViewById(R.id.good_prosearch_totalstate);
            img =  itemView.findViewById(R.id.good_prosearch_img) ;
            rltv =  itemView.findViewById(R.id.good_prosearch);
            btnadd = itemView.findViewById(R.id.good_prosearch_btn);
            ggg = itemView.findViewById(R.id.proserch_ggg);
        }
    }



}
