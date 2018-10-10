package com.sucetech.yijiamei.bean;

import java.time.LocalDateTime;


public class RecycleMaterial {


    public Integer id;

    /**
     * 医疗机构Id
     */
    public Integer medicalId;

    /**
     * 回收执照编号
     */
    public String licenseNumber;

    /**
     * 回收执照图片(这个在提交的时候可以不传)
     */
    public String licenseImages;

    /**
     * 回收人员Id(移动端传登录后的用户Id)
     */
    private Integer recycleUserId;

    /**
     * 回收时间,可以不传
     */
    public LocalDateTime dateTime;

    /**
     * 单价
     */
    public Float price;

    /**
     * 自检
     */
    public String selfCheck;

    /**
     * 描述
     */
    public String description;

    /**
     * 状态,可以不传
     */
    private Byte status;

    /**
     * 打包人员
     */
    public String packager;
    /**
     * 录音信息(相对路径,可以不传,后台会对上传的音频文件保存处理,)
     */
    public String audio;


}