package com.example.demo.config;

import com.data.service.BigTableService;
import com.data.service.ElasticSearchService;
import com.data.service.ItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }

    @Bean
    public ElasticSearchService esService() {
        return new ElasticSearchService();
    }

    @Bean
    public ItemService itemService(){
        return new ItemService();
    }

    @Bean
    public BigTableService bigTableService() {
        return new BigTableService();
    }

}
