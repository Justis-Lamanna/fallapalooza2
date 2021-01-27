package com.github.lucbui.birb;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserUtils {
    public static String getSingleValue(ValueRange range) {
        if(range == null) return null;
        List<List<Object>> contents = range.getValues();
        if(contents == null || contents.get(0) == null) {
            return null;
        }
        return (String)contents.get(0).get(0);
    }

    public static Integer getSingleValueInteger(ValueRange range, String field) {
        try {
            return getSingleValueInteger(range);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Error parsing field " + field + ": not a number", ex);
        }
    }

    private static Integer getSingleValueInteger(ValueRange range) {
        String str = getSingleValue(range);
        return str == null ? null : Integer.valueOf(str);
    }

    public static List<String> getMultiValue(ValueRange range, int size) {
        if(range == null) return Collections.nCopies(size, null);
        List<String> ret = new ArrayList<>();
        List<List<Object>> contents = range.getValues();
        if(contents == null) return Collections.nCopies(size, null);
        for(List<Object> rows : contents) {
            if(rows.isEmpty()) {
                ret.add(null);
            } else {
                ret.add((String)rows.get(0));
            }
        }
        if(ret.size() < size) {
            for(int i = 0; i < size - ret.size(); i++) {
                ret.add(null);
            }
        }
        return ret;
    }

    public static List<String> getMultiValueHorizontal(ValueRange range, int size) {
        if(range == null) return Collections.nCopies(size, null);
        List<String> ret = new ArrayList<>();
        List<List<Object>> contents = range.getValues();
        if(contents == null) return Collections.nCopies(size, null);
        List<Object> columns = contents.get(0);
        for(Object column : columns) {
            ret.add((String)column);
        }
        if(ret.size() < size) {
            for(int i = 0; i < size - ret.size(); i++) {
                ret.add(null);
            }
        }
        return ret;
    }

    private static List<Integer> getMultiValueInteger(ValueRange range, int size) {
        if(range == null) return Collections.nCopies(size, null);
        List<Integer> ret = new ArrayList<>();
        List<List<Object>> contents = range.getValues();
        if(contents == null) return Collections.nCopies(size, null);
        for(List<Object> rows : contents) {
            if(rows.isEmpty()) {
                ret.add(null);
            } else {
                ret.add(Integer.valueOf((String)rows.get(0)));
            }
        }
        if(ret.size() < size) {
            for(int i = 0; i < size - ret.size(); i++) {
                ret.add(null);
            }
        }
        return ret;
    }

    public static List<Integer> getMultiValueInteger(ValueRange range, int size, String field) {
        try {
            return getMultiValueInteger(range, size);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Error parsing field " + field + ": not a number", ex);
        }
    }
}
