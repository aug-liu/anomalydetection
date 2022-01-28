package com.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


public interface Item {

    public default boolean isNull() {
         return false;
    }
}
