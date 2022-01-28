package com.data.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
public class ShopItem implements Item {

    @Parsed(field = "DATA_MONTH")
    @JSONField(name = "DATA_MONTH")
    @Format(formats = {"yyyyMM"}, options = "locale=en;lenient=false")
    private Date month;

    @Parsed(field = "USER_ID")
    @JSONField(name = "USER_ID")
    private Long userID;

    @Parsed(field = "SHOP_NAME")
    @JSONField(name = "SHOP_NAME")
    private String shopName;

    @Parsed(field = "SHOP_SALES_VOLUME")
    @JSONField(name = "SHOP_SALES_VOLUME")
    private Double shopSalesVolum;

    @Parsed(field = "SHOP_SALES_AMOUNT")
    @JSONField(name = "SHOP_SALES_AMOUNT")
    private Double shopSalesAmount;

    @Parsed(field = "MAIN_BUSINESS")
    @JSONField(name = "MAIN_BUSINESS")
    private String mainBusiness;

    @Parsed(field = "BUSINESS_SCOPE")
    @JSONField(name = "BUSINESS_SCOPE")
    private String businessScope;

    @Parsed(field = "SHOP_PROVINCE")
    @JSONField(name = "SHOP_PROVINCE")
    private String shopProvince;

    @Parsed(field = "SHOP_CITY")
    @JSONField(name = "SHOP_CITY")
    private String shopCity;

    @Parsed(field = "SHOP_OPEN_DATE")
    @JSONField(name = "SHOP_OPEN_DATE")
    @Format(formats = {"yyyy-MM-DD"}, options = "locale=en;lenient=false")
    private Date openDate;

    @Parsed(field = "ITEMDESC_SCORE")
    @JSONField(name = "ITEMDESC_SCORE")
    private Double scoreItem;

    @Parsed(field = "SERVICE_SCORE")
    @JSONField(name = "SERVICE_SCORE")
    private Double scoreService;

    @Parsed(field = "DELIVERY_SCORE")
    @JSONField(name = "DELIVERY_SCORE")
    private Double scoreDelivery;

    @Parsed(field = "COMPANY_PROVINCE")
    @JSONField(name = "COMPANY_PROVINCE")
    private String compProvince;

    @Parsed(field = "COMPANY_CITY")
    @JSONField(name = "COMPANY_CITY")
    private String compCity;

    @Parsed(field = "COMPANY_COUNTY")
    @JSONField(name = "COMPANY_COUNTY")
    private String compCounty;

    @Parsed(field = "SHOP_DELIVERY_PROVINCE")
    @JSONField(name = "SHOP_DELIVERY_PROVINCE")
    private String deliverProvince;

    @Parsed(field = "SHOP_DELIVERY_CITY")
    @JSONField(name = "SHOP_DELIVERY_CITY")
    private String deliverCity;

    @Parsed(field = "SHOP_DELIVERY_COUNTY")
    @JSONField(name = "SHOP_DELIVERY_COUNTY")
    private String deliverCounty;


    @Override
    public boolean isNull() {
        return getUserID() == null || getUserID() <= 0;
    }
}
