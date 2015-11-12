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
public class ConverterExeption extends Exception {

    /**
     * Creates a new instance of <code>ConverterExeption</code> without detail
     * message.
     */
    public ConverterExeption() {
    }

    /**
     * Constructs an instance of <code>ConverterExeption</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ConverterExeption(String msg) {
        super(msg);
    }
}
