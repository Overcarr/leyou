package cn.leyou.item.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Table(name = "tb_spec_param")
@Data
public class SpecParam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long cid;
  @Column(name = "group_id")
  private Long groupId;
  private String name;
  @Column(name = "`numeric`")
  private Boolean numeric;
  private String unit;
  private Boolean generic;
  private Boolean searching;
  private String segments;


}
