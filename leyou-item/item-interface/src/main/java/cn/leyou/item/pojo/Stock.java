package cn.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tb_stock")
public class Stock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sku_id")
  private Long skuId;
  @Column(name = "seckill_stock")
  private Integer seckillStock;
  @Column(name = "seckill_total")
  private Integer seckillTotal;
  private Integer stock;


}
