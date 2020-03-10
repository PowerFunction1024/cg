package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.changgou.item.feign.PageFeign;
import com.netflix.discovery.converters.Auto;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {

  /*  *//***
     * 增加数据监听
     * @param eventType
     * @param rowData
     *//*
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    *//***
     * 修改数据监听
     * @param rowData
     *//*
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData) {
        System.out.println("UpdateListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    *//***
     * 删除数据监听
     * @param eventType
     *//*
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType) {
        System.out.println("DeleteListenPoint");
    }
*/
    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private PageFeign pageFeign;

    //字符串
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @ListenPoint(destination = "example",
                schema = "changgou_content",
            table = {"tb_content_category", "tb_content"}, eventType = {
            CanalEntry.EventType.UPDATE,
            CanalEntry.EventType.DELETE,
            CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("DeleteListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));

        //获取改变的分类id
        String category_id = getColumnValue(eventType, rowData);

        //根据分类的id获取数据库的数据
        Result<List<Content>> contentList = contentFeign.findByCategory(Long.valueOf(category_id));
        List<Content> data = contentList.getData();

        //将数据更新到redis
        stringRedisTemplate.boundValueOps("content_" + category_id).set(JSON.toJSONString(data));


        }


    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String category_id="";
        //判断 如果是删除  则获取beforlist
        if (eventType.equals(CanalEntry.EventType.DELETE)){
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("category_id")){
                     category_id=column.getValue();
                }
            }
        //判断 如果是添加 或者是更新 获取afterlist
        }else {
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("category_id")){
                    category_id=column.getValue();
                }
            }
        }

        return category_id;
    }



    @ListenPoint(destination = "example",
            schema = "changgou_goods",
            table = {"tb_spu"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        //判断操作类型
        if (eventType == CanalEntry.EventType.DELETE) {
            String spuId = "";
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();//spuid
                    break;
                }
            }
            //todo 删除静态页

        }else{
            //新增 或者 更新
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            String spuId = "";
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();
                    break;
                }
            }
            //更新 生成静态页
            pageFeign.createHtml(Long.valueOf(spuId));
        }
    }




}





















