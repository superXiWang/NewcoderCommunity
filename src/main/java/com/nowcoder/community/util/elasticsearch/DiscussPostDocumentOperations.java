package com.nowcoder.community.util.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/22-20:05
 */
@Component
public class DiscussPostDocumentOperations implements DocumentOperations<DiscussPost> {
    @Autowired
    public RestHighLevelClient client;
    @Autowired
    public IndicesOperations io;

    public static final String index = "discusspost";

    public Map<String, Object> discussPostToMap(DiscussPost discussPost) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", discussPost.getId());
        map.put("userId", discussPost.getUserId());
        map.put("title", discussPost.getTitle());
        map.put("content", discussPost.getContent());
        map.put("type", discussPost.getType());
        map.put("status", discussPost.getStatus());
        // ??????Date???????????????
        Date createTime = discussPost.getCreateTime();
//        System.out.println(createTime);
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(createTime);
//        System.out.println(format);
        map.put("createTime", format);
        map.put("commentCount", discussPost.getCommentCount());
        map.put("score", discussPost.getScore());

        return map;
    }

    @Override
    public IndexResponse insert(String id, DiscussPost source) {
        IndexResponse ir = null;
        try {
            // ????????????discusspost??????????????????
            if (!io.checkIndexExists(index)) {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping("_doc", XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject("_doc")
                                        .startObject("properties")
                                        .startObject("id").field("type", "integer").endObject()
                                        .startObject("userId").field("type", "integer").endObject()
                                        .startObject("title").field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart").endObject()
                                        .startObject("content").field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart").endObject()
                                        .startObject("type").field("type", "integer").endObject()
                                        .startObject("status").field("type", "integer").endObject()
                                        .startObject("createTime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss z||strict_date_optional_time||epoch_millis").endObject()
//                                        .startObject("createTime").field("type", "date").endObject()
                                        .startObject("commentCount").field("type", "integer").endObject()
                                        .startObject("score").field("type", "double").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            }
            // ?????????????????????
            Map<String, Object> sourceMap = discussPostToMap(source);
            ir = client.index(new IndexRequest(index, "_doc", id).source(sourceMap), RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("Unable to create mapping");
        }
        return ir;
    }

    @Override
    public void bulkInsert(List<DiscussPost> sourceList) {
        try {
            // ????????????discusspost??????????????????
            if (!io.checkIndexExists(index)) {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping("_doc", XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject("_doc")
                                        .startObject("properties")
                                        .startObject("id").field("type", "integer").endObject()
                                        .startObject("userId").field("type", "integer").endObject()
                                        .startObject("title").field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart").endObject()
                                        .startObject("content").field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart").endObject()
                                        .startObject("type").field("type", "integer").endObject()
                                        .startObject("status").field("type", "integer").endObject()
                                        .startObject("createTime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss z||strict_date_optional_time||epoch_millis").endObject()
//                                        .startObject("createTime").field("type", "date").endObject()
                                        .startObject("commentCount").field("type", "integer").endObject()
                                        .startObject("score").field("type", "double").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            }
            // ?????????????????????
            if(sourceList!=null && sourceList.size()!=0){
                BulkRequest bulker = new BulkRequest();
                for (DiscussPost each:sourceList) {
                    Map<String, Object> sourceMap = discussPostToMap(each);
                    bulker.add(new IndexRequest(index, "_doc", String.valueOf(each.getId())).source(sourceMap));
                }
                client.bulk(bulker, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            System.out.println("Unable to create mapping");
        }
    }

    @Override
    public DeleteResponse delete(String id) {
        try {
            // ????????????????????????
            if(!io.checkIndexExists(index)){
                return null;
            }

            DeleteResponse dr = client.delete(new DeleteRequest(index, "_doc", id), RequestOptions.DEFAULT);
            return dr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UpdateResponse update(String id, String key, String value) {
        // ????????????????????????
        try {
            if(!io.checkIndexExists(index)){
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder scriptString=new StringBuilder("ctx._source");
        scriptString.append("."+key+"=\'"+value+"\'");
        Script script=new Script(scriptString.toString());
        try {
            UpdateResponse ur = client.update(new UpdateRequest(index, "_doc", id).script(script), RequestOptions.DEFAULT);
            return ur;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GetResponse get(String id) {
        try {
            // ????????????????????????
            if(!io.checkIndexExists(index)){
                return null;
            }
            GetResponse gr = client.get(new GetRequest(index, "_doc", id), RequestOptions.DEFAULT);
            return gr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public SearchResponse search(String keyword, int current, int limit, String... fields){
        // ????????????????????????
        try {
            if(!io.checkIndexExists(index)){
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ??????????????????
        // ??????searchRequest????????????indexName, ??????searchSourceBuilder???builder??????.query(query)????????????
        MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(keyword, fields);
        // ???query??????????????????????????????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(fields!=null){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for(String each:fields){
                highlightBuilder.field(each);
            }
            highlightBuilder.preTags("<em>").postTags("</em>");
            searchSourceBuilder.query(matchQuery)
                    .highlighter(highlightBuilder)
                    .from(current)
                    .size(limit)
                    .sort("type", SortOrder.DESC)
                    .sort("score",SortOrder.DESC)
                    .sort("createTime",SortOrder.DESC);

            SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
            // ??????????????????
            try {
                SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
