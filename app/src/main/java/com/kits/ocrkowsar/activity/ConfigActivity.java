package com.kits.ocrkowsar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigActivity extends AppCompatActivity {

    APIInterface apiInterface;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Spinner spinnerPath,spinnercategory;
    String stackcategory="همه";
    String workcategory="0";
    ArrayList<String> stacks=new ArrayList<>();
    ArrayList<String> works=new ArrayList<>();
    EditText ed_Deliverer;
    TextView tv_laststack;
    LinearLayoutCompat ll_Stack;
    MaterialButton btn_config;
    EditText ed_titlesize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Config();
        init();


    }


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        works.add("برای انتخاب کلیک کنید");
        works.add("اسکن بارکد");
        works.add("انبار");
        works.add("بسته بندی");
        works.add("تحویل");

        spinnerPath=findViewById(R.id.configactivity_spinnerstacks);
        spinnercategory =findViewById(R.id.configactivity_spinnercategory);
        ed_Deliverer =findViewById(R.id.configactivity_Deliverer);
        tv_laststack =findViewById(R.id.configactivity_laststack);
        ll_Stack=findViewById(R.id.configactivity_line_stack);
        btn_config =findViewById(R.id.configactivity_btn);
        ed_titlesize = findViewById(R.id.config_titlesize);

    }

    public void init() {

        ed_Deliverer.setText(callMethod.ReadString("Deliverer"));
        tv_laststack.setText(callMethod.ReadString("StackCategory"));
        ed_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));

        btn_config.setOnClickListener(v -> {
            callMethod.EditString("Deliverer",ed_Deliverer.getText().toString());
            callMethod.EditString("Category",workcategory);
            callMethod.EditString("StackCategory",stackcategory);
            callMethod.EditString("TitleSize",NumberFunctions.EnglishNumber(ed_titlesize.getText().toString()));
            finish();
        });

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(ConfigActivity.this,
                android.R.layout.simple_spinner_item, works);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategory.setAdapter(spinner_adapter);
        spinnercategory.setSelection(Integer.parseInt(callMethod.ReadString("Category")));


        spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workcategory=String.valueOf(position);
                if(position==2){
                    ll_Stack.setVisibility(View.VISIBLE);
                }else {
                    ll_Stack.setVisibility(View.GONE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Call<RetrofitResponse> call =apiInterface.GetCustomerPath("GetStackCategory");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                stacks.add("همه");
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for ( Good good : response.body().getGoods()) {
                        stacks.add(good.getGoodExplain4());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(ConfigActivity.this,
                            android.R.layout.simple_spinner_item, stacks);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPath.setAdapter(spinner_adapter);
                    spinnerPath.setSelection(0);
                    tv_laststack.setText(callMethod.ReadString("StackCategory"));

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });

        spinnerPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stackcategory=stacks.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


}