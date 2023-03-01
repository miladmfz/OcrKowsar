package com.kits.ocrkowsar.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.ocrkowsar.R;
import com.kits.ocrkowsar.model.AppPrinter;
import com.kits.ocrkowsar.model.DatabaseHelper;
import com.kits.ocrkowsar.model.Factor;
import com.kits.ocrkowsar.model.RetrofitResponse;
import com.kits.ocrkowsar.webService.APIClient;
import com.kits.ocrkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Print {


    private final Context mContext;
    public APIInterface apiInterface;
    public Call<RetrofitResponse> call;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Intent intent;
    Integer il;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    int printerconter ;
    ArrayList<Factor> Factor_header = new ArrayList<>();
    ArrayList<Factor> Factor_row = new ArrayList<>();
    ArrayList<AppPrinter> AppPrinters ;
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    String Filter_print = "";
    TextView tv_rep;

    public Print(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        printerconter = 0;

    }

    public void dialogProg() {
        dialogProg.setContentView(R.layout.rep_prog);
        tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
        dialogProg.show();

    }



    public void GetAppPrinterList() {
        dialogProg();
        call = apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printerconter = 0;
                    AppPrinters = response.body().getAppPrinters();
                    GetData();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                GetAppPrinterList();

            }
        });
    }



    @SuppressLint("RtlHardcoded")
    public void printDialogView() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        dialogprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogprint.setContentView(R.layout.print_layout_view);
        main_layout = dialogprint.findViewById(R.id.print_layout_view_ll);
        CreateView();

    }


    public void GetData() {

        if (printerconter < (AppPrinters.size())){
            //Todo print
        }else {
            //Todo finish
            dialogProg.dismiss();
        }
    }
    public void CreateView() {
        //Todo print


        bitmap_factor = loadBitmapFromView(main_layout);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);


        Call<RetrofitResponse> call = apiInterface.OcrSendImage("OcrSendImage",
                bitmap_factor_base64,
                "1",
                AppPrinters.get(printerconter).getPrinterName(),
                AppPrinters.get(printerconter).getPrintCount()

        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                if (response.body().getText().equals("Done")) {
                    printerconter++;
                    GetData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                printerconter++;
                GetData();
            }
        });


    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


}
