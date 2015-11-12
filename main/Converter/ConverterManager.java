/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter;

import Command.CommandParameter;
import Converter.Default.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RlonRyan
 */
public final class ConverterManager {

    private static final Map<String, Method> converters;

    static {
        converters = new HashMap<>();
        addConverters(StaticConverters.class);
    }

    public static boolean hasConverterFor(String type) {
        return converters.containsKey(type.toLowerCase());
    }

    public static Object convert(CommandParameter parameter, String value) throws ConverterException {
        try {
            Method converter = converters.get(parameter.type().toLowerCase());
            if (converter == null) {
                throw new ConverterMissingExeption(value, parameter.type());
            }
            Object result = converter.invoke(null, parameter.tag(), value);
            return result == null ? converter.invoke(null, parameter.tag(), parameter.defaultValue()) : result;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ConversionException) {
                throw (ConversionException) e.getCause();
            } else {
                e.printStackTrace();
                throw new ConverterException(e.getLocalizedMessage());
            }
        } catch (IllegalAccessException | IllegalArgumentException exc) {
            exc.printStackTrace();
            throw new ConverterMissingExeption(value, parameter.type().toLowerCase());
        }
    }

    public static void addConverters(Class converterClass) {
        for (Method m : StaticConverters.class.getMethods()) {
            if (Modifier.isStatic(m.getModifiers()) && m.isAnnotationPresent(Converter.class)) {
                converters.putIfAbsent(m.getAnnotation(Converter.class).value().toLowerCase(), m);
            }
        }
    }

}
