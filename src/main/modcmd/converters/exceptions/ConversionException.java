/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.converters.exceptions;

/**
 *
 * @author RlonRyan
 */
public class ConversionException extends ConverterException {

    private static final String format = "Unable to convert parameter %1$s:%2$s\n\t- %2$s is not a valid %3$s";

    /**
     * Constructs an instance of <code>ConverterExeption</code> with the
     * specified detail message.
     *
     */
    public ConversionException(String tag, String value, String type) {
        super(String.format(format, tag, value, type));
    }

}
