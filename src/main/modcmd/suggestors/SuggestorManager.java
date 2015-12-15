/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.suggestors;

import modcmd.converters.exceptions.ConversionException;
import modcmd.converters.exceptions.ConverterException;
import modcmd.converters.exceptions.ConverterMissingExeption;
import modcmd.commands.CommandParameter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RlonRyan
 */
public final class SuggestorManager {

    private static final Map<String, Method> suggestors;

    static {
        suggestors = new HashMap<>();
        addSuggestors(StaticSuggestors.class);
    }

    public static boolean hasSuggestorFor(String type) {
        return suggestors.containsKey(type.toLowerCase());
    }

    public static void Suggest(String parameter, String value, List<String> options) {
        try {
            Method suggestor = suggestors.get(parameter.toLowerCase());
            if (suggestor == null) {
                // Do nothing atm.
                System.out.println("No such suggestor.");
            } else {
                suggestor.invoke(null, parameter, value, options);
            }
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException exc) {
            Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.WARNING, "Suggestor Issue!", exc);
        }
    }

    public static void addSuggestors(Class suggestorClass) {
        for (Method m : suggestorClass.getMethods()) {
            if (m.isAnnotationPresent(Suggestor.class)) {
                if (!(Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()))) {
                    Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} is not public static!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    continue;
                }
                if (m.getParameterTypes().length != 3) {
                    Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} does not accept proper number of arguments!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    continue;
                }
                if (m.getParameterTypes()[0] != String.class) {
                    Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} does not accept proper type for argument 2!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    continue;
                }
                if (m.getParameterTypes()[1] != String.class) {
                    Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} does not accept proper type for argument 3!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    continue;
                }
                if (m.getParameterTypes()[2] != List.class) {
                    Logger.getLogger(SuggestorManager.class.getCanonicalName()).log(Level.SEVERE, "Converter Method: {1}.{0} does not accept proper type for argument 3!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    continue;
                }
                for (String type : m.getAnnotation(Suggestor.class).value()) {
                    suggestors.putIfAbsent(type, m);
                }
            }
        }
    }

}
