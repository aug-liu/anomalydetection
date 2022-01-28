package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
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
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    @Test
    void createIndex() throws IOException {
        // 1 create index
        CreateIndexRequest request = new CreateIndexRequest("yolanda_java");

        // 2 send request
        CreateIndexResponse response =
                restHighLevelClient.indices().create( request, RequestOptions.DEFAULT );

        System.out.println( response );
    }

    /**
     * 获取索引
     */
    @Test
    void testExistingIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("yolanda_java");
        boolean exist = restHighLevelClient.indices().exists( request, RequestOptions.DEFAULT );
        System.out.println( exist );
    }


    /**
     * 删除索引
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("yolanda");
        AcknowledgedResponse response = restHighLevelClient.indices().delete( request, RequestOptions.DEFAULT );
        System.out.println( response.isAcknowledged() );
    }


    /**
     * 测试添加文档
     */
    @Test
    void testAddDocment() throws IOException {
        User user = new User("yolanda", 20);
        // 创建请求
        IndexRequest indexRequest = new IndexRequest("yolanda_index");

        // put /yolanda_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        // 把数据放入请求
        IndexRequest source = indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        // 客户端发送请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status()); // created
    }

    /**
     * 获取文档，判断是否存在
     */
    @Test
    void testIsDocExits() throws IOException {
        GetRequest getRequest = new GetRequest("yolanda_index", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 获取文档信息
     */
    @Test
    void testGetDoc() throws IOException {
        GetRequest getRequest = new GetRequest("yolanda_index", "1");

        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());//打印文档内容
        System.out.println(getResponse);
    }

    /**
     * 更新文档记录
     */
    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("yolanda_index", "1");
        request.timeout("1s");
        User user = new User("yolanda Liu", 30);

        request.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 删除文档记录
     */
    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("yolanda_index", "3");
        request.timeout("1s");

        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }


    // 批量插入数据：
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");

        ArrayList<User> userList = new ArrayList<>();
        userList.add( new User("yolanda1", 20));
        userList.add( new User("yolanda2", 20));

        for (int i=0; i<userList.size(); i++) {
            // 批量更新、创建、删除
            request.add(
                    new IndexRequest("yolanda_index")
                            .id(""+(i+3))
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    // 查询
    // 1、搜索请求
    // 2、条件构造
    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("yolanda_index");
        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // term: 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "yolanda 1");

//        QueryBuilders.matchAllQuery();

        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));

        System.out.println("--------------------------------------");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }
}
