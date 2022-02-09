package com.data.handle;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.beans.SimpleBeanInfo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConverter extends AbstractBeanField<SimpleBeanInfo, SimpleBeanInfo> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        Date value = null;

        if ( s != null && !"".equals( s ) ) {
            try {
                value =  new SimpleDateFormat( "yyyyMM" ).parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return value;
    }
}
