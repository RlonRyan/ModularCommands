/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 *
 * @author RlonRyan
 */
public final class CommandManager {

    private static final CommandNode ROOT_COMMAND_NODE;

    static {
        ROOT_COMMAND_NODE = new CommandNode("default");
    }

    public static boolean addCommandSet(String identifier) {
        return ROOT_COMMAND_NODE.registerSubset(identifier);
    }

    public static CommandNode getCommandSet(String identifier) {
        return ROOT_COMMAND_NODE.subNodes.getOrDefault(identifier, ROOT_COMMAND_NODE);
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
            //System.out.printf("Commmand Node: %1$s%n - Args: %2$s%n", node.identifier, args.toString());
            node.command.execute(args);
        } else {
            System.out.println(node.getHelp());
        }
    }

}
