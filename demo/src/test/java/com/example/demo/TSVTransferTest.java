package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.data.entity.DataItem;
import com.data.entity.Item;
import com.data.entity.ShopItem;
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

@SpringBootTest
public class TSVTransferTest {

    /**
     * 日志对象
     */
    private static final Logger LOGGER =  LoggerFactory.getLogger(TSVTransferTest.class);

    private static Long INDEX = new Long( 1 );

    private static String UTF_8 = "utf-8"; //utf-8  gb18030
    private static String GB18030 = "gb18030"; //utf-8  gb18030

    private static int BATCH_SIZE = 10000;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    /**
     * 插入数据
     */
    @Test
    public void testReadCSVShop() {
        // shop 表的数据处理
        String index_Shop = "data_shop";
        createIndex( index_Shop );

        String path_Shop = "D:\\java\\data_shop.tsv";
        handleTsvDataLBL( path_Shop, ShopItem.class, index_Shop, GB18030 );
    }

    /**
     * 插入数据
     */
    @Test
    public void testReadCSVData() {
        // data 表的数据处理
        String index_Data =  "data_202106";
        createIndex( index_Data );

        String path_Data = "D:\\java\\data_202106.tsv";
        handleTsvDataLBL( path_Data, DataItem.class, index_Data, GB18030 );
    }


    /**
     * 创建索引
     */
    void createIndex( String indexName ) {
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
     * Read the csv file line by line and write #BATCH_SIZE records to ES once - Suitable for large DataSet
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends Item> void handleTsvDataLBL(String path, Class<T> clazz, String esIndex, String encoding) {

        try (Reader inputReader = new InputStreamReader(new FileInputStream(new File(path)), encoding)) {
            List<T> list = new ArrayList<>();

            TsvParserSettings settings = new TsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            BeanProcessor<T> rowProcessor = new BeanProcessor<T>(clazz){
                @Override
                public T createBean(String[] row, Context context) {
                    T item = null;
                    try {
                        item = super.createBean(row, context);
                    } catch (Exception e) {
                        LOGGER.error(e.getCause().getMessage());
                        try {
                            item = clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException ee) {
                            LOGGER.error(ee.getCause().getMessage());
                        }
                    }
                    return item;
                }

                @Override
                public void beanProcessed(T item, ParsingContext parsingContext) {
                    if ( item != null && !item.isNull() ) {
                        list.add(item);
                    }

                    if (list.size() >= BATCH_SIZE) {
                        batchInsert( esIndex, list );
                        list.clear();
                    }
                }
            } ;
            settings.setProcessor(rowProcessor);
            TsvParser parser = new TsvParser(settings);

            parser.beginParsing( inputReader );
            String[] row = null;
            while((row = parser.parseNext()) != null) {
                continue;
            }

            if (list.size() > 0)
                batchInsert( esIndex, list );

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
            request.timeout(TimeValue.timeValueSeconds(10))
                    .id( "" + INDEX++ )
                    .source(JSON.toJSONString(org), XContentType.JSON);
            bulkRequest.add(request);
        }

        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
