package com.changgou.content.feign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/18 13:58
 *****/
@FeignClient(name="content")
@RequestMapping("/content")
public interface ContentFeign {




    //添加根据分类ID即category_id查询广告,供给changgou-service-canal调用以更新redis
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable(name = "id") Long id);

/*    *//***
     * Content分页条件搜索实现
     * @param content
     * @param page
     * @param size
     * @return
     *//*
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) Content content, @PathVariable int page, @PathVariable int size);

    *//***
     * Content分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     *//*
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size);

    *//***
     * 多条件搜索品牌数据
     * @param content
     * @return
     *//*
    @PostMapping(value = "/search" )
    Result<List<Content>> findList(@RequestBody(required = false) Content content);

    *//***
     * 根据ID删除品牌数据
     * @param id
     * @return
     *//*
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable Long id);

    *//***
     * 修改Content数据
     * @param content
     * @param id
     * @return
     *//*
    @PutMapping(value="/{id}")
    Result update(@RequestBody Content content, @PathVariable Long id);

    *//***
     * 新增Content数据
     * @param content
     * @return
     *//*
    @PostMapping
    Result add(@RequestBody Content content);

    *//***
     * 根据ID查询Content数据
     * @param id
     * @return
     *//*
    @GetMapping("/{id}")
    Result<Content> findById(@PathVariable Long id);

    *//***
     * 查询Content全部数据
     * @return
     *//*
    @GetMapping
    Result<List<Content>> findAll();*/
}