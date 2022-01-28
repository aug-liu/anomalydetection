package com.data.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.data.handle.DateTimeConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class DataItem implements Item {

    @Parsed(field = "DATA_MONTH")
    @JSONField(name = "DATA_MONTH")
    @Format(formats = {"yyyyMM"}, options = "locale=en;lenient=false")
    private Date month;

    @Parsed(field = "ITEM_ID")
    @JSONField(name = "ITEM_ID")
    private Long itemID;

    @Parsed(field = "ITEM_NAME")
    @JSONField(name = "ITEM_NAME")
    private String itemName;

    @Parsed(field = "BRAND_ID")
    @JSONField(name = "BRAND_ID")
    private Long brandID;

    @Parsed(field = "BRAND_NAME")
    @JSONField(name = "BRAND_NAME")
    private String brandName;

    @Parsed(field = "ITEM_PRICE")
    @JSONField(name = "ITEM_PRICE")
    private Double price;

    @Parsed(field = "ITEM_SALES_VOLUME")
    @JSONField(name = "ITEM_SALES_VOLUME")
    private Double salesVolumn;

    @Parsed(field = "ITEM_SALES_AMOUNT")
    @JSONField(name = "ITEM_SALES_AMOUNT")
    private Double salesAmount;

    @Parsed(field = "CATE_NAME_LV1")
    @JSONField(name = "CATE_NAME_LV1")
    private String level1;

    @Parsed(field = "CATE_NAME_LV2")
    @JSONField(name = "CATE_NAME_LV2")
    private String level2;

    @Parsed(field = "CATE_NAME_LV3")
    @JSONField(name = "CATE_NAME_LV3")
    private String level3;

    @Parsed(field = "CATE_NAME_LV4")
    @JSONField(name = "CATE_NAME_LV4")
    private String level4;

    @Parsed(field = "CATE_NAME_LV5")
    @JSONField(name = "CATE_NAME_LV5")
    private String level5;

    @Parsed(field = "ITEM_FAV_NUM")
    @JSONField(name = "ITEM_FAV_NUM")
    private Long favorNum;

    @Parsed(field = "TOTAL_EVAL_NUM")
    @JSONField(name = "TOTAL_EVAL_NUM")
    private Long evalNum;

    @Parsed(field = "ITEM_STOCK")
    @JSONField(name = "ITEM_STOCK")
    private Long stock;

    @Parsed(field = "ITEM_DELIVERY_PLACE")
    @JSONField(name = "ITEM_DELIVERY_PLACE")
    private String deliveryPlace;

    @Parsed(field = "ITEM_PROD_PLACE")
    @JSONField(name = "ITEM_PROD_PLACE")
    private String prodPlace;

    @Parsed(field = "ITEM_PARAM")
    @JSONField(name = "ITEM_PARAM")
    private String param;

    @Parsed(field = "USER_ID")
    @JSONField(name = "USER_ID")
    private long userID;

    @Parsed(field = "SHOP_NAME")
    @JSONField(name = "SHOP_NAME")
    private String shopName;

    @Override
    public boolean isNull() {
        return getItemID() == null || getItemID() <= 0;
    }

}
