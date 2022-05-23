package com.nowcoder.community.util.elasticsearch;


import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/20-9:54
 */
@Component
public class IndicesOperations {

    @Autowired
    private RestHighLevelClient client;

    public IndicesOperations(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean checkIndexExists(String name) throws IOException {
        return client.indices().exists(new GetIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void createIndex(String name) throws IOException {
        client.indices().create(new CreateIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void deleteIndex(String name) throws IOException {
        client.indices().delete(new DeleteIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void closeIndex(String name) throws IOException {
        client.indices().close(new CloseIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void openIndex(String name) throws IOException {
        client.indices().open(new OpenIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void putMapping(String index, String typeName, String source) throws IOException {
        client.indices().putMapping(new PutMappingRequest(index).type(typeName).source(source), RequestOptions.DEFAULT);
    }

}
