package cn.leyou.item.services.service;

import cn.leyou.item.pojo.SpecGroup;
import cn.leyou.item.pojo.SpecParam;
import cn.leyou.item.services.mapper.SpecGrupMapper;
import cn.leyou.item.services.mapper.SpecParmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecGroupService {
    @Autowired
    public SpecGrupMapper specGrupMapper;
    @Autowired
    public SpecParmMapper specParmMapper;

    public List<SpecGroup> querySpecGroups(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groups = specGrupMapper.select(specGroup);
        for (SpecGroup group : groups) {
            group.setSpecParams(querySpecParams(group.getId(),null,null,null));
        }
        return groups;
    }

    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean generic,Boolean searching) {
        Example example = new Example(SpecParam.class);
        Example.Criteria criteria = example.createCriteria();
        if(gid !=null){
            criteria.andEqualTo("groupId",gid);
        }
        if(cid !=null){
            criteria.andEqualTo("cid",cid);
        }
        if(generic !=null){
            if (generic==true) {
                criteria.andEqualTo("generic", "1");
            }else {
                criteria.andEqualTo("generic", "0");
            }
        }
        if(searching !=null){
            if (searching==true) {
                criteria.andEqualTo("searching", "1");
            }else {
                criteria.andEqualTo("searching", "0");
            }
        }
        return specParmMapper.selectByExample(example);
    }

    public int saveSpecParam(SpecParam specParam) {
        return specParmMapper.insert(specParam);
    }

    public int updateSpecParam(SpecParam specParam) {
        return specParmMapper.updateByPrimaryKey(specParam);
    }

    public int saveSpecGroup(SpecGroup specGroup) {
        return specGrupMapper.insert(specGroup);
    }

    public int deleteSpecGroup(Long id) {
        return specGrupMapper.deleteByPrimaryKey(id);
    }

}
