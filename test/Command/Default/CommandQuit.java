/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Default;

import Command.CommandParameter;
import Command.Command;

/**
 *
 * @author RlonRyan
 */
public class CommandQuit {

    @Command("quit")
    public static void execute(@CommandParameter(tag = "m", name = "message", type = "String") String message) {
        System.out.printf("Quit: %1$s%n", message);
        //beep();
        System.exit(0);
    }

    @Command("beep")
    public static void beep() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
