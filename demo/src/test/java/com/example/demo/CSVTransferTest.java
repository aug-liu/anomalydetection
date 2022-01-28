package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.data.handle.DataItemNoUse;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CSVTransferTest {

    /**
     * 日志对象
     */
    private static final Logger LOGGER =  LoggerFactory.getLogger(CSVTransferTest.class);

    private static Long INDEX = new Long( 1 );

    private static String ENCODING = "gb18030";

    private static int BATCH_SIZE = 10000;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    @Test
    void createIndex() throws IOException {
        // 1 create index
        CreateIndexRequest request = new CreateIndexRequest("data_202107");

        // 2 send request
        CreateIndexResponse response =
                restHighLevelClient.indices().create( request, RequestOptions.DEFAULT );
    }

    @Test
    public void testReadCSV() {
        String path = "D:\\java\\data_202107.tsv";

        handleCsvDataLBL( path, DataItemNoUse.class, "data_202107" );
    }


    /**
     * Method 2: Read the csv file line by line and write 10000 records to ES once - Suitable for large DataSet
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> void handleCsvDataLBL(String path, Class<T> clazz, String esIndex) {
        CSVReader reader;
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(path), ENCODING);
            CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').withQuoteChar('\'').build();
            reader = new CSVReaderBuilder(in).withCSVParser(csvParser).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }

        // 按照csv header定义csv数据与Java Bean的映射关系
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(clazz);
        try {
            strategy.captureHeader(reader);
        } catch (IOException | CsvRequiredFieldEmptyException e) {
            LOGGER.error(e.getMessage());
        }

        // 1、逐行读取数据； 2、转换成JavaBean； 3、存储到ES
        List<T> beanList = new ArrayList<>();
        String[] line = null;
        try{
            while ( (line = reader.readNext()) != null ) {
                T bean;
                bean = strategy.populateNewBean( line ); // convert the line String[] to Java Bean
                beanList.add(bean);

                System.out.println( "getLinesRead----------" + reader.getLinesRead() + ": " + line[0] + ", "+ line[2] + ", " + line[3] );
                System.out.println( "getRecordsRead----------" + reader.getRecordsRead() + ": " + line[0] + ", "+ line[2] + ", " + line[3] );

                // when there are 10000 records in the list, then put the list bulk to ES and clear the list to save memory
                if (beanList.size() >= BATCH_SIZE) {
                    System.out.println( "INDEX-----------------------" + INDEX );
                    batchInsert( esIndex, beanList );
                    beanList.clear();
                }
            }
        } catch (IOException | CsvFieldAssignmentException | CsvChainedException e ){
            System.out.println( "ERROR --- getLinesRead----------" + reader.getLinesRead() + ": " + line[0] + ", "+ line[2] + ", " + line[3] );
            System.out.println( "ERROR --- getRecordsRead----------" + reader.getRecordsRead() + ": " + line[0] + ", "+ line[2] + ", " + line[3] );
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        if (beanList.size() > 0)
            batchInsert( esIndex, beanList );

        System.out.println( "INDEX-----------------------" + INDEX );
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
        System.out.println("entities.sz = " + entities.size());
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




    /**
     * Method 2: 解析csv文件并批量转成bean - suitable for small dataset
     * @param path csv文件路径
     * @param clazz 类
     * @param <T> 泛型
     * @return 泛型bean集合
     */
    public <T> List<T> handleCsvData(String path, Class<T> clazz) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(path), ENCODING);
        } catch (Exception e) {
            LOGGER.error( e.getMessage() );
            return null;
        }

        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(clazz);

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(in)
                .withQuoteChar('\'')
                .withMappingStrategy(strategy)
                .withExceptionHandler(new CsvExceptionHandler() {
                    @Override
                    public CsvException handleException(CsvException e) throws CsvException {
                        LOGGER.error(e.getMessage());
                        return null;
                    }
                })
                .build();
        return csvToBean.parse();
    }

}
