package cn.leyou.search.controller;

import cn.leyou.pojo.ResultType;
import cn.leyou.search.pojo.Goods;
import cn.leyou.search.pojo.SearchRequest;
import cn.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class SearchController {
    @Autowired
    public SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<ResultType<Goods>> searchPage(@RequestBody SearchRequest searchRequest){
        ResultType<Goods> resultType = searchService.searchPage(searchRequest);
        if(resultType==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(resultType);
    }
}
