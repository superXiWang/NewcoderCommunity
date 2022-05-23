package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.elasticsearch.DocumentOperations;
import com.nowcoder.community.util.elasticsearch.IndicesOperations;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * @author xi_wang
 * @create 2022-05-2022/5/22-20:54
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMyElasticsearchAPI {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private IndicesOperations io;
    @Autowired
    @Qualifier("discussPostDocumentOperations")
    private DocumentOperations discussPostDocumentOperations;
    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testDocumentInsert(){
        DiscussPost discussPost = discussPostService.findDiscussPost(287);
        IndexResponse insert = discussPostDocumentOperations.insert("1", discussPost);
        System.out.println(insert.getIndex());
    }
    @Test
    public void testDocumentGet(){
        GetResponse documentFields = discussPostDocumentOperations.get("1");
        System.out.println(documentFields.getSourceAsString());
        //        Map<String, Object> res=documentFields.getSource();
//        for(Map.Entry<String, Object> entry:res.entrySet()){
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//        }


    }
    @Test
    public void testDocumentDelete(){
        DeleteResponse delete = discussPostDocumentOperations.delete("1");
    }

    @Test
    public void testDocumentBulkInsert(){
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(101, 0, 10);
        discussPostDocumentOperations.bulkInsert(discussPosts);
    }

    @Test
    public void testDocumentSearch(){

        SearchResponse response = discussPostDocumentOperations.search("计划", 2,3,"title", "content");
        // 输出命中的条目
        if (response.status().getStatus() == 200) {
            System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
            System.out.println("Maximum score: " + response.getHits().getMaxScore());

            for (SearchHit hit : response.getHits().getHits()) {
                System.out.println("-------------------------------------");
                System.out.println(hit.getSourceAsString());
                System.out.println("HighlightFields:");
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                for(Map.Entry<String, HighlightField> entry:highlightFields.entrySet()){
                    System.out.println("key:"+entry.getKey());
                    System.out.println("value:"+entry.getValue());
                    Text[] fragments = entry.getValue().getFragments();
                    int i=0;
                    for (Text each:fragments){
                        i++;
                        System.out.println("Fragment "+i+": "+each.toString());
                    }
                }
                System.out.println("-------------------------------------");
            }
        }
    }

}
