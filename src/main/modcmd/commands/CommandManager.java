/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.commands;

import modcmd.commands.exception.CommandException;
import modcmd.commands.exception.CommandMissingParameterException;
import modcmd.converters.ConverterManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import modcmd.permissions.PermissionManager;
import modcmd.permissions.exceptions.PermissionException;
import modcmd.user.CommandUser;

/**
 *
 * @author RlonRyan
 */
public final class CommandManager {

    public static final String ROOT_NAME = "root";
    public static final String DEFAULT_KEY = "default";

    public static final char MARKER = '-';
    public static final char ESCAPE = '\\';

    private static final CommandNode ROOT_COMMAND_NODE = new CommandNode("root");

    static {
        assert MARKER != ESCAPE;
        // Otherwise we have some pretty big issues...
    }

    public static Set<String> getCommandSetNames() {
        return ROOT_COMMAND_NODE.getSubnodes().keySet();
    }

    public static boolean hasCommandSet(String identifier) {
        return ROOT_COMMAND_NODE.subNodes.containsKey(identifier.toLowerCase());
    }

    public static CommandNode getCommandSet(String identifier) {
        if (!ROOT_COMMAND_NODE.subNodes.containsKey(identifier.toLowerCase())) {
            ROOT_COMMAND_NODE.registerSubset(identifier);
        }
        return ROOT_COMMAND_NODE.subNodes.get(identifier.toLowerCase());
    }

    public static Deque<String> execute(Object user, String cmdset, String line) {
        return execute(user, cmdset, line, true);
    }

    public static Deque<String> execute(Object user, String cmdset, String line, boolean checked) {
        return execute(user, cmdset, line.split("\\s+"), checked);
    }

    public static Deque<String> execute(Object user, String cmdset, String[] args) {
        return execute(user, cmdset, args, true);
    }

    public static Deque<String> execute(Object user, String cmdset, String[] args, boolean checked) {
        return execute(user, cmdset, new ArrayDeque<>(Arrays.asList(args)), checked);
    }

    public static Deque<String> execute(Object user, String cmdset, ArrayDeque<String> args) {
        return execute(user, cmdset, args, true);
    }

    public static Deque<String> execute(Object user, String cmdset, ArrayDeque<String> args, boolean checked) {

        Deque<String> lines = new ArrayDeque<>();

        CommandNode node = ROOT_COMMAND_NODE.subNodes.getOrDefault(cmdset, ROOT_COMMAND_NODE).getNearest(args);

        if (node.commandMethod == null) {
            lines.add("Command not found.");
            return lines;
        }

        // Check the user's permissions.
        if (checked && node.commandMethod.getAnnotation(Command.class).checked()) {
            try {
                if (PermissionManager.checkPermission(node.getFullIdentifier(), user) != 0) {
                    lines.add("Permission denied.");
                    return lines;
                }
            } catch (PermissionException e) {
                lines.add("Permission Handler Error.");
                lines.add(e.getLocalizedMessage());
                return lines;
            }
        }

        // Attempt to execute commandMethod.
        try {
            Object[] objargs = objectify(user, mapify(args), node.parameters);
            //System.out.println(Arrays.toString(objargs));
            Object result = node.commandMethod.invoke(null, objargs);
            if (result != null) {
                lines.add(result.toString());
            }
        } catch (CommandException ce) {
            lines.addAll(Arrays.asList(ce.getMessage().split("\\n+")));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, "Error!", ex);
            lines.add(ex.getClass().getName());
        } catch (NullPointerException ex) {
            Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.SEVERE, "Null pointer!", ex);
            lines.add("Null pointer!");
        }

        return lines;
    }

    public static Map<String, String> mapify(Collection<String> params) {

        // Craft Argument Map
        Map<String, String> argmap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String key = DEFAULT_KEY;

        // Iterate over tokens
        for (String e : params) {
            if (e.charAt(0) == MARKER && e.length() > 1) {
                if (sb.length() > 0) {
                    argmap.put(key, sb.toString());
                    sb = new StringBuilder();
                }
                key = e.substring(1);
            } else {
                if (e.length() > 0 && e.charAt(0) == ESCAPE) {
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

        // Return the argument map
        return argmap;
    }

    public static Object[] objectify(Object user, Map<String, String> args, Annotation... params) throws CommandException {

        Object[] objargs = new Object[params.length];
        boolean assigned = false;

        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof CommandUser) {
                objargs[i] = user;
            } else if (params[i] instanceof CommandParameter) {
                CommandParameter cp = (CommandParameter) params[i];
                if (args.containsKey(cp.tag())) {
                    objargs[i] = ConverterManager.convert(user, cp, args.get(cp.tag()));
                } else if (!assigned && !cp.defaultValue().startsWith("%") && args.containsKey(DEFAULT_KEY)) {
                    assigned = true;
                    objargs[i] = ConverterManager.convert(user, cp, args.get(DEFAULT_KEY));
                } else if (!cp.defaultValue().isEmpty()) {
                    objargs[i] = ConverterManager.convert(user, cp, cp.defaultValue());
                } else {
                    throw new CommandMissingParameterException(cp);
                }
            } else {
                throw new CommandException("Unknown parameter error.");
            }
        }

        return objargs;
    }

}
