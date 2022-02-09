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
public class Item {

    @Parsed(field = "DATA_MONTH")
    @JSONField(name = "DATA_MONTH")
//    @Format(formats = {"yyyyMM"}, options = "locale=en;lenient=false")
    String month;

    @Parsed(field = "USER_ID")
    @JSONField(name = "USER_ID")
    Long userID;

    @Parsed(field = "SHOP_NAME")
    @JSONField(name = "SHOP_NAME")
    String shopName;

    public boolean isNull() {
        return getUserID() == null || getUserID() <= 0;
    }

    public <T extends Item> void merger( T t ) {

    }
}
