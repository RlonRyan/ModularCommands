/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter;

import Converter.Default.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RlonRyan
 */
public class ConverterManager {

    private static final Map<String, IConverter> converters;

    static {
        converters = new HashMap<>();
        converters.put("integer", new IntegerConverter());
        converters.put("double", new DoubleConverter());
        converters.put("float", new FloatConverter());
        converters.put("string", new StringConverter());
        converters.put("", new StringConverter());
    }

    public static boolean hasConverterFor(String type) {
        return converters.containsKey(type.toLowerCase());
    }

    public static IConverter getConverterFor(String type) {
        return converters.get(type.toLowerCase());
    }

    public static IConverter addConverterFor(String type, IConverter converter) {
        return converters.putIfAbsent(type.toLowerCase(), converter);
    }

}
