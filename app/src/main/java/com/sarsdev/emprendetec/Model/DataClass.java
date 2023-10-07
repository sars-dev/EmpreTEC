package com.sarsdev.emprendetec.Model;

public class DataClass {

    private String dataTitle;
    private String dataDesc;
    private String dataArea;
    private String dataArea2;
    private String dataImage;
    private String key;

    public DataClass(String dataTitle, String dataDesc, String dataArea, String dataArea2, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataArea = dataArea;
        this.dataArea2 = dataArea2;
        this.dataImage = dataImage;
    }

    public String getDataArea2() {
        return dataArea2;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataArea() {
        return dataArea;
    }

    public String getDataImage() {
        return dataImage;
    }

    public DataClass() {

    }

}
