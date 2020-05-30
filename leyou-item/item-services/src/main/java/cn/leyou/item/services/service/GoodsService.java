package cn.leyou.item.services.service;

import cn.leyou.item.pojo.*;
import cn.leyou.item.services.mapper.*;
import cn.leyou.pojo.ResultType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    public SpuMapper spuMapper;
    @Autowired
    public BrandMapper brandMapper;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public SpuDetailMapper spuDetailMapper;
    @Autowired
    public StockMapper stockMapper;
    @Autowired
    public SkuMapper skuMapper;

    @Autowired
    public AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsService.class);

    public ResultType<SpuVo> querySpuVoList(String key, Boolean saleable, int page, int rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 查询spu
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%" + key +"%");
        }
        if (saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }
        PageHelper.startPage(page,rows);
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        // 把spu封装到spuvo中去
        List<SpuVo> spuVos =new ArrayList<>();
        spus.forEach(spu -> {
            SpuVo spuVo = new SpuVo();
            BeanUtils.copyProperties(spu,spuVo);

            // 查询品牌名字
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            String brandName = brand.getName();
            spuVo.setBName(brandName);
            // 查询分类名字
            List<String> cNames = categoryService.querycNameBycIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spuVo.setCName(StringUtils.join(cNames,"/"));
            spuVos.add(spuVo);
        });
        return new ResultType<SpuVo>(pageInfo.getTotal(),spuVos);

    }

    public void saveGoods(SpuVo spuVo) {
        spuVo.setId(null);
        spuVo.setCreateTime(new Date());
        spuVo.setLastUpdateTime(spuVo.getCreateTime());
        spuVo.setSaleable(null);
        spuVo.setValid(null);
        spuMapper.insertSelective(spuVo);

        // spu_detail
        SpuDetail spuDetail = spuVo.getSpuDetail();
        spuDetail.setSpuId(spuVo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuVo);

        this.sendMsg(spuVo.getId(),"insert");
    }

    private void saveSkuAndStock(SpuVo spuVo) {
        List<Sku> skus = spuVo.getSkus();
        for (Sku sku : skus) {
            sku.setSpuId(spuVo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        }
    }

    public SpuDetail querySpuDeatils(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkus(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        for (Sku skus1 : skus) {
            Stock stock = stockMapper.selectByPrimaryKey(skus1.getId());
            sku.setStock(stock.getStock());
        }
        return skus;
    }

    public void deleteSpu(Long spuId) {
        // 删除库存及sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        for (Sku sku1 : skus) {
            stockMapper.deleteByPrimaryKey(sku1.getId());
            skuMapper.deleteByPrimaryKey(sku1.getId());
        }
        //删除spuDeatil
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);
        this.sendMsg(spuId,"delete");
    }

    public void upGood(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        spu.setSaleable(true);
        spuMapper.updateByPrimaryKey(spu);
    }

    public void downGood(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        spu.setSaleable(false);
        spuMapper.updateByPrimaryKey(spu);
    }

    public Spu selectSpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        return spu;
    }

    public void sendMsg(Long id,String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            LOGGER.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }
}
