package com.kits.ocrkowsar.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.Good_StackFragment_Adapter;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.application.Print;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;


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
    Good_StackFragment_Adapter adapter;
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

        view= inflater.inflate(R.layout.fragment_stack, container, false);
        ll_main = view.findViewById(R.id.stackfragment_layout);
        rc_good = view.findViewById(R.id.stackfragment_good_recy);

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


        adapter = new Good_StackFragment_Adapter(goods, requireActivity());

        rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 1));
        rc_good.setAdapter(adapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

    }


    public String getBarcodeScan() {
        return BarcodeScan;
    }

    public void setBarcodeScan(String barcodeScan) {
        BarcodeScan = barcodeScan;
    }
}