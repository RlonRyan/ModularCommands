/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Command.CommandManager;
import Command.Default.*;
import Command.Print.*;
import java.util.Scanner;

/**
 *
 * @author RlonRyan
 */
public class CommandTerminal {

    static {
        CommandManager.getCommandSet("").registerCommand(CommandQuit.class);
        CommandManager.getCommandSet("print").registerCommand(CommandPrint.class);
        CommandManager.getCommandSet("util").registerCommand(CommandUtil.class);
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        while (true) {
            String result = CommandManager.execute(in.nextLine());
            if (result != null) {
                System.out.printf("Issue: %1$s%n", result);
            }
        }
    }

}
