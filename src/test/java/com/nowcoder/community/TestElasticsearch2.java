package com.nowcoder.community;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Map;


/**
 * @author xi_wang
 * @create 2022-05-2022/5/20-9:59
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestElasticsearch2 {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private IndicesOperations io;
    @Test
    public void testIndicesOperations() throws InterruptedException, IOException{
        String myIndex = "test";
        if (io.checkIndexExists(myIndex))
            io.deleteIndex(myIndex);
//        io.createIndex(myIndex);
//        Thread.sleep(1000);
//        io.closeIndex(myIndex);
//        io.openIndex(myIndex);
//        io.deleteIndex(myIndex);

        //we need to close the client to free resources
        client.close();
    }

    @Test
    public void testMappingOperations() {
        String index = "mytest";
        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);
            io.createIndex(index);
            XContentBuilder builder = null;
            try {
                builder = XContentFactory.jsonBuilder().
                        startObject().
                        field("_doc").
                        startObject().
                        field("properties").
                        startObject().
                        field("name").
                        startObject().
                        field("type").
                        value("text").
                        endObject().
                        endObject().
                        endObject().
                        endObject();
                AcknowledgedResponse response = client.indices()
                        .putMapping(new PutMappingRequest(index).type("_doc").source(builder), RequestOptions.DEFAULT);
                if (!response.isAcknowledged()) {
                    System.out.println("Something strange happens");
                }

            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

//            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testDocumentOperations(){
        String index = "mytest";
        String type = "_doc";

        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);

            try {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping(type, XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject(type)
                                        .startObject("properties")
                                        .startObject("text").field("type", "text").field("store", "true").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

            IndexResponse ir = client.index(new IndexRequest(index, type, "2").source("text", "unicorn"), RequestOptions.DEFAULT);
            System.out.println("Version: " + ir.getVersion());

            GetResponse gr = client.get(new GetRequest(index, type, "2"), RequestOptions.DEFAULT);
            System.out.println("Version: " + gr.getVersion());

            UpdateResponse ur = client.update(new UpdateRequest(index, type, "2").script(new Script("ctx._source.text = 'v2'")), RequestOptions.DEFAULT);
            System.out.println("Version: " + ur.getVersion());

//            DeleteResponse dr = client.delete(new DeleteRequest(index, type, "2"), RequestOptions.DEFAULT);
//            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBulkOperations(){
        String index = "mytest";
        String type = "_doc";

        try {
//            if (io.checkIndexExists(index))
//                io.deleteIndex(index);
//            try {
//                client.indices().create(
//                        new CreateIndexRequest()
//                                .index(index)
//                                .mapping(type, XContentFactory.jsonBuilder()
//                                        .startObject()
//                                        .startObject(type)
//                                        .startObject("properties")
//                                        .startObject("position").field("type", "integer").field("store", "true").endObject()
//                                        .endObject()
//                                        .endObject()
//                                        .endObject()),
//                        RequestOptions.DEFAULT);
//                ;
//            } catch (IOException e) {
//                System.out.println("Unable to create mapping");
//            }
            BulkRequest bulker = new BulkRequest();
//            for (int i = 1; i < 100; i++) {
//                bulker.add(new IndexRequest(index, type, Integer.toString(i)).source("position", Integer.toString(i)));
//            }
//
//            System.out.println("Number of actions for index: " + bulker.numberOfActions());
//            client.bulk(bulker, RequestOptions.DEFAULT);


//            bulker = new BulkRequest();
//            for (int i = 1; i <= 100; i++) {
//                bulker.add(new UpdateRequest(index, type, Integer.toString(i)).script(new Script("ctx._source.position += 2")));
//            }
//            System.out.println("Number of actions for update: " + bulker.numberOfActions());
//            client.bulk(bulker, RequestOptions.DEFAULT);
//
            bulker = new BulkRequest();
            for (int i = 1; i <= 1000; i++) {
                bulker.add(new DeleteRequest(index, type, Integer.toString(i)));
            }
            System.out.println("Number of actions for delete: " + bulker.numberOfActions());
            client.bulk(bulker, RequestOptions.DEFAULT);
//
//            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testQuery_usingRestTemplate(){
        return;
    }

    @Test
    public void testQuery(){
        String index = "mytest";
        String type = "_doc";

        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);
            try {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping(type, XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject(type)
                                        .startObject("properties")
                                        .startObject("text").field("type", "integer").field("store", "true").endObject()
                                        .startObject("number1").field("type", "integer").field("store", "true").endObject()
                                        .startObject("number2").field("type", "integer").field("store", "true").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

            BulkRequest bulker = new BulkRequest();
            for (int i = 1; i < 1000; i++) {
                bulker.add(new IndexRequest(index, type, Integer.toString(i)).source("text", Integer.toString(i), "number1", i + 1, "number2", i % 2));
            }

            client.bulk(bulker, RequestOptions.DEFAULT);
            client.indices().refresh(new RefreshRequest(index), RequestOptions.DEFAULT);

            // 逐步构建搜索请求
            // 构建searchRequest需要设置indexName, 需要searchSourceBuilder，builder通过.query(query)进行配置
            TermQueryBuilder filter = QueryBuilders.termQuery("number2", 1);
            RangeQueryBuilder range = QueryBuilders.rangeQuery("number1").gt(500);
            // query整合filter与range两个条件
            BoolQueryBuilder query = QueryBuilders.boolQuery().must(range).filter(filter);
            // 将query加载进搜索请求中，并设置高亮
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query).highlighter(new HighlightBuilder().field("text").preTags("<em>").postTags("</em>"));;

            SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
            // 执行搜索请求
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response.getHits().getTotalHits());
            // 输出命中的条目
            if (response.status().getStatus() == 200) {
                System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
                System.out.println("Maximum score: " + response.getHits().getMaxScore());

                for (SearchHit hit : response.getHits().getHits()) {
                    System.out.println(hit.getSourceAsString());
                    System.out.println("HighlightFields:");
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    for(Map.Entry<String, HighlightField> entry:highlightFields.entrySet()){
                        System.out.println("key:"+entry.getKey());
                        System.out.println("value:"+entry.getValue());
                    }
                }
            }

            SearchResponse response2 = client.search(new SearchRequest().indices(index), RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response2.getHits().getTotalHits());

//            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testQuery2(){
        String index = "mytest";
        String type = "_doc";

        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);
            try {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping(type, XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject(type)
                                        .startObject("properties")
                                        .startObject("text").field("type", "text").field("store", "true").endObject()
                                        .startObject("number1").field("type", "text").field("store", "true").endObject()
                                        .startObject("number2").field("type", "text").field("store", "true").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

            BulkRequest bulker = new BulkRequest();
            for (int i = 1; i < 1000; i++) {
                bulker.add(new IndexRequest(index, type, Integer.toString(i)).source("text", Integer.toString(i), "number1", Integer.toString(i + 1), "number2", Integer.toString(i % 2)));
            }

            client.bulk(bulker, RequestOptions.DEFAULT);
            client.indices().refresh(new RefreshRequest(index), RequestOptions.DEFAULT);

            // 逐步构建搜索请求
            // 构建searchRequest需要设置indexName, 需要searchSourceBuilder，builder通过.query(query)进行配置
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery("99", "number1");
            RangeQueryBuilder range = QueryBuilders.rangeQuery("number1").gt(500);
            // query整合filter与range两个条件
            BoolQueryBuilder query = QueryBuilders.boolQuery().must(range).filter(matchQuery);
            // 将query加载进搜索请求中，并设置高亮
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query).highlighter(new HighlightBuilder().field("text").preTags("<em>").postTags("</em>"));;

            SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
            // 执行搜索请求
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response.getHits().getTotalHits());
            // 输出命中的条目
            if (response.status().getStatus() == 200) {
                System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
                System.out.println("Maximum score: " + response.getHits().getMaxScore());

                for (SearchHit hit : response.getHits().getHits()) {
                    System.out.println(hit.getSourceAsString());
                    System.out.println("HighlightFields:");
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    for(Map.Entry<String, HighlightField> entry:highlightFields.entrySet()){
                        System.out.println("key:"+entry.getKey());
                        System.out.println("value:"+entry.getValue());
                    }
                }
            }

            SearchResponse response2 = client.search(new SearchRequest().indices(index), RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response2.getHits().getTotalHits());

//            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testAggregation(){
        String index = "mytest";
        String type = "_doc";

        // 逐步构建 search请求
        // search请求 需要indexName, 需要searchSourceBuilder, 需要query，query通过.aggregation()接受 聚合函数
        // 在tag字段上进行能够 词条聚合，统计.terms()中的出现
        AggregationBuilder aggsBuilder = AggregationBuilders.terms("tag").field("tag");
        // 在number1字段上进行能够数字字段的 扩展统计聚合，统计.terms()中的出现
        ExtendedStatsAggregationBuilder aggsBuilder2 = AggregationBuilders.extendedStats("number1").field("number1");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).aggregation(aggsBuilder).
                aggregation(aggsBuilder2).size(0);
        SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
        SearchResponse response = null;
        // 执行 search请求
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (response.status().getStatus() == 200) {
            System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
            Terms termsAggs = response.getAggregations().get("tag");
            System.out.println("Aggregation name: " + termsAggs.getName());
            System.out.println("Aggregation total: " + termsAggs.getBuckets().size());
            for (Terms.Bucket entry : termsAggs.getBuckets()) {
                System.out.println(" - " + entry.getKey() + " " + entry.getDocCount());
            }

            ExtendedStats extStats = response.getAggregations().get("number1");
            System.out.println("Aggregation name: " + extStats.getName());
            System.out.println("Count: " + extStats.getCount());
            System.out.println("Min: " + extStats.getMin());
            System.out.println("Max: " + extStats.getMax());
            System.out.println("Standard Deviation: " + extStats.getStdDeviation());
            System.out.println("Sum of Squares: " + extStats.getSumOfSquares());
            System.out.println("Variance: " + extStats.getVariance());

        }
        try {
            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //we need to close the client to free resources
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
