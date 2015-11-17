package modcmd.example;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import modcmd.example.commands.print.CommandPrint;
import modcmd.example.commands.CommandUtil;
import modcmd.commands.CommandManager;
import modcmd.documents.CommandPageGenerator;
import java.util.Scanner;

/**
 *
 * @author RlonRyan
 */
public class CommandTerminal {

    static {
        CommandManager.getCommandSet("ct").registerCommand(CommandUtil.class);
        CommandManager.getCommandSet("ct").registerSubset("print");
        CommandManager.getCommandSet("ct").getNearest("print").registerCommand(CommandPrint.class);
        CommandManager.getCommandSet("ct").registerCommand(CommandPageGenerator.class);
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        while (true) {
            String result = CommandManager.execute("root", "ct", in.nextLine(), false);
            if (result != null) {
                System.out.printf("Result: %1$s%n", result);
            }
        }
    }

}