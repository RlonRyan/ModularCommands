/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 *
 * @author RlonRyan
 */
public class CommandNode {

    public final String identifier;
    public final CommandNode parent;
    public final ICommand command;

    final HashMap<String, CommandNode> subNodes;

    protected CommandNode(String identifier) {
        this(identifier, null, null);
    }

    public CommandNode(String identifier, CommandNode parent) {
        this(identifier, parent, null);
    }

    public CommandNode(String identifier, CommandNode parent, ICommand command) {
        this.identifier = identifier.toLowerCase();
        this.parent = parent;
        this.subNodes = new HashMap<>();
        this.command = command;
    }

    public String getFullIdentifier() {
        return (this.parent == null ? "" : this.parent.getFullIdentifier()) + " " + this.identifier;
    }

    public CommandNode getNearest(ArrayDeque<String> args) {

        //System.out.println(this.getFullIdentifier() + " args: " + args.peek());
        if (this.subNodes.isEmpty() || args.peek() == null) {
            return this;
        }

        CommandNode node = this.subNodes.get(args.pop());

        return node == null ? this : node.getNearest(args);

    }

    public boolean registerCommand(ICommand command) {
        return subNodes.putIfAbsent(command.getIdentifier().toLowerCase(), new CommandNode(identifier, this, command)) == null;
    }

    public boolean registerSubset(String identifier) {
        return subNodes.putIfAbsent(identifier.toLowerCase(), new CommandNode(identifier, this)) == null;
    }

    public boolean deregisterSubnode(String identifier) {
        return subNodes.remove(identifier.toLowerCase()) != null;
    }

    public String getHelp() {

        StringBuilder sb = new StringBuilder();

        sb.append("Help for ").append(this.identifier).append(":").append('\n');

        if (subNodes.isEmpty()) {
            sb.append("No help to give...");
        }

        for (CommandNode node : this.subNodes.values()) {
            if (node.command == null) {
                sb.append(" - Subgroup: ").append(node.identifier).append('\n');
            } else {
                sb.append(" - Subcommand: ").append(node.command.getIdentifier()).append('\n');
                sb.append("   - Usage: ").append(node.command.getUsage()).append('\n');
            }
        }

        sb.append("");

        return sb.toString();
    }

}
