/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import commands.exception.CommandException;
import converters.ConverterManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;
import permissions.PermissionManager;
import permissions.exceptions.PermissionException;

/**
 *
 * @author RlonRyan
 */
public final class CommandManager {

    private static final CommandNode ROOT_COMMAND_NODE;

    static {
        ROOT_COMMAND_NODE = new CommandNode("Root");
    }

    public static CommandNode getCommandSet(String identifier) {
        if (!ROOT_COMMAND_NODE.subNodes.containsKey(identifier.toLowerCase())) {
            ROOT_COMMAND_NODE.registerSubset(identifier);
        }
        return ROOT_COMMAND_NODE.subNodes.get(identifier);
    }

    public static String execute(String user, String line) {
        return execute(user, line.split("\\s+"));
    }

    public static String execute(String user, String... args) {
        return execute(user, new ArrayDeque<>(Arrays.asList(args)));
    }

    public static String execute(String user, ArrayDeque<String> args) {

        CommandNode node = ROOT_COMMAND_NODE.getNearest(args);

        if (node.command != null) {
            //System.out.printf("Commmand Node: %1$s%n - Args: %2$s%n", node.identifier, args.toString());\

            // Check the user's permissions.
            if (node.command.getAnnotation(Command.class).checked() && !"root".equalsIgnoreCase(user)) {
                try {
                    if (PermissionManager.checkPermission(node.getFullIdentifier(), user) != 0) {
                        return "Permission denied.";
                    }
                } catch (PermissionException e) {
                    return String.format("Command Permission Issue:%n%t- User: %1$s%n%t- Permission: %2$s%n%t- Reason: %3$s%n", user, node.getFullIdentifier(), e.getMessage());
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
                Object[] objargs = mapify(params, args.toString().replaceAll(",", ""));
                //System.out.println(Arrays.toString(objargs));
                Object result = node.command.invoke(null, objargs);
                return result == null ? null : result.toString();
            } catch (CommandException ce) {
                return ce.getMessage();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage());
                return ex.getLocalizedMessage();
            } catch (NullPointerException ex) {
                Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage());
                return "Null pointer!";
            }
        } else {
            System.out.println(node.getHelp());
            return null;
        }
    }

    public static final Object[] mapify(Deque<CommandParameter> argset, String args) throws CommandException {
        Object[] objargs = new Object[argset.size()];
        int index = 0;
        for (CommandParameter param : argset) {
            int loc = args.indexOf(param.tag() + ":") + param.tag().length() + 1;
            char delim = ' ';
            if (args.charAt(loc) == '\"') {
                delim = '\"';
                loc++;
            }
            int end = args.indexOf(delim, loc);
            end = end < loc ? args.length() - 1 : end;
            end = end < 0 ? 0 : end;
            //System.out.println(param.toString());
            String arg = args.substring(loc, end).trim();
            arg = arg.isEmpty() ? param.defaultValue() : arg;
            objargs[index++] = ConverterManager.convert(param, arg);
        }
        return objargs;
    }

}
