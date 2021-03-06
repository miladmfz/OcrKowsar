package com.kits.ocrkowsar.webService;//package com.kits.test.webService;

import com.kits.ocrkowsar.model.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetFactor(@Field("tag") String tag
                                    , @Field("barcode") String barcode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> CheckState(@Field("tag") String tag
                                    , @Field("AppOCRCode") String AppOCRCode
                                    , @Field("State") String State
                                    , @Field("Deliverer") String Deliverer);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GoodShortage(@Field("tag") String tag
                                    , @Field("OCRFactorRowCode") String OCRFactorRowCode
                                    , @Field("Shortage") String Shortage);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetOcrFactorList(@Field("tag") String tag
                                    , @Field("State") String State
                                    , @Field("SearchTarget") String SearchTarget);




    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> SetPackDetail(@Field("tag") String tag
            , @Field("OcrFactorCode") String OcrFactorCode
            , @Field("Reader") String Reader
            , @Field("Controler") String Controler
            , @Field("Packer") String Packer
            , @Field("PackDeliverDate") String PackDeliverDate
            , @Field("PackCount") String PackCount);




    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodDetail(@Field("tag") String tag,
                                  @Field("GoodCode") String GoodCode);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetCustomerPath(@Field("tag") String tag);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> test(@Field("tag") String test);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> SendImage(@Field("tag") String tag
            , @Field("barcode") String barcode
            , @Field("image") String image);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> Kowsar_log(@Field("tag") String tag
            , @Field("Device_Id") String Device_Id
            , @Field("Address_Ip") String Address_Ip
            , @Field("Server_Name") String Server_Name
            , @Field("Factor_Code") String Factor_Code
            , @Field("StrDate") String StrDate
            , @Field("Broker") String Broker
            , @Field("Explain") String Explain);

    @FormUrlEncoded
    @POST("index.php")
    Call<String> getImageData(@Field("tag") String tag,
                              @Field("image") String image,
                              @Field("barcode") String barcode
    );

    @POST("index.php")
    @FormUrlEncoded
    Call <RetrofitResponse> GetImage(@Field("tag") String tag,
                           @Field("GoodCode") String GoodCode,
                           @Field("IX") Integer IX,
                           @Field("Scale") Integer Scale);

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Activation(
            @Field("tag")             String tag
            , @Field("ActivationCode")  String ActivationCode
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Errorlog(
            @Field("tag")         String tag
            , @Field("ErrorLog")    String ErrorLog
            , @Field("Broker")      String Broker
            , @Field("DeviceId")    String DeviceId
            , @Field("ServerName")  String ServerName
            , @Field("StrDate")     String StrDate
            , @Field("VersionName") String VersionName
    );


}

