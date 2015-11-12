/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter;

/**
 *
 * @author RlonRyan
 */
public final class ConverterMissingExeption extends ConverterException {

    private static final String format = "Unable to convert parameter %2$s:%1$s\n\t- %2$s is not a valid type.";

    /**
     * Constructs an instance of <code>ConverterException</code> with the
     * specified detail message.
     *
     */
    public ConverterMissingExeption(String value, String type) {
        super(String.format(format, value, type));
    }

}
