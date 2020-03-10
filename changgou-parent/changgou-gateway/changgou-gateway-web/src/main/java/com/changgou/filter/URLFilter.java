package com.changgou.filter;

public class URLFilter {

    /**
     * 要放行的路径
     */
    private static final String noAuthorizeurls = "/api/user/add,/api/user/login";

    public static boolean hasAuthorize(String uri){
        String[] split = noAuthorizeurls.split(",");
        for (String u : split) {
            if (u.equalsIgnoreCase(uri)){
                return true;
            }
        }
        return false;
    }
}
