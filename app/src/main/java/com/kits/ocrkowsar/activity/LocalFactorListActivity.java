package com.kits.ocrkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.adapter.LocalFactorList_Adapter;
import com.kits.ocrkowsar.application.CallMethod;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.NumberFunctions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;


public class LocalFactorListActivity extends AppCompatActivity {
    private DatabaseHelper dbh;
    LocalFactorList_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView factor_header_recycler;
    private EditText edtsearch;
    Handler handler;
    ArrayList<Factor> factors = new ArrayList<>();
    String IsSent, signature = "1", srch = "";
    TextView textView_Count;
    int width = 1;


    FloatingActionButton fab;
    public ArrayList<String[]> Multi_sign = new ArrayList<>();
    public ArrayList<String> Multi_barcode = new ArrayList<>();
    Menu item_multi;
    Intent intent;
    CallMethod callMethod;
    Toolbar toolbar;
    SwitchMaterial mySwitch_activestack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_factor_list);

        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }

    }
    ////////////////////////////////////////////////////////////////////////////

    public void intent() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        IsSent = bundle.getString("IsSent");
        signature = bundle.getString("signature");

    }

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

        factor_header_recycler = findViewById(R.id.factor_headerActivity_recyclerView);
        fab = findViewById(R.id.factor_headerActivity_fab);
        //fab.setVisibility(View.VISIBLE);

        textView_Count = findViewById(R.id.factorheaderActivity_count);
        toolbar = findViewById(R.id.factor_headerActivity_toolbar);
        edtsearch = findViewById(R.id.factorheaderActivity_edtsearch);
        mySwitch_activestack = findViewById(R.id.factorheaderActivityswitch);

        setSupportActionBar(toolbar);
        handler = new Handler();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

    }

    public void init(){

        srch = callMethod.ReadString("Last_search");

        factors = dbh.factorscan(IsSent, srch, signature);

        edtsearch.setText(callMethod.ReadString("Last_search"));

        fab.setOnClickListener(v -> {
            for (String[] s : Multi_sign) {
                Multi_barcode.add(s[0]);
            }
            intent = new Intent(this, PaintActivity.class);
            intent.putExtra("ScanResponse", "Multi_sign");
            intent.putExtra("FactorImage", "hasimage");
            intent.putExtra("Width", String.valueOf(width));
            intent.putStringArrayListExtra("list", Multi_barcode);

            startActivity(intent);
            finish();
        });


        mySwitch_activestack.setOnCheckedChangeListener((compoundButton, b) -> {
            //grid
            if (b) {
                signature = "1";
                mySwitch_activestack.setText("بدون امضا");

            } else {
                signature = "0";
                mySwitch_activestack.setText("همه");

            }
            factors = dbh.factorscan(IsSent, srch, signature);
            adapter = new LocalFactorList_Adapter(factors, this, width);
            if (adapter.getItemCount() == 0) {
                callMethod.showToast("فاکتوری یافت نشد");
            }
            textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(adapter.getItemCount())));
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
            factor_header_recycler.setLayoutManager(gridLayoutManager);
            factor_header_recycler.setAdapter(adapter);
            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());
        });


        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {

                            srch = NumberFunctions.EnglishNumber(dbh.GetRegionText(editable.toString()));
                            srch=srch.replace(" ","%");
                            callMethod.EditString("Last_search", srch);
                            factors = dbh.factorscan(IsSent, srch, signature);

                            adapter = new LocalFactorList_Adapter(factors, LocalFactorListActivity.this, width);
                            if (adapter.getItemCount() == 0) {
                                callMethod.showToast("فاکتوری یافت نشد");
                            }
                            textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(adapter.getItemCount())));
                            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
                            factor_header_recycler.setLayoutManager(gridLayoutManager);
                            factor_header_recycler.setAdapter(adapter);
                            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());


                        }, 1000);

                        handler.postDelayed(() -> edtsearch.selectAll(), 5000);
                    }
                });


        adapter = new LocalFactorList_Adapter(factors, this, width);
        if (adapter.getItemCount() == 0) {
            callMethod.showToast("فاکتوری یافت نشد");
        }
        textView_Count.setText(NumberFunctions.PerisanNumber(String.valueOf(adapter.getItemCount())));
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
        factor_header_recycler.setLayoutManager(gridLayoutManager);
        factor_header_recycler.setAdapter(adapter);
        factor_header_recycler.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;
        getMenuInflater().inflate(R.menu.options_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Factor factor : factors) {
                factor.setCheck(false);
            }
            Multi_sign.clear();
            adapter.multi_select = false;

            adapter = new LocalFactorList_Adapter(factors, this, width);
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);//grid
            factor_header_recycler.setLayoutManager(gridLayoutManager);
            factor_header_recycler.setAdapter(adapter);
            factor_header_recycler.setItemAnimator(new DefaultItemAnimator());
            fab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void factor_select_function(String Factor_barcode, String Customer_code, int flag) {
        if (flag == 1) {
            fab.setVisibility(View.VISIBLE);
            Multi_sign.add(new String[]{Factor_barcode, Customer_code, ""});
            item_multi.findItem(R.id.menu_multi).setVisible(true);

        } else {
            int b = 0, c = 0;
            for (String[] s : Multi_sign) {

                if (s[0].equals(Customer_code)) b = c;
                c++;

            }
            Multi_sign.remove(b);
            if (Multi_sign.size() < 1) {
                fab.setVisibility(View.GONE);
                adapter.multi_select = false;
                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        intent = new Intent(this, LocalFactorListActivity.class);
        intent.putExtra("IsSent", IsSent);
        intent.putExtra("signature", signature);
        startActivity(intent);
        finish();

    }
}

