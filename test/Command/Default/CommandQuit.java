/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.Default;

import Command.ICommand;
import java.util.ArrayDeque;

/**
 *
 * @author RlonRyan
 */
public class CommandQuit implements ICommand {

    @Override
    public String getIdentifier() {
        return "quit";
    }

    @Override
    public String getUsage() {
        return "[No arguments]";
    }

    @Override
    public void execute(ArrayDeque<String> args) {
        System.exit(0);
    }

}
