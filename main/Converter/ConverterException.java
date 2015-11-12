/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Converter;

import Command.CommandException;

/**
 *
 * @author RlonRyan
 */
public class ConverterException extends CommandException {

    /**
     * Creates a new instance of <code>ConverterException</code> without detail
     * message.
     */
    public ConverterException() {
    }

    /**
     * Constructs an instance of <code>ConverterException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ConverterException(String msg) {
        super(msg);
    }

}
