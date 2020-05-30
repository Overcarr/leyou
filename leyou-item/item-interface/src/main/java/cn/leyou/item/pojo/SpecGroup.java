package cn.leyou.item.pojo;



import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Table(name = "tb_spec_group")
@Data
public class SpecGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long cid;

  private String name;

  private List<SpecParam> specParams;



  // getter和setter省略

}