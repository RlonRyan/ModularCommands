/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.suggestors;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import modcmd.commands.CommandManager;

/**
 *
 * @author RlonRyan
 */
public class StaticSuggestors {
    
    @Suggestor({"boolean", "bool", "b"})
    public static void suggestBool(String tag, String value, List<String> options) {
        
        if (value.isEmpty()) {
            options.add("true");
            options.add("false");
            return;
        }
        switch (value.charAt(0)) {
            case 'F':
            case 'f':
            case '0':
                options.add("false");
                return;
            case 'T':
            case 't':
            case '1':
                options.add("true");
                return;
            default:
                options.add("true");
                options.add("false");
        }
        
    }

    @Suggestor({"command", "cmd", "c"})
    public static void suggestCommand(String tag, String parameter, List<String> options) {
        ArrayDeque<String> tokens = new ArrayDeque<>(Arrays.asList(parameter.split("\\s+")));
        if(tokens.isEmpty()) {
            options.addAll(CommandManager.getCommandSetNames());
        } else {
            options.addAll(CommandManager.getCommandSet(tokens.pop()).suggestCompletion(tokens));
        }
    }

}
