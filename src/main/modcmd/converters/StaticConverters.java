/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.converters;

import modcmd.commands.CommandManager;
import modcmd.commands.CommandNode;
import modcmd.converters.exceptions.ConversionException;

/**
 *
 * @author RlonRyan
 */
public class StaticConverters {

    @Converter("integer")
    public static int convertInteger(Object user, String tag, String parameter) throws ConversionException {
        try {
            return Integer.decode(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("double")
    public static double convertDouble(Object user, String tag, String parameter) throws ConversionException {
        try {
            return Double.parseDouble(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("float")
    public static double convertFloat(Object user, String tag, String parameter) throws ConversionException {
        try {
            return Float.parseFloat(parameter);
        } catch (NumberFormatException ne) {
            throw new ConversionException(tag, parameter, "integer");
        }
    }

    @Converter("string")
    public static String convertString(Object user, String tag, String parameter) {
        return parameter;
    }

    @Converter("user")
    public static Object convertUser(Object user, String tag, String parameter) throws ConversionException {
        if (parameter.equalsIgnoreCase("%user%")) {
            return user;
        } else {
            throw new ConversionException(tag, parameter, "User");
        }
    }

    @Converter("command")
    public static CommandNode convertCommand(Object user, String tag, String parameter) throws ConversionException {
        String[] tokens = parameter.split("\\s+");
        if (tokens.length < 2) {
            throw new ConversionException(tag, parameter, "Command");
        }
        String command = parameter.substring(parameter.indexOf(' ') + 1);
        CommandNode node = CommandManager.getCommandSet(tokens[0]).getNearest(parameter.substring(parameter.indexOf(' ') + 1));
        if (!node.identifier.equalsIgnoreCase(command.substring(command.trim().lastIndexOf(' ') + 1))) {
            throw new ConversionException(tag, parameter, "Command");
        }
        return node;
    }

}
