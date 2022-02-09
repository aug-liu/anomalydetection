package com.data.handle;

import com.data.handle.DateTimeConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
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
public class DataItemNoUse {

    @CsvCustomBindByName(column = "DATA_MONTH", converter = DateTimeConverter.class)
    private Date month;

    @CsvBindByName(column = "ITEM_ID")
    private Long itemID;

    @CsvBindByName(column = "ITEM_NAME")
    private String itemName;

    @CsvBindByName(column = "BRAND_ID")
    private Long brandID;

    @CsvBindByName(column = "BRAND_NAME")
    private String brandName;

    @CsvBindByName(column = "ITEM_PRICE")
    private Double price;

    @CsvBindByName(column = "ITEM_SALES_VOLUME")
    private Double salesVolumn;

    @CsvBindByName(column = "ITEM_SALES_AMOUNT")
    private Double salesAmount;

    @CsvBindByName(column = "CATE_NAME_LV1")
    private String level1;

    @CsvBindByName(column = "CATE_NAME_LV2")
    private String level2;

    @CsvBindByName(column = "CATE_NAME_LV3")
    private String level3;

    @CsvBindByName(column = "CATE_NAME_LV4")
    private String level4;

    @CsvBindByName(column = "CATE_NAME_LV5")
    private String level5;

    @CsvBindByName(column = "ITEM_FAV_NUM")
    private Long favorNum;

    @CsvBindByName(column = "TOTAL_EVAL_NUM")
    private Long evalNum;

    @CsvBindByName(column = "ITEM_STOCK")
    private Long stock;

    @CsvBindByName(column = "ITEM_DELIVERY_PLACE")
    private String deliveryPlace;

    @CsvBindByName(column = "ITEM_PROD_PLACE")
    private String prodPlace;

    @CsvBindByName(column = "ITEM_PARAM")
    private String param;

    @CsvBindByName(column = "USER_ID")
    private long userID;

    @CsvBindByName(column = "SHOP_NAME")
    private String shopName;


    public static void main(String[] args) {
        long long_max= Long.MAX_VALUE;//得到长整型的最大值。
        int int_max= Integer.MAX_VALUE;//得到整型的最大值。
        short short_max=Short.MAX_VALUE;//得到短整型的最大值。
        byte byte_max=Byte.MAX_VALUE;//得到Byte型最大值。
        System.out.println("LONG最大值："+long_max);
        System.out.println("INT最大值："+int_max);
        System.out.println("SHORT最大值："+short_max);
        System.out.println("BYTE最大值："+byte_max);
    }
}
