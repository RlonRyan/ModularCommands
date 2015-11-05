/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command;

import java.util.ArrayDeque;

/**
 *
 * @author RlonRyan
 */
public interface ICommand {

    String getIdentifier();

    String getUsage();

    void execute(ArrayDeque<String> args);

}
