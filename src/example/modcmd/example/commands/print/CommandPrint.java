/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.example.commands.print;

import modcmd.commands.CommandParameter;
import modcmd.commands.Command;

/**
 *
 * @author RlonRyan
 */
public class CommandPrint {

    @Command(
            name = "message",
            about = "Prints a message to the console."
    )
    public static void print(@CommandParameter(tag = "m", name = "message", description = "The message to print.", type = "String") String message) {
        System.out.println(message);
    }

    @Command(
            name = "stars",
            about = "Prints a number of stars to the console."
    )
    public static void printStars(@CommandParameter(tag = "n", name = "number", description = "The number of stars to print.", type = "Integer") int n) {
        for (; n > 0; n--) {
            System.out.print("*");
        }
        System.out.println();
    }

    @Command(
            name = "pyramid",
            about = "Prints a pyramid of stars to the console."
    )
    public static void printPyramid(@CommandParameter(tag = "n", name = "number", description = "The number of stars to print.", type = "Integer") int n) {
        for (int i = 0; 2 * i < n; i++) {
            for (int ii = 0; ii < i; ii++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

}
