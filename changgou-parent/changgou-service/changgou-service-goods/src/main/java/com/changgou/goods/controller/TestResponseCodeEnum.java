package com.changgou.goods.controller;

public class TestResponseCodeEnum {
    public static void main(String[] args) {
        System.out.println(ResponseCodeEnum.getMsgByCode("001"));
        ResponseCodeEnum[] values = ResponseCodeEnum.values();
        System.out.println(ResponseCodeEnum.ERROR.getCode());
        System.out.println(ResponseCodeEnum.ERROR.getMsg());
        System.out.println(ResponseCodeEnum.valueOf("UNKNOW"));

    }
}
