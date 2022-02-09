package com.data.service;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ElasticSearchService {

    private static Long INDEX = Long.valueOf(1);

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    public void createIndex( String indexName ) {
        // 1 create index
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        // 2 send request
        try {
            CreateIndexResponse response =
                    restHighLevelClient.indices().create( request, RequestOptions.DEFAULT );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Batch insert data to ElasticSearch
     * @param index
     * @param entities
     * @param <T>
     * @throws IOException
     */
    public <T> void batchInsert(String index, List<T> entities)  {
        BulkRequest bulkRequest = new BulkRequest(index);
        System.out.println("entities.sz = " + INDEX );
        for (T org : entities) {
            IndexRequest request = new IndexRequest( index );
            // put /data_202107/_doc/

            String strToSave = JSON.toJSONString(org);
            request.timeout(TimeValue.timeValueSeconds(10))
                    .id( "" + INDEX++ )
                    .source(strToSave, XContentType.JSON);
            bulkRequest.add(request);
        }

        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
