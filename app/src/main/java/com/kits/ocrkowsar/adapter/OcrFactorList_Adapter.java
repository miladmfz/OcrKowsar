package com.kits.ocrkowsar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfirmActivity;
import com.kits.ocrkowsar.activity.FactorActivity;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OcrFactorList_Adapter extends RecyclerView.Adapter<OcrFactorList_Adapter.facViewHolder> {
    APIInterface apiInterface ;

    private final Context mContext;
    Intent intent;
    ArrayList<Factor> factors = new ArrayList<>();
    ArrayList<Factor> factors_tmp= new ArrayList<>();
    ArrayList<Factor> factors_get;
    String state ;
    String filter;
    String path;
    CallMethod callMethod;

    public OcrFactorList_Adapter(ArrayList<Factor> retrofitFactors,String State,String efilter,String pathfilter, Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(context);
        this.state = State;
        this.filter = efilter;
        this.path = pathfilter;
        this.factors_get = retrofitFactors;
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        if(!path.equals("همه")){
            for ( Factor f : factors_get) {
                if(f.getCustomerPath().equals(pathfilter)){
                    factors_tmp.add(f);
                }
            }
        }else {
            this.factors_tmp = factors_get;
        }



        switch (filter){
            case "0":
                this.factors = factors_tmp;
                break;
            case "1":
                for (Factor f : factors_tmp) {
                    if(f.getHasShortage().equals("1")){
                        this.factors.add(f);
                    }
                }
                break;
            case "2":
                for (Factor f : factors_tmp) {
                    if(f.getIsEdited().equals("1")){
                        factors.add(f);
                    }
                }
                break;

            case "3":
                for (Factor f : factors_tmp) {
                    if(f.getHasShortage().equals("1")){
                        if(f.getIsEdited().equals("1")){
                            factors.add(f);
                        }
                    }
                }
                break;
        }




    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.factor_list, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {



        Factor factor =factors.get(position);


        holder.fac_customer.setText(NumberFunctions.PerisanNumber(factor.getCustName()));
        holder.fac_code.setText(NumberFunctions.PerisanNumber(factor.getFactorPrivateCode()));
        holder.fac_customercode.setText(NumberFunctions.PerisanNumber(factors.get(position).getCustomerCode()));

        if(state.equals("0")){
            if(factor.getIsEdited().equals("1")){
                holder.fac_hasedite.setText("اصلاح شده");
            }else {
                holder.fac_hasedite.setText(" ");
            }
            if(factor.getHasShortage().equals("1")){
                holder.fac_hasedite.setText("کسری موجودی");
            }else {
                holder.fac_hasedite.setText(" ");
            }
        }
        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(factor.getFactorDate()));
        if(factors.get(position).getAppIsControled().equals("1")) {

            if (factor.getAppIsPacked().equals("1")) {
                holder.fac_state.setText("مرحله تحویل");
            }else if(factor.getAppIsPacked().equals("0")){
                holder.fac_state.setText("مرحله بسته بندی");
            }

        }else {
            holder.fac_state.setText("مرحله انبار");

        }

        if(callMethod.ReadString("Category").equals("4")) {
            holder.fac_factor_btn.setText("دریافت فاکتور");
        }
        if(state.equals("4")){
            holder.fac_factor_btn.setVisibility(View.GONE);
        }else {
            holder.fac_factor_btn.setVisibility(View.VISIBLE);
        }


        holder.fac_factor_btn.setOnClickListener(v -> {

            if(callMethod.ReadString("Category").equals("4")) {

                Call<RetrofitResponse> call =apiInterface.CheckState("OcrDeliverd",factor.getAppOCRFactorCode(),"1",callMethod.ReadString("Deliverer"));
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        if(response.isSuccessful()) {
                            Log.e("test","0");
                            assert response.body() != null;
                            if (response.body().getFactors().get(0).getErrCode().equals("0")){
                                intent = new Intent(mContext, FactorActivity.class);
                                intent.putExtra("ScanResponse", factor.getAppTcPrintRef());
                                intent.putExtra("FactorImage", "");
                                mContext.startActivity(intent);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        Log.e("test","1");

                        Log.e("test",t.getMessage());
                    }
                });


            }else {
                intent = new Intent(mContext, ConfirmActivity.class);
                intent.putExtra("ScanResponse", factor.getAppTcPrintRef());
                intent.putExtra("State",state);
                mContext.startActivity(intent);
            }

        });

    }

    @Override
    public int getItemCount() {
        return factors.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView fac_customer;
        private final TextView fac_customercode;
        private final TextView fac_code;
        private final TextView fac_hasedite;
        private final TextView fac_kowsardate;
        private final TextView fac_state;
        private final Button fac_factor_btn;

        MaterialCardView fac_rltv;

        facViewHolder(View itemView) {
            super(itemView);

            fac_customer = itemView.findViewById(R.id.factor_list_customer);
            fac_customercode = itemView.findViewById(R.id.factor_list_customercode);

            fac_code = itemView.findViewById(R.id.factor_list_privatecode);
            fac_hasedite = itemView.findViewById(R.id.factor_list_hasedited);
            fac_kowsardate = itemView.findViewById(R.id.factor_list_kowsardate);
            fac_state = itemView.findViewById(R.id.factor_list_state);
            fac_factor_btn = itemView.findViewById(R.id.factor_list_btn);

            fac_rltv = itemView.findViewById(R.id.factor_list);
        }
    }


}
