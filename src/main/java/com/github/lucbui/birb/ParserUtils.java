package com.github.lucbui.birb;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParserUtils {
    public static List<String> flatten(ValueRange range) {
        if(range == null) return null;
        List<List<Object>> contents = range.getValues();
        List<String> flattenedContents = new ArrayList<>();
        if(contents != null) {
            for(List<Object> subList : contents) {
                if(subList != null) {
                    for(Object obj : subList) {
                        flattenedContents.add(Objects.toString(obj, null));
                    }
                }
            }
        }
        return flattenedContents;
    }

    public static String getSingleValue(ValueRange range) {
        List<String> flattened = flatten(range);
        if(flattened.isEmpty()) {
            return null;
        }
        return flattened.get(0);
    }

    public static Integer getSingleValueInteger(ValueRange range) {
        String strValue = getSingleValue(range);
        return strValue == null ? null : Integer.valueOf(strValue);
    }

    public static List<String> getMultiValue(ValueRange range) {
        return flatten(range);
    }

    public static List<Integer> getMultiValueInteger(ValueRange range) {
        List<String> strValue = getMultiValue(range);
        return strValue == null ? null : strValue.stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    public static <X> List<X> pad(List<X> list, int size, X padWith) {
        if(list.size() == size) {
            return list;
        } else if(list.size() > size) {
            return list.subList(0, size);
        } else {
            while(list.size() < size) {
                list.add(padWith);
            }
            return list;
        }
    }
}
