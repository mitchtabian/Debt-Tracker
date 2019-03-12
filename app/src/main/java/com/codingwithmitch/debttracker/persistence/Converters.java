package com.codingwithmitch.debttracker.persistence;

import com.codingwithmitch.debttracker.models.Person;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;


import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public BigDecimal fromLong(Long value) {
        return value == null ? null : new BigDecimal(value).divide(new BigDecimal(100));
    }

    @TypeConverter
    public Long toLong(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        } else {
            return bigDecimal.multiply(new BigDecimal(100)).longValue();
        }
    }

    @TypeConverter
    public static Person fromStringToPerson(String value){
        Type personType = new TypeToken<Person>(){}.getType();
        return new Gson().fromJson(value, personType);
    }


    @TypeConverter
    public static String fromPerson(Person person){
        Gson gson = new Gson();
        String json = gson.toJson(person);
        return json;
    }
}
