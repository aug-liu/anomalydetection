package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.data.entity.BigTableItem;
import com.data.entity.DataItem;
import com.data.entity.Item;
import com.data.entity.ShopItem;
import com.data.service.BigTableService;
import com.data.service.ElasticSearchService;
import com.data.service.ItemService;
import com.univocity.parsers.common.Context;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class TSVTransferTest {

    /**
     * 日志对象
     */
    private static final Logger LOGGER =  LoggerFactory.getLogger(TSVTransferTest.class);

    private static Long INDEX = Long.valueOf(1);

    private static String UTF_8 = "utf-8"; //utf-8  gb18030
    private static String GB18030 = "gb18030"; //utf-8  gb18030

    private static int BATCH_SIZE = 10000;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    ElasticSearchService esService;

    @Autowired
    ItemService itemService;

    @Autowired
    BigTableService bigTableService;


    /**
     * 插入数据 - shop
     */
    @Test
    public void testReadCSVShop() {
        // shop 表的数据处理
        String index_Shop = "data_shop";
        esService.createIndex( index_Shop );

        String path_Shop = "D:\\java\\data_shop.tsv";
        itemService.handleTsvDataLBL( path_Shop, ShopItem.class, index_Shop, GB18030 );
    }

    /**
     * 插入数据 - Item
     */
    @Test
    public void testReadCSVData() {
        // data 表的数据处理
        String index_Data =  "data_202106";
        esService.createIndex( index_Data );

        String path_Data = "D:\\java\\data_202106.tsv";
        itemService.handleTsvDataLBL( path_Data, DataItem.class, index_Data, GB18030 );
    }

    /**
     * 插入数据 - BigTable
     */
    @Test
    public void testReadCSVBigData() {
        // 大宽表的数据处理
        String index_Data =  "big_table";
        esService.createIndex( index_Data );

        String path[] = {"D:\\java\\data_202106.tsv", "D:\\java\\data_202107.tsv", "D:\\java\\data_202108.tsv", "D:\\java\\data_202109.tsv"};
//        String path[] = {"D:\\java\\data_202106.tsv" };
        bigTableService.handleTsvDataBigTable( path, DataItem.class, index_Data, GB18030 );
    }

}
