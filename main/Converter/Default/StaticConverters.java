/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter.Default;

import Converter.Converter;
import Converter.ConversionException;

/**
 *
 * @author RlonRyan
 */
public class StaticConverters {

    @Converter("integer")
    public static int convertInteger(String tag, String parameter) throws ConversionException {
        try {
            return Integer.decode(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("double")
    public static double convertDouble(String tag, String parameter) throws ConversionException {
        try {
            return Double.parseDouble(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("float")
    public double convertFloat(String tag, String parameter) throws ConversionException {
        try {
            return Float.parseFloat(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("string")
    public static String convertString(String tag, String parameter) {
        return parameter;
    }

}
