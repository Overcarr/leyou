package cn.leyou.cart.service;

import cn.leyou.auth.pojo.UserInfo;
import cn.leyou.cart.client.GoodClient;
import cn.leyou.cart.interceptor.LoginInterceptor;
import cn.leyou.cart.pojo.Cart;
import cn.leyou.item.pojo.Sku;
import cn.leyou.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private GoodClient goodClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    static final String KEY_PREFIX = "leyou:cart:uid:";

    static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public void saveCare(Cart cart){

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        // 查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo){
            String s = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(s, Cart.class);
            Integer num1 = cart.getNum();
            num1 = num1 + num;
            cart.setNum(num1);
        }else {
            cart.setUserId(userInfo.getId());
            Sku sku = goodClient.selectSkuById(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
        }
        hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

    }

    public List<Cart> selectAll() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX +userInfo.getId();
        if (!stringRedisTemplate.hasKey(key)){
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        if (CollectionUtils.isEmpty(carts)){
            return null;
        }
        return carts.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
    }


    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX +userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);
        String s = hashOps.get(cart.getSkuId().toString()).toString();
        Cart parse = JsonUtils.parse(s, Cart.class);
        parse.setNum(cart.getNum());
        hashOps.put(parse.getSkuId().toString(),JsonUtils.serialize(parse));
    }

    public void delect(String skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX +userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }

    public void saveLocal(List<Cart> carts) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX +userInfo.getId();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        for (Cart cart : carts) {
            if (hashOps.hasKey(cart.getSkuId().toString())){
                String s = hashOps.get(cart.getSkuId().toString()).toString();
                cart = JsonUtils.parse(s, Cart.class);
                Integer num1 = cart.getNum();
                num1 = num1 + cart.getNum();
                cart.setNum(num1);
            }else {
                cart.setUserId(userInfo.getId());
                Sku sku = goodClient.selectSkuById(cart.getSkuId());
                cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
                cart.setPrice(sku.getPrice());
                cart.setTitle(sku.getTitle());
            }
            hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
        }
    }
}
