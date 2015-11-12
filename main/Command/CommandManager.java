/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command;

import Converter.ConverterExeption;
import Converter.ConverterManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static void execute(String line) {
        execute(line.split("\\s+"));
    }

    public static void execute(String... args) {
        execute(new ArrayDeque<>(Arrays.asList(args)));
    }

    public static void execute(ArrayDeque<String> args) {
        CommandNode node = ROOT_COMMAND_NODE.getNearest(args);
        if (node.command != null) {
            //System.out.printf("Commmand Node: %1$s%n - Args: %2$s%n", node.identifier, args.toString());\
            Deque<CommandParameter> params = new ArrayDeque<>();
            for (Annotation[] annos : node.command.getParameterAnnotations()) {
                for (Annotation anno : annos) {
                    if (anno instanceof CommandParameter) {
                        params.add((CommandParameter) anno);
                    }
                }
            }
            try {
                Object[] objargs = mapify(params, args.toString().replaceAll(",", ""));
                //System.out.println(Arrays.toString(objargs));
                node.command.invoke(null, objargs);
            } catch (CommandException | ConverterExeption ce) {
                Logger.getLogger(CommandManager.class.getCanonicalName()).log(Level.WARNING, ce.getMessage());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                System.out.println("Shoot.");
                ex.printStackTrace();
            } catch (NullPointerException except) {
                System.out.println("This is really bad...");
                except.printStackTrace();
            }
        } else {
            System.out.println(node.getHelp());
        }
    }

    public static final Object[] mapify(Deque<CommandParameter> argset, String args) throws CommandException, ConverterExeption {
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
            objargs[index++] = ConverterManager.getConverterFor(param.type()).convert(args.substring(loc, end));
        }
        return objargs;
    }

}
