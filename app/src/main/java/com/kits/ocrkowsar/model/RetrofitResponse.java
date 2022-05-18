package com.kits.ocrkowsar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class RetrofitResponse {

    @SerializedName("Goods")
    private ArrayList<Good> goods;
    @SerializedName("Activations")
    private ArrayList<Activation> Activations;

    @SerializedName("Factors")
    private ArrayList<Factor> Factors;
    @SerializedName("Activation")
    private Activation activation;

    @SerializedName("Good")
    private Good good;
    @SerializedName("Factor")
    private Factor Factor;
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrMessage")
    private String ErrMessage;

    private String message;
    private String path;
    @SerializedName("Text")
    private String Text;


    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public void setGood(Good good) {
        this.good = good;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Good> getGoods() {
        return goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.goods = goods;
    }

    public ArrayList<Factor> getFactors() {

        return Factors;
    }

    public void setFactors(ArrayList<Factor> factors) {
        Factors = factors;
    }

    public Good getGood() {
        return good;
    }

    public Factor getFactor() {
        return Factor;
    }

    public void setFactor(Factor factor) {
        Factor = factor;
    }

    public ArrayList<Activation> getActivations() {
        return Activations;
    }

    public void setActivations(ArrayList<Activation> activations) {
        Activations = activations;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }
}
