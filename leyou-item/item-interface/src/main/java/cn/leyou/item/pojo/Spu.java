package cn.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_spu")
@Data
public class Spu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  @Column(name = "sub_title")
  private String subTitle;
  private Long cid1;
  private Long cid2;
  private Long cid3;
  @Column(name = "brand_id")
  private Long brandId;
  private Boolean saleable;
  private Boolean valid;
  @Column(name = "create_time")
  private Date createTime;// 创建时间
  @Column(name = "last_update_time")
  private Date lastUpdateTime;// 最后修改时间
}
