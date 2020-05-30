package cn.leyou.item.services.service;

import cn.leyou.item.pojo.Category;
import cn.leyou.item.services.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    public CategoryMapper categoryMapper;



    public List<Category> queryCategory(Long id){
        Category category = new Category();
        category.setParentId(id);
        return categoryMapper.select(category);
    }

    public List<String> querycNameBycIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

    public List<Category> queryById(Long id) {
        Category c1 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c1.getParentId());
        Category c3 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
