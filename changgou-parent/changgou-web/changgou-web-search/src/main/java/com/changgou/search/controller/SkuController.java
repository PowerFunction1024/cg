package com.changgou.search.controller;

import com.changgou.entity.Page;
import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ApiListing;

import java.util.Map;

@Controller
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;
    @GetMapping("/list")
    public String search(@RequestParam(required = false) Map<String,String> searchMap, Model model){
        Map resultMap  = skuFeign.search(searchMap);
        model.addAttribute("resultMap", resultMap );
        model.addAttribute("searchMap", searchMap );


        String url=urlAdd(searchMap);
        model.addAttribute("url", url );

        //分页计算
        Page<SkuInfo> page = new Page<>(
                Long.parseLong(resultMap.get("total").toString()),//总记录数
                Integer.parseInt(resultMap.get("pageNum").toString()),//当前页
                Integer.parseInt(resultMap.get("pageSize").toString()) //每页显示条数
        );
        model.addAttribute("page",page );


        return "search";
    }

    private String urlAdd(Map<String,String> searchMap) {
        String url="/search/list";
        if (searchMap!=null){
           url+="?";
           for (Map.Entry<String, String> entry : searchMap.entrySet()) {


               String value = entry.getValue();
               String key = entry.getKey();
               //如果是排序 则 跳过 拼接排序的地址 因为有数据
               if (key.equalsIgnoreCase("sortField")||key.equalsIgnoreCase("sortRule")){

                   continue;
               }

               if (key.equalsIgnoreCase("pageNum")){

                   continue;
               }



               url+=key+"="+value+"&";
           }

           if (url.lastIndexOf("&")!=-1){
              url= url.substring(0,url.lastIndexOf("&"));
           }
       }

        return url;
    }
}
