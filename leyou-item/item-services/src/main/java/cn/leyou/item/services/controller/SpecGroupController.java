package cn.leyou.item.services.controller;

import cn.leyou.item.pojo.SpecGroup;
import cn.leyou.item.pojo.SpecParam;
import cn.leyou.item.services.service.SpecGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
@Api(tags = "SpecGroupController",description = "规格参数")
public class SpecGroupController {

    @Autowired
    public SpecGroupService specGroupService;

    @ApiOperation("查询规格参数组")
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups=specGroupService.querySpecGroups(cid);
        if(CollectionUtils.isEmpty(specGroups)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(specGroups);
    }

    @ApiOperation("根据条件查询规格参数")
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParams(@RequestParam(name = "gid",required = false)Long gid,
                                                           @RequestParam(name = "cid",required = false)Long cid,
                                                           @RequestParam(name = "generic",required = false)Boolean generic,
                                                           @RequestParam(name = "searching",required = false)Boolean searching
                                                           ){
        List<SpecParam> specParams= specGroupService.querySpecParams(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(specParams)){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(specParams);
    }

    @PostMapping("param")
    @ApiOperation("新增规格参数")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam){
        int i = specGroupService.saveSpecParam(specParam);
        if(i == 0 ){
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("param")
    @ApiOperation("修改规格参数")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam){
        int i = specGroupService.updateSpecParam(specParam);
        if(i == 0 ){
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("group")
    @ApiOperation("新增规格参数组")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup){
        int i = specGroupService.saveSpecGroup(specGroup);
        if(i == 0 ){
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("group/{id}")
    @ApiOperation(("删除规格参数组"))
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id")Long id){
        int i = specGroupService.deleteSpecGroup(id);
        if(i == 0 ){
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
