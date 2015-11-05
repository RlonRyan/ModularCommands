/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Print;

import Command.ICommand;
import java.util.ArrayDeque;

/**
 *
 * @author RlonRyan
 */
public class CommandPrintStars implements ICommand {

    @Override
    public String getIdentifier() {
        return "stars";
    }

    @Override
    public String getUsage() {
        return "[number]";
    }

    @Override
    public void execute(ArrayDeque<String> args) {
        if (args.size() < 1) {
            System.out.println("Not enough arguments.");
            return;
        }

        try {
            final int num = Integer.decode(args.peek() == null ? "0" : args.pop());
            for (int i = 0; i < num; i++) {
                System.out.print("*");
            }
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("Argument not a number.");
        }
    }
}
