package com.sucetech.yijiamei.bean;


public class RecycleMaterialDetails {

    /**
     * 自增Id
     */
    public Integer id;

    /**
     * 回收Id 可以不传
     */
    public Integer refId;

    /**
     * 物料类型
     */
    public int type;

    /**
     * 重量
     */
    public Float weight;
    public String price;

    /**
     * 体积
     */
    public String volume;

    /**
     * 流程Id可以不传
     */
    public Byte flowType;
    public float multiple;

}