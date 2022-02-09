package com.data.service;

import com.data.entity.Item;
import com.univocity.parsers.common.Context;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class ItemService {

    @Autowired
    private ElasticSearchService esService;

    /**
     * 日志对象
     */
    private static final Logger LOGGER =  LoggerFactory.getLogger(BigTableService.class);
//    private static String UTF_8 = "utf-8"; //utf-8  gb18030
//    private static String GB18030 = "gb18030"; //utf-8  gb18030
    private static int BATCH_SIZE = 10000;



    /**
     * FOR SINGLE FILE extract to ES
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
                        esService.batchInsert( esIndex, list );
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
                esService.batchInsert( esIndex, list );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
