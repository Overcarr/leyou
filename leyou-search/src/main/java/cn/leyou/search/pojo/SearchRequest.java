package cn.leyou.search.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    public String key;
    public Integer page;
    public String sortBy;
    public Boolean descending;
    private Map<String,String> filter;
    private static final Integer DEAFULT_SIZE = 20;
    private static final Integer DEAFULT_PAGE = 1;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
            if (page==null){
                return DEAFULT_PAGE;
            }
            return Math.max(DEAFULT_PAGE,page);
    }
    public Integer getSize() {
        return DEAFULT_SIZE;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
