package cn.leyou.item.services.service;

import cn.leyou.item.pojo.Brand;
import cn.leyou.item.services.mapper.BrandMapper;
import cn.leyou.pojo.ResultType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    public BrandMapper brandMapper;


    public ResultType<Brand> queryBrandLists(String key, int page, int rows, String sortBy, Boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%" + key +"%").orEqualTo("letter",key);
        }
        PageHelper.startPage(page,rows);
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " "+(desc ? "desc" : "asc"));
        }
        List<Brand> brands = brandMapper.selectByExample(example);
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);
        return new ResultType<>(brandPageInfo.getTotal(),brandPageInfo.getList());
    }

    public void saveBrand(Brand brand,List<Long> cids) {
        this.brandMapper.insertSelective(brand);

        for (Long cid : cids) {
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        }
    }

    public List<Brand> queryBrands(Long cid) {
        return brandMapper.queryBrands(cid);
    }

    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
