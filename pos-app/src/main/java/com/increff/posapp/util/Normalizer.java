package com.increff.posapp.util;

import com.increff.posapp.model.*;
import com.increff.posapp.service.ApiException;

import java.lang.reflect.Field;

public class Normalizer {

    private Normalizer() {}

    public static void normalize(Object o) throws ApiException {
        Field[] fields = o.getClass().getDeclaredFields();
        for(Field field: fields){
            try{
                if(field.getType().getSimpleName().equals("String")){
                    field.setAccessible(true);
                    String val = (String) field.get(o);
                    field.set(o, val.toLowerCase());
                    field.setAccessible(false);
                }
                else if(field.getType().getSimpleName().equals("Double")){
                    field.setAccessible(true);
                    Double val = (Double) field.get(o);
                    field.set(o, DoubleUtil.round(val, 2));
                    field.setAccessible(false);
                }
            }
            catch (IllegalAccessException illegalArgumentException){
                throw new ApiException("An unknown error occurred");
            }

        }
    }
}
