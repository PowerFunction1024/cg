package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Integer num, Long id, String username) {
        if (num<=0){
            redisTemplate.boundHashOps("Cart_"+username).delete(id);
            return;
        }

        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();

        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();

        //将SKU转换成OrderItem
        OrderItem orderItem = sku2OrderItem(num, sku, spu);

        /******
         * 购物车数据存入到Redis
         * namespace = Cart_[username]
         * key=id(sku)
         * value=OrderItem
         */
        redisTemplate.boundHashOps("Cart_"+username).put(id,orderItem);



    }

    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> list = redisTemplate.boundHashOps("Cart_" + username).values();
        return list;
    }

    /***
     * SKU转成OrderItem
     * @param sku
     * @param num
     * @return
     */

    private OrderItem sku2OrderItem(Integer num, Sku sku, Spu spu) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());

        orderItem.setSkuId(sku.getId());
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setName(sku.getName());//商品的名称SKU的名称
        orderItem.setPrice(sku.getPrice());//SKU的单价
        orderItem.setNum(num);//购买数量
        orderItem.setMoney(num*sku.getPrice());//小计
        orderItem.setPayMoney(num*sku.getPrice());//实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(num*sku.getWeight());
        return orderItem;
    }
}
