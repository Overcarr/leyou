package cn.leyou.item.api;

import cn.leyou.item.pojo.Sku;
import cn.leyou.item.pojo.Spu;
import cn.leyou.item.pojo.SpuDetail;
import cn.leyou.item.pojo.SpuVo;
import cn.leyou.pojo.ResultType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping
public interface GoodsApi {

    // 商品查询
    @GetMapping("spu/page")
    public ResultType<SpuVo> querySpuVoList(@RequestParam(name = "key",required = false)String key,
                                                            @RequestParam(name ="saleable",required = false)Boolean saleable,
                                                            @RequestParam(name = "page",defaultValue = "1")int page,
                                                            @RequestParam(name = "rows",defaultValue = "5")int rows);

    // 根据spuId查询商品详情
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDeatils(@PathVariable Long spuId);

    //根据spuId查询sku
    @GetMapping("sku/list")
    public List<Sku> querySkus(@RequestParam("id")Long spuId);

    //根据spuId查询spu
    @GetMapping("spu/{id}")
    public Spu selectSpuById(@RequestParam("id")Long id);

    @GetMapping("sku/{id}")
    public Sku selectSkuById(@PathVariable Long id);
}
