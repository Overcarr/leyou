package cn.leyou.item.services.controller;

import cn.leyou.item.pojo.Brand;
import cn.leyou.item.services.service.BrandService;
import cn.leyou.pojo.ResultType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
@RestController
@Api(tags = "BrandController",description = "品牌管理")
public class BrandController {

    @Autowired
    public BrandService brandService;

    @ApiOperation("分页查询品牌")
    @GetMapping("page")
    public ResponseEntity<ResultType<Brand>> queryBrandLists(@RequestParam(name = "key",required = false)String key,
                                                             @RequestParam(name = "page",defaultValue = "1")int page,
                                                             @RequestParam(name = "rows",defaultValue = "5")int rows,
                                                             @RequestParam(name = "sortBy",required = false)String sortBy,
                                                             @RequestParam(name = "desc",required = false)Boolean desc){
          if (page >=100){
              page =1;
          }
          ResultType<Brand> resultType = brandService.queryBrandLists(key,page,rows,sortBy,desc);
          if (resultType ==null){
              return new ResponseEntity(HttpStatus.NOT_FOUND);
          }
          return new ResponseEntity<>(resultType,HttpStatus.OK);
    }

    @ApiOperation("保存品牌")
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam(name = "cids")List<Long> cids){
        if(brand==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("根据cid查询品牌")
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrands(@PathVariable Long cid){
        List<Brand> brands = brandService.queryBrands(cid);
        if(CollectionUtils.isEmpty(brands)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(brands);
    }

    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        Brand brand = brandService.queryBrandById(id);
        return ResponseEntity.ok(brand);
    };
}
