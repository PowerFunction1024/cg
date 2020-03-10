package com.changgou.search.service;

import java.util.Map;

public interface SskuService {

    /***
     * 导入SKU数据
     */
    void importSku();

    /***
     * 搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String, String> searchMap);
}