/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter;

/**
 *
 * @author RlonRyan
 * @param <T> The type to convert to.
 */
public interface IConverter<T> {

    T convert(String parameter) throws ConverterExeption;

}
