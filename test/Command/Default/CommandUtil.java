/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Default;

import Command.Command;
import Command.CommandManager;
import Command.CommandParameter;

/**
 *
 * @author RlonRyan
 */
public class CommandUtil {

    @Command("repeat")
    public static void execute(
            @CommandParameter(tag = "n", name = "loops", type = "Integer") Integer loops,
            @CommandParameter(tag = "c", name = "command", type = "String") String cmd
    ) {
        for (int i = 0; i < loops; i++) {
            CommandManager.execute(cmd);
        }
    }

}
