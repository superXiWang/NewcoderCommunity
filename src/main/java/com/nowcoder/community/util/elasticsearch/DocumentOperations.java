package com.nowcoder.community.util.elasticsearch;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/22-20:02
 */
@Component
public interface DocumentOperations<T> {
    public IndexResponse insert(String id,T source);
    public DeleteResponse delete(String id);
    public UpdateResponse update(String id, String key, String value);
    public GetResponse get(String id);
    public void bulkInsert(List<T> sourceList);
    public SearchResponse search(String keyword,int current,int limit,String... fields);
}
