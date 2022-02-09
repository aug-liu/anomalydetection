package com.data.service;

import com.data.entity.BigTableItem;
import com.data.entity.Item;
import com.data.entity.ShopItem;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BigTableService {

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
     * 1. read csv shop data and store the data in memory variable - map
     *
     * 2. read each file (202106 - 202109) and convert each column to an {@code DataItem} object
     * find the exactly matched {@code ShopItem} data from map variable
     *
     * 3. combine the {@code DataItem} object and {@code ShopItem} Object together to {@code BigTableItem}
     *
     * 4. pass data to ES
     *
     * @param path
     * @param clazz
     * @param esIndex
     * @param encoding
     * @param <T>
     */
    public <T extends Item> void handleTsvDataBigTable(String path[], Class<T> clazz, String esIndex, String encoding) {
        Map<Long, List<ShopItem>> map = null;

        // 1. read csv shop data and store the data in memory variable - map
        try {
            map = readShopDataByMonth( "D:\\java\\data_shop.tsv", encoding );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (map == null) return;

        // 2. read each file (202106 - 202109) and convert each column to an DataItem object
        // find the exactly matched ShopItem data from map variable
        Map<Long, List<ShopItem>> finalMap = map;
        Arrays.stream(path).forEach(p -> handleData(p, BigTableItem.class, esIndex, encoding, finalMap) );
    }

    /**
     * Map<Long, List<ShopItem>>
     *     key - Long, USER_ID
     *     value - List<>, usually have 4 items in the list
     * @return
     */
    private Map<Long, List<ShopItem>> readShopDataByMonth( String path, String encoding ) throws FileNotFoundException, UnsupportedEncodingException {
        Map<Long, List<ShopItem>> map = new HashMap<>();

        Reader inputReader = new InputStreamReader(new FileInputStream(new File(path)), encoding);
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(true);

        BeanProcessor<ShopItem> rowProcessor = new BeanProcessor<ShopItem>(ShopItem.class){
            @Override
            public void beanProcessed(ShopItem item, ParsingContext parsingContext) {
                if ( item != null && !item.isNull() ) {
                    List<ShopItem> list = map.get( item.getUserID() );
                    if( list == null )
                        list = new ArrayList<>();
                    list.add( item );

                    map.put(item.getUserID(), list);
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
        parser.stopParsing();
        return map;
    }


    /**
     *
     * @param path
     * @param clazz
     * @param esIndex
     * @param encoding
     * @param finalMap
     * @param <T>
     */
    private <T extends Item> void handleData( String path, Class<T> clazz, String esIndex, String encoding, Map<Long, List<ShopItem>> finalMap) {

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
                        // 3. combine the DataItem object and ShopItem Object together to BigTableItem
                        List<ShopItem> itemList = finalMap.get( item.getUserID() );
                        List<ShopItem> matchedData = itemList.stream()
                                .filter(iter -> iter.getMonth().equals(item.getMonth()))
                                .collect(Collectors.toList());

                        if ( matchedData == null || matchedData.size() <= 0 ) {
                            LOGGER.warn( "Could not find matched shop data!" );
                        } else {
                            if( matchedData.size() > 1 ) {
                                LOGGER.warn("Find more than one shop data, " + matchedData.size() + " records - "
                                        + matchedData.toString());
                                List<ShopItem> data = matchedData.stream()
                                        .filter(iter -> iter.getShopName() != null && iter.getShopName().equals(item.getShopName()))
                                        .collect(Collectors.toList());
                                item.merger( (data != null && data.size()>0) ? data.get(0) : matchedData.get(0) );
                            } else
                                item.merger( matchedData.get(0) );
                        }
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
