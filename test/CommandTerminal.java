
import Command.CommandManager;
import Command.Default.CommandQuit;
import Command.Print.CommandPrintStars;
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

        CommandManager.addCommandSet("print");
        CommandManager.getCommandSet("print").registerCommand(new CommandPrintStars());
        CommandManager.getCommandSet("").registerCommand(new CommandQuit());

    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        while (true) {
            CommandManager.execute(in.nextLine());
        }
    }

}
