/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.converters;

import modcmd.converters.exceptions.ConversionException;
import modcmd.converters.exceptions.ConverterException;
import modcmd.converters.exceptions.ConverterMissingExeption;
import modcmd.commands.CommandParameter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        for (Method m : converterClass.getMethods()) {
            if (m.isAnnotationPresent(Converter.class)) {
                if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    converters.putIfAbsent(m.getAnnotation(Converter.class).value().toLowerCase(), m);
                } else {
                    Logger.getLogger(ConverterManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} is not public static!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                }
            }
        }
    }

}
