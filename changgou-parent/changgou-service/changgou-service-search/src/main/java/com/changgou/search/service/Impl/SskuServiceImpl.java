package com.changgou.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SskuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchResultMapperImpl;
import com.changgou.search.service.SskuService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SskuServiceImpl implements SskuService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SskuEsMapper skuEsMapper;

    /**
     * 导入sku数据到es
     */
    @Override
    public void importSku() {
        //调用changgou-service-goods微服务
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        //将数据转成search.Sku
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(result.getData()), SkuInfo.class);

        // Map<String, Object> specMap = skuInfo.getSpecMap();//空的值
        for (SkuInfo skuInfo : skuInfos) {
            Map<String,Object> map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            //{"手机屏幕尺寸":"5.5寸","网络":"联通2G","颜色":"紫","测试":"实施","机身内存":"32G","存储":"16G","像素":"300万像素"}
            skuInfo.setSpecMap(map);//map有值
        }

        skuEsMapper.saveAll(skuInfos);

    }

@Autowired
private ElasticsearchTemplate esTemplate;
    @Override
    public Map search(Map<String, String> searchMap) {

        //1.获取关键字的值
        String keywords = searchMap.get("keywords");
        if (StringUtils.isEmpty(keywords)){
            keywords="华为";//默认值
        }

        //2.创建查询对象 的构建对象

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //3.设置查询的条件

        //3.设置分组条件  商品分类
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));
        //3.1品牌的分类
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(50));
        //3.2设置分组条件  商品的规格
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(100));
        //设置主关键字查询
        //nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords ));
        //设置高亮条件
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));
        //设置主关键字查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"name","brandName","categoryName"));


        //添加多条件组合查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        if (!StringUtils.isEmpty(searchMap.get("brand"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brand"))); //品牌条件
        }
        if (!StringUtils.isEmpty(searchMap.get("categoryName"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("categoryName")));//分类条件
        }
        //规格过滤查询
        if (searchMap!=null){
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")){
                    boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",searchMap.get(key)));
                }
            }


        }

        //价格过滤查询
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            if (split.length==2) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            } else if(split.length==1){
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }
        }
        //构建过滤查询
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);



        //构建分页查询
        Integer pageNum=1;

        if (!StringUtils.isEmpty(searchMap.get("pageNum"))) {

            try {
                pageNum = Integer.valueOf(searchMap.get("pageNum"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        Integer pageSize=10;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,pageSize ));


        //构建排序查询
        String sortRule = searchMap.get("sortRule");
        String sortField = searchMap.get("sortField");

        if (!StringUtils.isEmpty(sortRule)&&!StringUtils.isEmpty(sortField)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equals("DESC")? SortOrder.DESC:SortOrder.ASC));
        }


        //4.构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();



        //5.执行查询
        //AggregatedPage<SkuInfo> skuPage = esTemplate.queryForPage(query, SkuInfo.class);
        AggregatedPage<SkuInfo> skuPage = esTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapperImpl());

        //获取  商品分类  分组结果
        StringTerms stringTermsCategory = (StringTerms) skuPage.getAggregation("skuCategorygroup");
        List<String> categoryList =getStringsCategoryList(stringTermsCategory);

        //5.1 获取  品牌的分类  分组结果
        StringTerms skuBrandgroup = (StringTerms) skuPage.getAggregation("skuBrandgroup");
        List<String> brandList = getStringsCategoryList(skuBrandgroup);

        //5.2 获取  商品规格数据  分组结果
        StringTerms stringTermsSpec  = (StringTerms) skuPage.getAggregation("skuSpecgroup");

        HashMap<String, Set> specMap = getStringSetHashMap(stringTermsSpec);


        //6.返回结果

        HashMap<String,Object> hm = new HashMap();

        hm.put("rows", skuPage.getContent());//内容
        hm.put("total", skuPage.getTotalElements()); //总条数
        hm.put("totalPages", skuPage.getTotalPages());//总页数
        hm.put("categoryList", categoryList);//分类分组结果
        hm.put("brandList", brandList);//品牌分组结果
        hm.put("specMap", specMap);//规格分组结果

        //分页数据保存
        //设置当前页码
        hm.put("pageNum", pageNum);
        hm.put("pageSize", 10);

        return hm;


    }




    /**
     * 获取分类列表数据
     *
     * 获取分类列表数据(也在这里获取)
     *
     * @param stringTerms
     * @return
     */
    private List<String> getStringsCategoryList(StringTerms stringTerms) {
        List<String> categoryList = new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//分组的值
                categoryList.add(keyAsString);
            }
        }
        return categoryList;
    }

    /**
     * 获取规格 数据
     *
     * @param
     * @return
     */
    private HashMap<String, Set> getStringSetHashMap(StringTerms stringTermsSpec) {
        HashMap<String,Set> specMap = new HashMap();
        HashSet<String> specList = new HashSet();
        if (stringTermsSpec!=null){
            List<StringTerms.Bucket> buckets = stringTermsSpec.getBuckets();

            for (StringTerms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();
                specList.add(keyAsString);
            }
        }

        for (String specjson : specList) {

            Map<String,String> map = JSON.parseObject(specjson, Map.class);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> specValue = specMap.get(key);

                if (specValue==null){
                    specValue = new HashSet<String>();
                }

                specValue.add(value);
                specMap.put(key, specValue);

            }

        }
        return specMap;
    }
}
