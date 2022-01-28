package com.data.handle;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.beans.SimpleBeanInfo;
import java.lang.reflect.Field;

public class NumberConverter extends AbstractBeanField<SimpleBeanInfo, SimpleBeanInfo> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {

        Field field = getField();
        if( field.getClass().equals(Long.class) ) {
            return Long.parseLong( s );
        }

        if( field.getClass().equals(Double.class) ) {
            return Double.parseDouble( s );
        }

        return s;
    }
}
