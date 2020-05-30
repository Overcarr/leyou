package cn.leyou.item.services.mapper;

import cn.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    void insertCategoryAndBrand(Long cid, Long bid);

    /*@Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")*/
    List<Brand> queryBrands(Long cid);
}
