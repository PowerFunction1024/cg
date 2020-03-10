package com.changgou.seckill.task;

import com.changgou.entity.IdWorker;
import com.changgou.exception.BaseExceptionHandler;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadingCreateOrder {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;



    /***
     * 多线程下单操作
     */
    @Async
    public void createOrder(){


        try {
        Thread.sleep(10000);

    /*        //时间区间
            String time = "2019102314";
            //用户登录名
            String username="szitheima";
            //用户抢购商品
            Long id = 1131815633483337728L;
    */
            //从队列中获取排队信息
            SeckillStatus seckillStatus  = (SeckillStatus)redisTemplate.boundListOps("SeckillOrderQueue").rightPop();


                //从队列中获取一个商品
                Object sgood = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
                if(sgood==null){
                    //清理当前用户的排队信息
                    clearQueue(seckillStatus);
                    return;
                }








                if (seckillStatus !=null){
                String time =seckillStatus .getTime();
                String username =seckillStatus .getUsername();
                Long id =seckillStatus .getGoodsId();





                SeckillGoods goods  = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id+"");

                //如果没有库存，则直接抛出异常   前面从//从队列中获取一个商品   能判断库存是否存在
             /*   if (goods==null || goods.getStockCount()<=0){
                    throw new RuntimeException("已售罄!");
                }*/
                //如果有库存，则生成订单

                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(idWorker.nextId());
                seckillOrder.setSeckillId(goods.getSkuId());
                seckillOrder.setMoney(goods.getCostPrice());
                seckillOrder.setUserId(username);
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setStatus("0");


                //抢单成功，更新抢单状态,排队->等待支付
                seckillStatus.setStatus(2);
                seckillStatus.setOrderId(seckillOrder.getId());
                seckillStatus.setMoney(Float.parseFloat(seckillOrder.getMoney()));
                redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);



                //将秒杀订单存入到Redis中
                redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);


                    //商品库存-1
                    //goods.setStockCount(goods.getStockCount()-1);
                    Long surplusCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(id+"", -1);//商品数量递减
                    goods.setStockCount(surplusCount.intValue());    //根据计数器统计

                //判断还有没库存,如果没有则同步到数据库,同时删除redis对应数据

                if (goods.getStockCount()<=0){
                    seckillGoodsMapper.updateByPrimaryKeySelective(goods);
                    redisTemplate.boundHashOps("SeckillGoods_" + time).delete(id+"");

                }else {
                    //如果有库存  则直数据重置到Reids中
                    redisTemplate.boundHashOps("SeckillGoods_" + time).put(id+"", goods);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /***
     * 清理用户排队信息
     * @param seckillStatus
     */
    public void clearQueue(SeckillStatus seckillStatus){
        //清理排队标示
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());

        //清理抢单标示
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
    }
}