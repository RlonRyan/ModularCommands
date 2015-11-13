/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Command.Default.*;
import Command.Print.*;
import commands.CommandManager;
import java.util.Scanner;

/**
 *
 * @author RlonRyan
 */
public class CommandTerminal {

    static {
        CommandManager.getCommandSet("").registerCommand(CommandUtil.class);
        CommandManager.getCommandSet("print").registerCommand(CommandPrint.class);
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        while (true) {
            String result = CommandManager.execute("root", in.nextLine());
            if (result != null) {
                System.out.printf("Issue: %1$s%n", result);
            }
        }
    }

}
