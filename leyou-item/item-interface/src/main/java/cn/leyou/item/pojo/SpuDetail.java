package cn.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;

@Table(name = "tb_spu_detail")
@Data
public class SpuDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "spu_id")
  private Long spuId;
  private String description;
  @Column(name = "generic_spec")
  private String genericSpec;
  @Column(name = "special_spec")
  private String specialSpec;
  @Column(name = "packing_list")
  private String packingList;
  @Column(name = "after_service")
  private String afterService;

}
