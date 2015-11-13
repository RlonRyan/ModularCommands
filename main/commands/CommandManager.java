/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import commands.exception.CommandException;
import commands.exception.CommandMissingParameterException;
import converters.ConverterManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import permissions.PermissionManager;
import permissions.exceptions.PermissionException;

/**
 *
 * @author RlonRyan
 */
public final class CommandManager {

    public static final String ROOT_NAME = "root";
    public static final String DEFAULT_KEY = "default";

    public static final char MARKER_CHAR = '-';
    public static final char ESCAPE_CHAR = '\\';

    private static final CommandNode ROOT_COMMAND_NODE = new CommandNode("root");

    static {
        assert MARKER_CHAR != ESCAPE_CHAR;
        // Otherwise we have some pretty big issues...
    }

    public static CommandNode getCommandSet(String identifier) {
        if (!ROOT_COMMAND_NODE.subNodes.containsKey(identifier.toLowerCase())) {
            ROOT_COMMAND_NODE.registerSubset(identifier);
        }
        return ROOT_COMMAND_NODE.subNodes.get(identifier.toLowerCase());
    }

    public static String execute(String user, String cmdset, String line) {
        return execute(user, cmdset, line, true);
    }

    public static String execute(String user, String cmdset, String line, boolean checked) {
        return execute(user, cmdset, line.split("\\s+"), checked);
    }

    public static String execute(String user, String cmdset, String[] args) {
        return execute(user, cmdset, args, true);
    }

    public static String execute(String user, String cmdset, String[] args, boolean checked) {
        return execute(user, cmdset, new ArrayDeque<>(Arrays.asList(args)), checked);
    }

    public static String execute(String user, String cmdset, ArrayDeque<String> args) {
        return execute(user, cmdset, args, true);
    }

    public static String execute(String user, String cmdset, ArrayDeque<String> args, boolean checked) {

        CommandNode node = ROOT_COMMAND_NODE.subNodes.getOrDefault(cmdset, ROOT_COMMAND_NODE).getNearest(args);

        if (node.command != null) {
            //System.out.printf("Commmand Node: %1$s%n - Args: %2$s%n", node.identifier, args.toString());\

            // Check the user's permissions.
            if (checked && node.command.getAnnotation(Command.class).checked()) {
                try {
                    if (PermissionManager.checkPermission(node.getFullIdentifier(), user) != 0) {
                        return "Permission denied.";
                    }
                } catch (PermissionException e) {
                    return e.getMessage();
                }
            }

            // Get Parameters
            Deque<CommandParameter> params = new ArrayDeque<>();
            for (Annotation[] annos : node.command.getParameterAnnotations()) {
                for (Annotation anno : annos) {
                    if (anno instanceof CommandParameter) {
                        params.add((CommandParameter) anno);
                    }
                }
            }

            // Attempt to execute command.
            try {
                Object[] objargs = objectify(params, mapify(args));
                //System.out.println(Arrays.toString(objargs));
                Object result = node.command.invoke(null, objargs);
                return result == null ? null : result.toString();
            } catch (CommandException ce) {
                return ce.getMessage();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, "Error!", ex);
                return ex.getClass().getName();
            } catch (NullPointerException ex) {
                Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, "Null pointer!", ex);
                return "Null pointer!";
            }
        } else {
            System.out.println(node.getHelp());
            return null;
        }
    }

    public static Map<String, String> mapify(Collection<String> params) {
        //System.out.printf("Mapifying...%n\tParams: %1$s%n\tDefault Tag: \"%2$s\"%n", params.toString(), "");
        Map<String, String> argmap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String key = DEFAULT_KEY;
        for (String e : params) {
            if (e.charAt(0) == MARKER_CHAR && e.length() > 1) {
                if (sb.length() > 0) {
                    argmap.put(key, sb.toString());
                    sb = new StringBuilder();
                }
                key = e.substring(1);
            } else {
                if (e.length() > 0 && e.charAt(0) == ESCAPE_CHAR) {
                    e = e.substring(1);
                }
                if (sb.length() < 1) {
                    sb.append(e.trim());
                } else {
                    sb.append(" ").append(e.trim());
                }
            }
        }

        // Put remaining stuff.
        if (sb.length() > 0) {
            argmap.put(key, sb.toString());
        }

        //System.out.println(argmap.toString());
        return argmap;
    }

    public static Object[] objectify(Deque<CommandParameter> params, Map<String, String> args) throws CommandException {
        Object[] objargs = new Object[params.size()];
        int index = 0;

        //System.out.printf("Size: %1$s%nContains Default: %2$b%n", argset.size(), args.containsKey(""));
        if (params.size() == 1 && !args.containsKey(params.peek().tag()) && args.containsKey(DEFAULT_KEY)) {
            CommandParameter param = params.pop();
            //System.out.printf("Default Key Value: %1$s%n\t - Default value: \"%2$s\"%n", args.getOrDefault(DEFAULT_KEY, param.defaultValue()), param.defaultValue());
            objargs[0] = ConverterManager.convert(param, args.getOrDefault(DEFAULT_KEY, param.defaultValue()));
            return objargs;
        }

        for (CommandParameter param : params) {
            if ((!param.defaultValue().isEmpty()) || args.containsKey(param.tag())) {
                objargs[index++] = ConverterManager.convert(param, args.getOrDefault(param.tag(), param.defaultValue()));
            } else {
                throw new CommandMissingParameterException(param);
            }
        }

        return objargs;
    }

}
