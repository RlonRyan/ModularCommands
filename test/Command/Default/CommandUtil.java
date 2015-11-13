/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Default;

import commands.Command;
import commands.CommandManager;
import commands.CommandParameter;

/**
 *
 * @author RlonRyan
 */
public class CommandUtil {

    @Command("quit")
    public static void execute(@CommandParameter(tag = "m", name = "message", type = "String", defaultValue = "Goodbye!") String message) {
        System.out.printf("Quit: %1$s%n", message);
        System.exit(0);
    }

    @Command("beep")
    public static void beep() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    @Command(value = "repeat", checked = true)
    public static void repeat(
            @CommandParameter(tag = "n", name = "loops", type = "Integer") Integer loops,
            @CommandParameter(tag = "c", name = "command", type = "String") String cmd
    ) {
        for (int i = 0; i < loops; i++) {
            CommandManager.execute("root", cmd);
        }
    }

    @Command(value = "sudo", checked = true)
    public static void sudo(
            @CommandParameter(tag = "u", name = "user", type = "String") String user,
            @CommandParameter(tag = "c", name = "command", type = "String") String cmd
    ) {
        CommandManager.execute(user, cmd);
    }

}
