package com.changgou.goods.controller;

public enum ResponseCodeEnum {

    ERROR("001","输入参数出错"),
    SUCCESS("002","调用成功"),
    UNKNOW("003","未知的出错类型");
    private String code;
    private String msg;

    ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    //根据枚举的code获取msg的方法
    public static String getMsgByCode(String code){
        for (ResponseCodeEnum responseCodeEnum : ResponseCodeEnum.values()) {
            if (responseCodeEnum.getCode().equals(code)){
                return responseCodeEnum.getMsg();
            }
        }
        return null;
    }



}
