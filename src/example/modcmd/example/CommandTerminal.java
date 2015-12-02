package modcmd.example;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Deque;
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
        CommandManager.getCommandSet("ct").registerCommands(CommandUtil.class);
        CommandManager.getCommandSet("ct").registerSubset("print");
        CommandManager.getCommandSet("ct").getNearest("print").registerCommands(CommandPrint.class);
        CommandManager.getCommandSet("ct").registerCommands(CommandPageGenerator.class);
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println("Command Termial");
        System.out.println("====================");
        System.out.println();
        final Scanner in = new Scanner(System.in);
        while (true) {
            Deque<String> result = CommandManager.execute("root", "ct", in.nextLine(), false);
            for (String line : result) {
                System.out.println(line);
            }
            System.out.println();
        }
    }

}
