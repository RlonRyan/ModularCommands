/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands.validators;

import commands.Command;
import commands.CommandManager;
import commands.CommandParameter;
import converters.ConverterManager;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RlonRyan
 */
public final class CommandValidator {

    private static final Logger logger = Logger.getLogger(CommandManager.class.getCanonicalName());

    public static final String INVALID_COMMAND_HEADER = "Invalid Command Method: {0}";
    public static final String COMMAND_MISSING_ANNOTATION = "\t- Missing Annotation: {0}\n\t- This is really bad! There might be a core error!";
    public static final String COMMAND_MISSING_MODIFIER = "\t- Missing Modifier: {0}";
    public static final String COMMAND_BAD_NAME = "\t- Bad Name: \"{0}\"";
    public static final String INVALID_PARAMETER_HEADER = "\t- Invalid Parameter: {0}";
    public static final String PARAMETER_MISSING_ANNOTATION = "\t\t- Missing Annotation: {0}";
    public static final String PARAMETER_MISSING_CONVERTER = "\t\t- Missing Converter For: {0}";

    public static boolean validate(Method m) {

        if (m.getAnnotation(Command.class) == null) {
            logger.log(Level.SEVERE, INVALID_COMMAND_HEADER, m.getName());
            logger.log(Level.SEVERE, COMMAND_MISSING_ANNOTATION, Command.class.getName());
            return false;
        }

        if (m.getAnnotation(Command.class).value().isEmpty()) {
            logger.log(Level.SEVERE, INVALID_COMMAND_HEADER, m.getName());
            logger.log(Level.SEVERE, COMMAND_BAD_NAME, m.getAnnotation(Command.class).value());
            return false;
        }

        final boolean isPublic = Modifier.isPublic(m.getModifiers());
        final boolean isStatic = Modifier.isStatic(m.getModifiers());

        if (!(isPublic && isStatic)) {
            logger.log(Level.SEVERE, INVALID_COMMAND_HEADER, m.getName());
            if (!isPublic) {
                logger.log(Level.SEVERE, COMMAND_MISSING_MODIFIER, "public");
            }
            if (!isStatic) {
                logger.log(Level.SEVERE, COMMAND_MISSING_MODIFIER, "static");
            }
            return false;
        }

        List<Parameter> invalidParams = new ArrayList<>();

        for (Parameter p : m.getParameters()) {
            CommandParameter annotation = p.getAnnotation(CommandParameter.class);
            if (annotation == null) {
                invalidParams.add(p);
            } else if (!ConverterManager.hasConverterFor(annotation.type())) {
                invalidParams.add(p);
            }
        }

        if (invalidParams.size() > 0) {
            logger.log(Level.SEVERE, INVALID_COMMAND_HEADER, m.getName());
            for (Parameter p : invalidParams) {
                logger.log(Level.SEVERE, INVALID_PARAMETER_HEADER, p.getName());
                CommandParameter annotation = p.getAnnotation(CommandParameter.class);
                if (annotation == null) {
                    logger.log(Level.SEVERE, PARAMETER_MISSING_ANNOTATION, CommandParameter.class.getName());
                } else if (!ConverterManager.hasConverterFor(annotation.type())) {
                    logger.log(Level.SEVERE, PARAMETER_MISSING_CONVERTER, annotation.type());
                } else {
                    logger.log(Level.SEVERE, "This is really odd...");
                }

            }
            return false;
        }

        // It survived!
        return true;
    }

}
