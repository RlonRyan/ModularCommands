/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.example.commands;

import modcmd.commands.Command;
import modcmd.commands.CommandManager;
import modcmd.commands.CommandNode;
import modcmd.commands.CommandParameter;
import java.util.ArrayDeque;
import java.util.Arrays;

/**
 *
 * @author RlonRyan
 */
public class CommandUtil {

    @Command("quit")
    public static void execute(@CommandParameter(tag = "m", name = "message", description = "The reason for quitting.", type = "String", defaultValue = "Goodbye!") String message) {
        System.out.printf("Quit: %1$s%n", message);
        System.exit(0);
    }

    @Command("help")
    public static void help(@CommandParameter(tag = "s", name = "subject", description = "The subject to get help on.", type = "String", defaultValue = "root") String message) {
        ArrayDeque<String> parts = new ArrayDeque<>(Arrays.asList(message.split("\\s+")));
        CommandNode node = CommandManager.getCommandSet("ct").getNearest(parts);
        if (node.identifier.equals("default") && node.parent != null) {
            node = node.parent;
        }
        System.out.println(node.getHelp());
    }

    @Command("suggest")
    public static void suggest(@CommandParameter(tag = "l", name = "line", description = "The line to complete.", type = "String") String line) {
        ArrayDeque<String> args = new ArrayDeque<>(Arrays.asList(line.split("\\s+")));
        System.out.println(CommandManager.getCommandSet("ct").getNearest(args).suggestCompletion(args));
    }

    @Command("beep")
    public static void beep() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    @Command("mapify")
    public static void mapify(@CommandParameter(tag = "l", name = "line", description = "The line to mapify.", type = "String") String line) {
        System.out.println(CommandManager.mapify(new ArrayDeque<>(Arrays.asList(line.split("\\s+")))));
    }

    @Command(value = "repeat", checked = true)
    public static void repeat(
            @CommandParameter(tag = "n", name = "loops", description = "The number of times to loop.", type = "Integer") Integer loops,
            @CommandParameter(tag = "c", name = "command", description = "The command to execute.", type = "String") String cmd
    ) {
        for (int i = 0; i < loops; i++) {
            CommandManager.execute("root", "ct", cmd, false);
        }
    }

    @Command(value = "sudo", checked = true)
    public static String sudo(
            @CommandParameter(tag = "u", name = "user", description = "The user to execute as.", type = "String", defaultValue = "root") String user,
            @CommandParameter(tag = "c", name = "command", description = "The command to execute.", type = "String") String cmd
    ) {
        return CommandManager.execute(user, "ct", cmd, false);
    }

    @Command("whois")
    public static void whois(@CommandParameter(tag = "u", name = "user", description = "The user to find information on.", type = "User", defaultValue = "%user%") Object user) {
        System.out.println(user.toString());
    }

    @Command("me")
    public static void me(
            @CommandParameter(tag = "u", name = "user", description = "The user to find information on.", type = "User", defaultValue = "%user%") Object user,
            @CommandParameter(tag = "m", name = "message", description = "The message to say.", type = "String", defaultValue = "NOPE") String message) {
        System.out.println(user.toString() + " " + message);
    }

}
