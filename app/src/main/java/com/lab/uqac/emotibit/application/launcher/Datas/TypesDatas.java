package com.lab.uqac.emotibit.application.launcher.Datas;

import java.util.HashMap;

public enum TypesDatas {

    ACCX(0, "AX"), ACCY(0, "AY"), ACCZ(0, "AZ"), GYROX(1, "GX"), GYROY(1, "GY"), GYROZ(1, "GZ"),
    MAGNX(2, "MX"), MAGNY(2, "MY"), MAGNZ(2, "MZ"), EDA(3, "EA"), HUM(4, "H0"), PPGR(5, "PR"),
    PPGIR(6, "PI"), PPGGRN(7, "PG"),TEMP(8, "T0"), THERM(9, "TH"), TL(99, "TL"), BAT(99, "B%"),
    RD(99, "RD"), HE(99, "HE"), RB(99, "RB"), RE(99, "RE"), TU(99, "TU"), MH(99, "MH"),
    UN(99, "UN"), AK(99, "AK");

    private int mSelectedGraph;
    private String mTag;

    TypesDatas(int selectedGraph, String tag) {

        mSelectedGraph = selectedGraph;
        mTag = tag;
    }

    public int getmSelectedGraph() {
        return mSelectedGraph;
    }

    public void setmSelectedGraph(int mSelectedGraph) {
        this.mSelectedGraph = mSelectedGraph;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }


    public static HashMap<String, TypesDatas> mapString(){

        HashMap<String, TypesDatas> map = new HashMap<String, TypesDatas>();

        for(TypesDatas typesDatas : TypesDatas.values()){

                map.put(typesDatas.mTag, typesDatas);
        }

        return map;
    }
}
