package com.kits.ocrkowsar.model;

import com.google.gson.annotations.SerializedName;

public class Factor {

    @SerializedName("FactorBarcode")
    private String FactorBarcode;
    @SerializedName("FactorPrivateCode")
    private String FactorPrivateCode;

    @SerializedName("FactorCode")
    private String FactorCode;
    @SerializedName("ScanDate")
    private String ScanDate;
    @SerializedName("FactorDate")
    private String FactorDate;
    @SerializedName("FactorExplain")
    private String FactorExplain;
    @SerializedName("CustName")
    private String CustName;
    @SerializedName("CustomerCode")
    private String CustomerCode;
    @SerializedName("CustomerRef")
    private String CustomerRef;
    @SerializedName("SumPrice")
    private String SumPrice;
    @SerializedName("NewSumPrice")
    private String NewSumPrice;
    @SerializedName("SumAmount")
    private String SumAmount;
    @SerializedName("RowCount")
    private String RowCount;
    @SerializedName("IsSent")
    private String IsSent;
    @SerializedName("Deliverer")
    private String Deliverer;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("SignatureImage")
    private String SignatureImage;
    @SerializedName("CameraImage")
    private String CameraImage;
    @SerializedName("FactorImage")
    private String FactorImage;
    @SerializedName("AppIsControled")
    private String AppIsControled;
    @SerializedName("AppOCRFactorCode")
    private String AppOCRFactorCode;

    @SerializedName("AppIsPacked")
    private String AppIsPacked;

    @SerializedName("AppIsDelivered")
    private String AppIsDelivered;

    @SerializedName("AppTcPrintRef")
    private String AppTcPrintRef;

    @SerializedName("CustomerPath")
    private String CustomerPath;

    @SerializedName("IsEdited")
    private String IsEdited;

    @SerializedName("HasShortage")
    private String HasShortage;

    @SerializedName("HasSignature")
    private String HasSignature;

    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrMessage")
    private String ErrMessage;

    @SerializedName("Check")
    private boolean Check;


    public String getCustomerPath() {
        return CustomerPath;
    }

    public void setCustomerPath(String customerPath) {
        CustomerPath = customerPath;
    }

    public String getIsEdited() {
        return IsEdited;
    }

    public void setIsEdited(String isEdited) {
        IsEdited = isEdited;
    }

    public String getHasShortage() {
        return HasShortage;
    }

    public void setHasShortage(String hasShortage) {
        HasShortage = hasShortage;
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

    public String getAppIsControled() {
        return AppIsControled;
    }

    public void setAppIsControled(String appIsControled) {
        AppIsControled = appIsControled;
    }

    public String getAppIsPacked() {
        return AppIsPacked;
    }

    public void setAppIsPacked(String appIsPacked) {
        AppIsPacked = appIsPacked;
    }

    public String getAppIsDelivered() {
        return AppIsDelivered;
    }

    public void setAppIsDelivered(String appIsDelivered) {
        AppIsDelivered = appIsDelivered;
    }

    public String getAppTcPrintRef() {
        return AppTcPrintRef;
    }

    public void setAppTcPrintRef(String appTcPrintRef) {
        AppTcPrintRef = appTcPrintRef;
    }

    public String getAppOCRFactorCode()
    {
        return AppOCRFactorCode;
    }

    public void setAppOCRFactorCode(String appOCRFactorCode) {
        AppOCRFactorCode = appOCRFactorCode;
    }


    public String getCustomerRef() {
        return CustomerRef;
    }

    public void setCustomerRef(String customerRef) {
        CustomerRef = customerRef;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public String getCameraImage() {
        return CameraImage;
    }

    public void setCameraImage(String cameraImage) {
        CameraImage = cameraImage;
    }

    public String getFactorImage() {
        return FactorImage;
    }

    public void setFactorImage(String factorImage) {
        FactorImage = factorImage;
    }

    public String getFactorBarcode() {
        if (FactorBarcode != null)
        {
            return FactorBarcode;
        }else {
            return "";
        }
    }

    public boolean isCheck() {
        return Check;
    }

    public void setCheck(boolean check) {
        Check = check;
    }

    public void setFactorBarcode(String factorBarcode) {
        FactorBarcode = factorBarcode;
    }

    public String getFactorPrivateCode() {
        if (FactorPrivateCode != null)
        {
            return FactorPrivateCode;
        }else {
            return "";
        }
    }

    public void setFactorPrivateCode(String factorPrivateCode) {
        FactorPrivateCode = factorPrivateCode;
    }

    public String getFactorCode() {
        if (FactorCode != null)
        {
            return FactorCode;
        }else {
            return "";
        }
    }

    public void setFactorCode(String factorCode) {
        FactorCode = factorCode;
    }

    public String getScanDate() {
        if (ScanDate != null)
        {
            return ScanDate;
        }else {
            return "";
        }
    }

    public void setScanDate(String scanDate) {
        ScanDate = scanDate;
    }

    public String getFactorDate() {
        if (FactorDate != null)
        {
            return FactorDate;
        }else {
            return "";
        }
    }

    public void setFactorDate(String factorDate) {
        FactorDate = factorDate;
    }

    public String getFactorExplain() {
        if (FactorExplain != null)
        {
            return FactorExplain;
        }else {
            return "";
        }
    }

    public void setFactorExplain(String factorExplain) {
        FactorExplain = factorExplain;
    }

    public String getCustName() {
        if (CustName != null)
        {
            return CustName;
        }else {
            return "";
        }
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getSumPrice() {
        if (SumPrice != null)
        {
            return SumPrice;
        }else {
            return "";
        }
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getNewSumPrice() {
        if (NewSumPrice != null)
        {
            return NewSumPrice;
        }else {
            return "";
        }
    }

    public void setNewSumPrice(String newSumPrice) {
        NewSumPrice = newSumPrice;
    }

    public String getSumAmount() {
        if (SumAmount != null)
        {
            return SumAmount;
        }else {
            return "";
        }
    }

    public String getHasSignature() {
        return HasSignature;
    }

    public void setHasSignature(String hasSignature) {
        HasSignature = hasSignature;
    }

    public void setSumAmount(String sumAmount) {
        SumAmount = sumAmount;
    }

    public String getRowCount() {
        if (RowCount != null)
        {
            return RowCount;
        }else {
            return "";
        }
    }

    public void setRowCount(String rowCount) {
        RowCount = rowCount;
    }

    public String getSignatureImage() {
        if (SignatureImage != null)
        {
            return SignatureImage;
        }else {
            return "";
        }
    }

    public void setSignatureImage(String signatureImage) {
        SignatureImage = signatureImage;
    }

    public String getIsSent() {
        if (IsSent != null)
        {
            return IsSent;
        }else {
            return "";
        }
    }

    public void setIsSent(String isSent) {
        IsSent = isSent;
    }

    public String getDeliverer() {
        if (Deliverer != null)
        {
            return Deliverer;
        }else {
            return "";
        }
    }

    public void setDeliverer(String Deliverer) {
        Deliverer = Deliverer;
    }

    public String getAddress() {
        if (Address != null)
        {
            return Address;
        }else {
            return "";
        }
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        if (Phone != null)
        {
            return Phone;
        }else {
            return "";
        }
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
