package com.kits.ocrkowsar.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.activity.ConfirmActivity;
import com.kits.ocrkowsar.activity.NavActivity;
import com.kits.ocrkowsar.adapter.Action;
import com.kits.ocrkowsar.adapter.Good_ProSearch_Adapter;
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


public class StackFragment extends Fragment {

    APIInterface apiInterface;
    APIInterface secendApiInterface;
    DatabaseHelper dbh ;
    ArrayList<String> GoodCodeCheck=new ArrayList<>();
    LinearLayoutCompat ll_main;

    CallMethod callMethod;
    ArrayList<Good> goods;

    String BarcodeScan;

    int width=1;
    Handler handler;
    Good_ProSearch_Adapter adapter;
    Print print;
    View view;
    Dialog dialogProg;
    RecyclerView rc_good;



    public ArrayList<Good> getGoods() {
        return goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.goods = goods;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("kowsar","5 " );

        view= inflater.inflate(R.layout.fragment_stack, container, false);
        ll_main = view.findViewById(R.id.stackfragment_layout);
        rc_good = view.findViewById(R.id.stackfragment_good_recy);
        Log.e("kowsar","6 " );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("kowsar","7 " );


        callMethod = new CallMethod(requireActivity());
        dbh = new DatabaseHelper(requireActivity(), callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);
        handler=new Handler();
        print=new Print(requireActivity());

        dialogProg = new Dialog(requireActivity());
        dialogProg.setContentView(R.layout.rep_prog);
        dialogProg.findViewById(R.id.rep_prog_text).setVisibility(View.GONE);
        Log.e("kowsar","8 " );

        callrecycler();

    }




    public void callrecycler() {


        adapter = new Good_ProSearch_Adapter(goods, requireActivity());

        rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        rc_good.setAdapter(adapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());
        Log.e("kowsar","9 " );

    }


    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}