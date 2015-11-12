/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Print;

import Command.CommandParameter;
import Command.Command;

/**
 *
 * @author RlonRyan
 */
public class CommandPrint {

    @Command("")
    public static void print(@CommandParameter(tag = "m", name = "message", type = "String") String message) {
        System.out.println(message);
    }

    @Command("stars")
    public static void printStars(@CommandParameter(tag = "n", name = "number", type = "Integer", defaultValue = "10") Integer n) {
        for (; n > -1; n--) {
            System.out.print("*");
        }
        System.out.println();
    }

    @Command("pyramid")
    public static void printPyramid(@CommandParameter(tag = "n", name = "number", type = "Integer", defaultValue = "10") Integer n) {
        for (int i = 0; 2 * i < n; i++) {
            for (int ii = 0; ii < i; ii++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

}
