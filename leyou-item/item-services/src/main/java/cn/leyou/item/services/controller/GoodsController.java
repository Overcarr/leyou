package cn.leyou.item.services.controller;

import cn.leyou.item.pojo.Sku;
import cn.leyou.item.pojo.Spu;
import cn.leyou.item.pojo.SpuDetail;
import cn.leyou.item.pojo.SpuVo;
import cn.leyou.item.services.service.GoodsService;
import cn.leyou.pojo.ResultType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags ="GoodsController",description = "商品管理")
public class GoodsController {
    @Autowired
    public GoodsService goodsService;

    @GetMapping("spu/page")
    @ApiOperation("商品查询")
    public ResponseEntity<ResultType<SpuVo>> querySpuVoList(@RequestParam(name = "key",required = false)String key,
                                                            @RequestParam(name ="saleable",required = false)Boolean saleable,
                                                            @RequestParam(name = "page",defaultValue = "1")int page,
                                                            @RequestParam(name = "rows",defaultValue = "5")int rows) {

        ResultType<SpuVo> list = goodsService.querySpuVoList(key,saleable,page,rows);
        if (CollectionUtils.isEmpty(list.getItems())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation("新增商品")
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuVo spuVo){
        System.out.println(spuVo);
        if (spuVo ==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        this.goodsService.saveGoods(spuVo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("查询SpuDetail")
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDeatils(@PathVariable Long spuId){
        SpuDetail spuDetail = goodsService.querySpuDeatils(spuId);
        if (spuDetail==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spuDetail);
    }

    @ApiOperation("查询sku及库存")
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkus(@RequestParam("id")Long spuId){
        List<Sku> skus = goodsService.querySkus(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    @ApiOperation("根据id删除商品")
    @DeleteMapping("spu/delete/{id}")
    public ResponseEntity<Void> deleteSpu(@PathVariable Long id){
        goodsService.deleteSpu(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上架商品")
    @PostMapping("spu/up/{id}")
    public ResponseEntity<Void> upGood(@PathVariable Long id){
        goodsService.upGood(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("下架商品")
    @PostMapping("spu/down/{id}")
    public ResponseEntity<Void> downGood(@PathVariable Long id){
        goodsService.downGood(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("查询spu根据主键")
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> selectSpuById(@RequestParam("id")Long id){
        return ResponseEntity.ok(goodsService.selectSpuById(id));
    }


    @GetMapping("sku/{id}")
    public Sku selectSkuById(@PathVariable Long id){
        return goodsService.selectSkuById(id);
    }
}
