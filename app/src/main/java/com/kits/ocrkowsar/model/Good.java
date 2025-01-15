package com.kits.ocrkowsar.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Good implements Serializable {


    @SerializedName("StackLocation")
    private String StackLocation;
    @SerializedName("Isbn")
    private String Isbn;
    @SerializedName("MinAmount")
    private String MinAmount;
    @SerializedName("GoodName")
    private String GoodName;

    @SerializedName("FormNo")
    private String FormNo;



    @SerializedName("GoodCode")
    private String GoodCode;

    @SerializedName("Price")
    private String Price;

    @SerializedName("TotalAvailable")
    private String TotalAvailable;

    @SerializedName("size")
    private String size;

    @SerializedName("CoverType")
    private String CoverType;

    @SerializedName("PageNo")
    private String PageNo;

    @SerializedName("FacAmount")
    private String FacAmount;

    @SerializedName("GoodExplain4")
    private String GoodExplain4;

    @SerializedName("GoodMaxSellPrice")
    private String GoodMaxSellPrice;

    @SerializedName("FactorRowCode")
    private String FactorRowCode;

    @SerializedName("AppRowIsPacked")
    private String AppRowIsPacked;

    @SerializedName("AppRowIsControled")
    private String AppRowIsControled;

    @SerializedName("ShortageAmount")
    private Integer ShortageAmount;

    @SerializedName("AppOCRFactorRowCode")
    private String AppOCRFactorRowCode;

    @SerializedName("SumPrice")
    private String SumPrice;


    @SerializedName("CachedBarCode")
    private String CachedBarCode;
    @SerializedName("GoodImageName")
    private String GoodImageName;
    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrMessage")
    private String ErrMessage;







    @SerializedName("StackAmount")
    private String StackAmount;

    @SerializedName("MaxSellPrice")
    private String MaxSellPrice;
    @SerializedName("GoodExplain2")
    private String GoodExplain2;


    public String getGoodExplain2() {
        return GoodExplain2;
    }

    public void setGoodExplain2(String goodExplain2) {
        GoodExplain2 = goodExplain2;
    }

    public String getMaxSellPrice() {
        return MaxSellPrice;
    }

    public void setMaxSellPrice(String maxSellPrice) {
        MaxSellPrice = maxSellPrice;
    }

    public String getStackAmount() {
        return StackAmount;
    }

    public void setStackAmount(String stackAmount) {
        StackAmount = stackAmount;
    }

    public String getIsbn() {
        return Isbn;
    }

    public void setIsbn(String isbn) {
        Isbn = isbn;
    }

    public String getMinAmount() {
        return MinAmount;
    }

    public void setMinAmount(String minAmount) {
        MinAmount = minAmount;
    }

    public String getStackLocation() {
        return StackLocation;
    }

    public void setStackLocation(String stackLocation) {
        StackLocation = stackLocation;
    }

    public String getGoodImageName() {
        return GoodImageName;
    }


    public void setGoodImageName(String goodImageName) {
        GoodImageName = goodImageName;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getErrMessage() {
        return ErrMessage;
    }

    public void setErrMessage(String errMessage) {
        ErrMessage = errMessage;
    }

    public String getCachedBarCode() {
        return CachedBarCode;
    }

    public void setCachedBarCode(String cachedBarCode) {
        CachedBarCode = cachedBarCode;
    }

    public String getGoodExplain4() { return GoodExplain4; }

    public void setGoodExplain4(String goodExplain4) { GoodExplain4 = goodExplain4; }

    public Integer getShortageAmount() {
        return ShortageAmount;
    }

    public void setShortageAmount(Integer shortageAmount) {
        ShortageAmount = shortageAmount;
    }

    public String getTotalAvailable() {
        return TotalAvailable;
    }

    public void setTotalAvailable(String totalAvailable) {
        TotalAvailable = totalAvailable;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCoverType() {
        return CoverType;
    }

    public void setCoverType(String coverType) {
        CoverType = coverType;
    }

    public String getPageNo() {
        return PageNo;
    }

    public void setPageNo(String pageNo) {
        PageNo = pageNo;
    }

    public String getAppOCRFactorRowCode() {
        return AppOCRFactorRowCode;
    }

    public void setAppOCRFactorRowCode(String appOCRFactorRowCode) { AppOCRFactorRowCode = appOCRFactorRowCode;}

    public String getAppRowIsPacked() {
        return AppRowIsPacked;
    }

    public void setAppRowIsPacked(String appRowIsPacked) {
        AppRowIsPacked = appRowIsPacked;
    }

    public String getAppRowIsControled() {
        return AppRowIsControled;
    }

    public void setAppRowIsControled(String appRowIsControled) { AppRowIsControled = appRowIsControled;}

    public String getGoodMaxSellPrice() { if (GoodMaxSellPrice != null) {return GoodMaxSellPrice; }else {return "";}}

    public void setGoodMaxSellPrice(String goodMaxSellPrice) {GoodMaxSellPrice = goodMaxSellPrice;}

    public String getFactorRowCode() {
        return FactorRowCode;
    }

    public void setFactorRowCode(String factorRowCode) {
        FactorRowCode = factorRowCode;
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getGoodCode() {
        return GoodCode;
    }

    public void setGoodCode(String goodCode) {
        GoodCode = goodCode;
    }

    public String getGoodName() {if (GoodName != null){return GoodName; }else {return "";} }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getPrice() {if (Price != null) {return Price;}else {return "";}}

    public void setPrice(String price) {
        Price = price;
    }

    public String getFacAmount() {if (FacAmount != null) {return FacAmount;}else {return "";}}

    public void setFacAmount(String facAmount) {
        FacAmount = facAmount;
    }

    public String getSumPrice() { return String.valueOf(Long.parseLong(Price)* Long.parseLong(FacAmount));}

}
