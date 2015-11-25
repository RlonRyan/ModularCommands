/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.commands.validators;

import modcmd.commands.Command;
import modcmd.commands.CommandManager;
import modcmd.commands.CommandParameter;
import modcmd.converters.ConverterManager;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modcmd.user.CommandUser;

/**
 *
 * @author RlonRyan
 */
public final class CommandValidator {

    private static final Logger LOGGER = Logger.getLogger(CommandManager.class.getCanonicalName());

    public static final String INVALID_COMMAND_HEADER = "\nInvalid Command Method: %1$s\n";
    public static final String COMMAND_MISSING_ANNOTATION = "\t- Missing Annotation: %1$s\n\t- This is really bad! There might be a core error!\n";
    public static final String COMMAND_MISSING_MODIFIER = "\t- Missing Modifier: %1$s\n";
    public static final String COMMAND_BAD_NAME = "\t- Bad Name: \"%1$s\"\n";
    public static final String INVALID_PARAMETER_HEADER = "\t- Invalid Parameter: %1$s\n";
    public static final String PARAMETER_MISSING_ANNOTATION = "\t\t- Missing Annotation: %1$s\n";
    public static final String PARAMETER_MISSING_CONVERTER = "\t\t- Missing Converter For: %1$s\n";

    public static boolean validate(Method m) {

        StringBuilder sb = new StringBuilder();

        if (m.getAnnotation(Command.class) == null) {
            sb.append(String.format(INVALID_COMMAND_HEADER, m.getName()));
            sb.append(String.format(COMMAND_MISSING_MODIFIER, Command.class.getName()));
            LOGGER.log(Level.SEVERE, sb.toString());
            return false;
        }

        if (m.getAnnotation(Command.class).value().isEmpty()) {
            sb.append(String.format(INVALID_COMMAND_HEADER, m.getName()));
            sb.append(String.format(COMMAND_BAD_NAME, m.getAnnotation(Command.class).value()));
            LOGGER.log(Level.SEVERE, sb.toString());
            return false;
        }

        final boolean isPublic = Modifier.isPublic(m.getModifiers());
        final boolean isStatic = Modifier.isStatic(m.getModifiers());

        if (!(isPublic && isStatic)) {
            sb.append(String.format(INVALID_COMMAND_HEADER, m.getName()));
            if (!isPublic) {
                sb.append(String.format(COMMAND_MISSING_MODIFIER, "public"));
            }
            if (!isStatic) {
                sb.append(String.format(COMMAND_MISSING_MODIFIER, "static"));
            }
            LOGGER.log(Level.SEVERE, sb.toString());
            return false;
        }

        List<Parameter> invalidParams = new ArrayList<>();

        for (Parameter p : m.getParameters()) {
            CommandParameter annotation = p.getAnnotation(CommandParameter.class);
            if (annotation == null) {
                if (p.getAnnotation(CommandUser.class) == null) {
                    invalidParams.add(p);
                }
            } else if (!ConverterManager.hasConverterFor(annotation.type())) {
                invalidParams.add(p);
            }
        }

        if (invalidParams.size() > 0) {
            sb.append(String.format(INVALID_COMMAND_HEADER, m.getName()));

            for (Parameter p : invalidParams) {
                sb.append(String.format(INVALID_PARAMETER_HEADER, m.getName()));
                CommandParameter annotation = p.getAnnotation(CommandParameter.class);
                if (annotation == null) {
                    if (p.getAnnotation(CommandUser.class) == null) {
                        sb.append(String.format(PARAMETER_MISSING_ANNOTATION, CommandParameter.class.getName()));
                    } else {
                        sb.append(String.format("This is really odd..."));
                    }
                } else if (!ConverterManager.hasConverterFor(annotation.type())) {
                    sb.append(String.format(PARAMETER_MISSING_CONVERTER, annotation.type()));
                } else {
                    sb.append(String.format("This is really odd..."));
                }

            }
            LOGGER.log(Level.SEVERE, sb.toString());
            return false;
        }

        // It survived!
        return true;
    }

}
