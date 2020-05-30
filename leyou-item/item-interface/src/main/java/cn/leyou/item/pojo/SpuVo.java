package cn.leyou.item.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SpuVo extends Spu {
    public String cName;
    public String bName;
    public SpuDetail spuDetail;
    public List<Sku> skus;
}
