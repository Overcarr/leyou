package cn.leyou.item.api;

import cn.leyou.item.pojo.SpecGroup;
import cn.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecApi {

    @GetMapping("groups/{cid}")
    public List<SpecGroup> querySpecGroups(@PathVariable("cid") Long cid);

    @GetMapping("params")
    public List<SpecParam> querySpecParams(@RequestParam(name = "gid",required = false)Long gid,
                                                           @RequestParam(name = "cid",required = false)Long cid,
                                                           @RequestParam(name = "generic",required = false)Boolean generic,
                                                           @RequestParam(name = "searching",required = false)Boolean searching
    );
}
