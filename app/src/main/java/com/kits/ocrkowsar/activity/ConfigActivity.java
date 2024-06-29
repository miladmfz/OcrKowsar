package com.kits.ocrkowsar.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.application.ImageInfo;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Good;
import com.kits.ocrkowsar.model.Job;
import com.kits.ocrkowsar.model.JobPerson;
import com.kits.ocrkowsar.model.NumberFunctions;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigActivity extends AppCompatActivity  {

    APIInterface apiInterface;
    APIInterface secendApiInterface;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Spinner spinnerPath,spinnercategory,spinnerjob,spinnerjobperson,spinnerActiveDatabase;
    String stackcategory="همه";
    String workcategory="0";
    ArrayList<String> jobsstr=new ArrayList<>();
    ArrayList<String> jobpersonsstr=new ArrayList<>();
    ArrayList<Integer> jobpersonsref_int=new ArrayList<>();
    ArrayList<String> stacks=new ArrayList<>();
    ArrayList<String> works=new ArrayList<>();
    ArrayList<String> ActiveDatabase_array=new ArrayList<>();
    TextView ed_Deliverer;
    TextView tv_laststack;
    TextView tv_delay;
    LinearLayoutCompat ll_Stack;
    MaterialButton btn_config;
    EditText ed_titlesize;
    SwitchMaterial sm_arabictext;
    ImageInfo imageInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Config();
        init();


    }


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        imageInfo = new ImageInfo(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        secendApiInterface = APIClient.getCleint(callMethod.ReadString("SecendServerURL")).create(APIInterface.class);

        ActiveDatabase_array.add("هر دو دیتابیس");
        ActiveDatabase_array.add("دیتابیس اول");
        ActiveDatabase_array.add("دیتابیس دوم");



        works.add("برای انتخاب کلیک کنید");
        works.add("اسکن بارکد");
        works.add("انبار");
        works.add("بسته بندی");
        works.add("تحویل");
        works.add("مدیریت");
        works.add("جانمایی انبار");

        spinnerPath=findViewById(R.id.configactivity_spinnerstacks);
        spinnercategory =findViewById(R.id.configactivity_spinnercategory);
        spinnerActiveDatabase =findViewById(R.id.configactivity_spinneractivedatabase);
        spinnerjob =findViewById(R.id.configactivity_spinnerjob);
        spinnerjobperson =findViewById(R.id.configactivity_spinnerjobperson);
        ed_Deliverer =findViewById(R.id.configactivity_Deliverer);
        tv_laststack =findViewById(R.id.configactivity_laststack);
        tv_delay =findViewById(R.id.configactivity_delay);
        ll_Stack=findViewById(R.id.configactivity_line_stack);
        btn_config =findViewById(R.id.configactivity_btn);
        ed_titlesize = findViewById(R.id.config_titlesize);
        sm_arabictext = findViewById(R.id.config_arabictext);
        ImageView img_logo = findViewById(R.id.configactivity_logo);

        Glide.with(img_logo)
                .asBitmap()
                .load(callMethod.ReadString("ServerURLUse")+"SlideImage/logo.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("test","Failed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("test","Ready");
                        imageInfo.SaveLogo(resource);
                        return false;
                    }
                })
                .into(img_logo);



    }

    public void init() {
        GetDataIsPersian();
        ed_Deliverer.setText(callMethod.ReadString("Deliverer"));
        tv_laststack.setText(callMethod.ReadString("StackCategory"));
        tv_delay.setText(callMethod.ReadString("Delay"));

        ed_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        sm_arabictext.setChecked(callMethod.ReadBoolan("ArabicText"));
        btn_config.setOnClickListener(v -> {
            callMethod.EditString("Deliverer",ed_Deliverer.getText().toString());
            callMethod.EditString("Delay",tv_delay.getText().toString());

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


        ArrayAdapter<String> ActiveDatabase_adapter = new ArrayAdapter<>(ConfigActivity.this,
                android.R.layout.simple_spinner_item, ActiveDatabase_array);
        ActiveDatabase_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActiveDatabase.setAdapter(ActiveDatabase_adapter);
        spinnerActiveDatabase.setSelection(Integer.parseInt(callMethod.ReadString("ActiveDatabase")));



        Call<RetrofitResponse> call =apiInterface.GetCustomerPath("GetStackCategory");
        call.enqueue(new Callback<RetrofitResponse>() {
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

        spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workcategory=String.valueOf(position);
                if(position==2){
                    ll_Stack.setVisibility(View.VISIBLE);
                }else {
                    ll_Stack.setVisibility(View.GONE);
                }
                GetJob("Ocr"+position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerActiveDatabase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                callMethod.EditString("ActiveDatabase",String.valueOf(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        spinnerjob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0) {
                    GetJobPerson(jobsstr.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerjobperson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0) {

                    callMethod.EditString("JobPersonRef",String.valueOf(jobpersonsref_int.get(position)));
                    ed_Deliverer.setText(jobpersonsstr.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void GetJob(String where) {
        jobsstr.clear();
        jobpersonsstr.clear();
        jobpersonsref_int.clear();
        spinnerjob.setAdapter(null);
        spinnerjobperson.setAdapter(null);

        Call<RetrofitResponse> call =apiInterface.GetJob("TestJob",where);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Job> jobs=response.body().getJobs();
                    jobsstr.add("برای انتخاب کلیک کنید");

                    for(Job job:jobs){
                        jobsstr.add(job.getTitle());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(ConfigActivity.this,
                            android.R.layout.simple_spinner_item,jobsstr );
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerjob.setAdapter(spinner_adapter);
                    spinnerjob.setSelection(0);

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });

    }

    public void GetDataIsPersian() {

        Call<RetrofitResponse> call =apiInterface.GetDataDbsetup("kowsar_info","DataIsPersian");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {

                    assert response.body() != null;
                    callMethod.EditBoolan("ArabicText", !response.body().getText().equals("1"));


                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });

    }


    public void GetJobPerson(String where) {
        Call<RetrofitResponse> call =apiInterface.GetJobPerson("TestJobPerson",where);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<JobPerson> jobPersons=response.body().getJobPersons();

                    jobpersonsstr.add("برای انتخاب کلیک کنید");
                    jobpersonsref_int.add(0);
                    for(JobPerson jobPerson:jobPersons){
                        jobpersonsstr.add(jobPerson.getName());
                        jobpersonsref_int.add(Integer.parseInt(jobPerson.getJobPersonCode()));
                    }

                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(ConfigActivity.this,
                            android.R.layout.simple_spinner_item,jobpersonsstr);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerjobperson.setAdapter(spinner_adapter);
                    spinnerjobperson.setSelection(0);

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}