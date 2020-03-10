package com.changgou.search.dao;

import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//下面注解可以不加
@Repository
public interface SskuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {

}

