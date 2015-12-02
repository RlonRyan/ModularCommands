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
import modcmd.user.CommandUser;

/**
 *
 * @author RlonRyan
 */
public class CommandUtil {

    @Command(
            name = "quit",
            about = "Quits the running application by calling system.exit()."
    )
    public static void execute(
            @CommandParameter(tag = "m", name = "message", description = "The reason for quitting.", type = "String", defaultValue = "Goodbye!") String message,
            @CommandParameter(tag = "v", name = "value", description = "The quit value", type = "int", defaultValue = "0") int value
    ) {
        System.out.printf("Quit: %1$s%n", message);
        System.exit(value);
    }

    @Command(
            name = "help",
            about = "Retrieves help for the nearest command."
    )
    public static void help(@CommandParameter(tag = "s", name = "subject", description = "The subject to get help on.", type = "String", defaultValue = "root") String message) {
        ArrayDeque<String> parts = new ArrayDeque<>(Arrays.asList(message.split("\\s+")));
        CommandNode node = CommandManager.getCommandSet("ct").getNearest(parts);
        if (node.identifier.equals("default") && node.parent != null) {
            node = node.parent;
        }
        for (String line : node.getHelp(new ArrayDeque<String>())) {
            System.out.println(line);
        }
    }

    @Command(
            name = "about",
            about = "Retrieves a description of a command."
    )
    public static void about(@CommandParameter(tag = "s", name = "subject", description = "The command to get a description for.", type = "cmd", defaultValue = "ct") CommandNode cmd) {
        if (cmd.command == null) {
            System.out.println();
            System.out.print(cmd.identifier);
            System.out.println(": A command set.");
        } else {
            System.out.println();
            System.out.print(cmd.identifier);
            System.out.print(": ");
            System.out.println(cmd.command.about());
        }
    }

    @Command(
            name = "suggest",
            about = "Suggests completion options for the provided line."
    )
    public static void suggest(@CommandParameter(tag = "l", name = "line", description = "The line to complete.", type = "String") String line) {
        ArrayDeque<String> args = new ArrayDeque<>(Arrays.asList(line.split("\\s+")));
        System.out.println(CommandManager.getCommandSet("ct").getNearest(args).suggestCompletion(args));
    }

    @Command(
            name = "beep",
            about = "Triggers the system to beep."
    )
    public static void beep() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    @Command(
            name = "mapify",
            about = "Uses the internal parser to break down the provide line into arguments."
    )
    public static void mapify(@CommandParameter(tag = "l", name = "line", description = "The line to mapify.", type = "String") String line) {
        System.out.println(CommandManager.mapify(new ArrayDeque<>(Arrays.asList(line.split("\\s+")))));
    }

    @Command(
            name = "repeat",
            about = "Repeats a command. This command is checked.",
            checked = true
    )
    public static void repeat(
            @CommandParameter(tag = "n", name = "loops", description = "The number of times to loop.", type = "Integer") Integer loops,
            @CommandParameter(tag = "c", name = "command", description = "The command to execute.", type = "String") String cmd
    ) {
        for (int i = 0; i < loops; i++) {
            CommandManager.execute("root", "ct", cmd, false);
        }
    }

    @Command(
            name = "sudo",
            about = "Executes a command as someone else.",
            checked = true
    )
    public static void sudo(
            @CommandParameter(tag = "u", name = "user", description = "The user to execute as.", type = "String", defaultValue = "root") String user,
            @CommandParameter(tag = "c", name = "command", description = "The command to execute.", type = "String") String cmd
    ) {
        for (String line : CommandManager.execute(user, "ct", cmd, false)) {
            System.out.println(line);
        }
    }

    @Command(
            name = "whoami",
            about = "Tells the user who he is."
    )
    public static void whoami(@CommandParameter(tag = "u", name = "user", description = "The user to find information on.", type = "User", defaultValue = "%") Object user) {
        System.out.println(user.toString());
    }

    @Command(
            name = "me",
            about = "Prints a message."
    )
    public static void me(
            @CommandUser Object user,
            @CommandParameter(tag = "m", name = "message", description = "The message to say.", type = "String") String message) {
        System.out.println(user.toString() + " " + message);
    }

}
