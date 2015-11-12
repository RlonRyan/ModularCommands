
import Command.CommandManager;
import Command.Default.*;
import Command.Print.*;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
            CommandManager.execute(in.nextLine());
        }
    }

}
