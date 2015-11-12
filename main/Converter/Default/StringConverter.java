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
public class StringConverter implements IConverter<String> {

    @Override
    public String convert(String parameter) throws ConverterExeption {
        return parameter;
    }

}
