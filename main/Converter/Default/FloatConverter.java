/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter.Default;

import Converter.ConverterExeption;
import Converter.IConverter;

/**
 *
 * @author RlonRyan
 */
public class FloatConverter implements IConverter<Float> {

    @Override
    public Float convert(String parameter) throws ConverterExeption {
        try {
            return Float.parseFloat(parameter);
        } catch (NumberFormatException ne) {
            throw new ConverterExeption("Parameter: " + parameter + " is not a valid number.");
        }
    }

}
