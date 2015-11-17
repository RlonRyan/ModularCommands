/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.commands.exception;

import modcmd.commands.CommandParameter;

/**
 *
 * @author RlonRyan
 */
public class CommandMissingParameterException extends CommandException {

    public static final String MESSAGE_FORMAT = "Missing required parameter: -%1$s [%2$s]%n\t- Description: %3$s%n\t- Type: %4$s";

    /**
     * Constructs an instance of <code>CommandException</code> with the
     * specified detail message.
     *
     * @param p the command parameter that is missing.
     */
    public CommandMissingParameterException(CommandParameter p) {
        super(String.format(MESSAGE_FORMAT, p.tag(), p.name(), p.description(), p.type()));
    }
}
