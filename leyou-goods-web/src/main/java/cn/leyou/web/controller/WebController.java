package cn.leyou.web.controller;

import cn.leyou.web.service.GoodsHtmlService;
import cn.leyou.web.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("item")
public class WebController {


    @Autowired
    private WebService webService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        Map<String, Object> map = webService.loadData(id);

        model.addAllAttributes(map);
        // 页面静态化
        this.goodsHtmlService.asyncExcute(id);
        return "item";
    }
}
