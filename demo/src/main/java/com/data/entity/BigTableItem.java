package com.data.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class BigTableItem extends DataItem {


    @JSONField(name = "SHOP_SALES_VOLUME")
    private Double shopSalesVolum;


    @JSONField(name = "SHOP_SALES_AMOUNT")
    private Double shopSalesAmount;


    @JSONField(name = "SHOP_MAIN_BUSINESS")
    private String shopMainBusiness;


    @JSONField(name = "SHOP_BUSINESS_SCOPE")
    private String shopBusinessScope;


    @JSONField(name = "SHOP_PROVINCE")
    private String shopProvince;


    @JSONField(name = "SHOP_CITY")
    private String shopCity;


    @JSONField(name = "SHOP_OPEN_DATE")
    @Format(formats = {"yyyy-MM-DD"}, options = "locale=en;lenient=false")
    private Date shopOpenDate;


    @JSONField(name = "SHOP_ITEMDESC_SCORE")
    private Double shopScoreItem;


    @JSONField(name = "SHOP_SERVICE_SCORE")
    private Double shopScoreService;


    @JSONField(name = "SHOP_DELIVERY_SCORE")
    private Double shopScoreDelivery;


    @JSONField(name = "SHOP_COMPANY_PROVINCE")
    private String shopCompProvince;


    @JSONField(name = "SHOP_COMPANY_CITY")
    private String shopCompCity;


    @JSONField(name = "SHOP_COMPANY_COUNTY")
    private String shopCompCounty;


    @JSONField(name = "SHOP_DELIVERY_PROVINCE")
    private String shopDeliverProvince;


    @JSONField(name = "SHOP_DELIVERY_CITY")
    private String shopDeliverCity;


    @JSONField(name = "SHOP_DELIVERY_COUNTY")
    private String shopDeliverCounty;

    @Override
    public <T extends Item> void merger(T t) {
        Field[] sourceFields = t.getClass().getDeclaredFields();
        Field[] targetFields = this.getClass().getDeclaredFields();

        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            if (Modifier.isStatic(sourceField.getModifiers()))
                continue;

            Field targetField = targetFields[i];
            if (Modifier.isStatic(targetField.getModifiers()))
                continue;

            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (!(sourceField.get(t) == null) ) {
                    targetField.set(this, sourceField.get(t));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
