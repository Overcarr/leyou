package cn.leyou.item.services.controller;

import cn.leyou.item.pojo.Category;
import cn.leyou.item.services.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
@Api(tags = "CategoryController",description = "分类商品")
public class CategoryController {
    @Autowired
    public CategoryService categoryService;

    @ApiOperation("查询商品")
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategory(@RequestParam Long pid){
         if (pid.longValue() < 0 || pid == null){
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
        List<Category> categories = categoryService.queryCategory(pid);
         if (categories.size()>0 && categories!=null){
             return new ResponseEntity<>(categories, HttpStatus.OK);
         }
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation("根据ids查询商品")
    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){

        List<String> names = this.categoryService.querycNameBycIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }

    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryById(@RequestParam("id")Long id){
        List<Category> categories = categoryService.queryById(id);
        if (CollectionUtils.isEmpty(categories)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categories);
    }



}
