package cn.leyou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultType<T> {

    private Long total;

    private int totalPage;

    private List<T> items;

    public ResultType(Long total,List<T> data){
        this.total = total;
        this.items = data;
    }

}
