/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import permissions.PermissionManager;

/**
 *
 * @author RlonRyan
 */
public class CommandNode {

    public final String parentIdent;
    public final String identifier;
    public final CommandNode parent;
    public final Method command;

    final HashMap<String, CommandNode> subNodes;

    protected CommandNode(String identifier) {
        this(identifier, null, null);
    }

    protected CommandNode(String identifier, CommandNode parent) {
        this(identifier, parent, null);
    }

    protected CommandNode(String identifier, CommandNode parent, Method command) {
        this.parentIdent = (parent == null ? "" : parent.getFullIdentifier());
        this.identifier = identifier.toLowerCase();
        this.parent = parent;
        this.subNodes = new HashMap<>();
        this.command = command;
        //Logger.getGlobal().log(Level.INFO, "Registering {0} with ident: {1}", new Object[]{this.command == null ? "command node" : "command", this.getFullIdentifier()});
    }

    public String getFullIdentifier() {
        return this.parent == null ? this.identifier : (this.parentIdent + "." + ((this.identifier.length() < 1) ? "default" : this.identifier));
    }

    public CommandNode getNearest(ArrayDeque<String> args) {

        //System.out.println(this.getFullIdentifier() + " args: " + args.peek());
        if (this.subNodes.isEmpty() || args.peek() == null) {
            return this;
        }

        CommandNode node = this.subNodes.get(args.peek());

        if (node == null) {
            node = this.subNodes.get("");
        } else {
            args.pop();
        }

        if (node == null) {
            node = this;
        } else {
            node = node.getNearest(args);
        }

        return node;

    }

    public void registerCommand(Class command) {
        for (Method m : command.getDeclaredMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    String tag = m.getAnnotation(Command.class).value();
                    subNodes.putIfAbsent(tag, new CommandNode(tag, this, m));
                } else {
                    Logger.getLogger(PermissionManager.class.getCanonicalName()).log(Level.SEVERE, "Command Method: {1}.{0} is not public static!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                }
            }
        }
    }

    public boolean registerSubset(String identifier) {
        return subNodes.putIfAbsent(identifier.toLowerCase(), new CommandNode(identifier, this)) == null;
    }

    public boolean deregisterSubnode(String identifier) {
        return subNodes.remove(identifier.toLowerCase()) != null;
    }

    public String getHelp() {

        StringBuilder sb = new StringBuilder();

        sb.append("Help for ").append(this.identifier.length() > 0 ? this.identifier : "ROOT").append(":").append('\n');

        if (subNodes.isEmpty()) {
            sb.append("No help to give...");
        }

        for (CommandNode node : (this.identifier.length() < 1 ? this.parent.subNodes.values() : this.subNodes.values())) {
            if (node.command == null) {
                sb.append(" - Subgroup: ").append(node.identifier).append('\n');
            } else {
                sb.append(" - Subcommand: ").append(node.command.getAnnotation(Command.class).value()).append('\n');
                sb.append("   - Usage: ").append(node.command.getAnnotation(Command.class).value()).append(' ');
                for (Annotation annos[] : node.command.getParameterAnnotations()) {
                    for (Annotation param : annos) {
                        if (param instanceof CommandParameter) {
                            sb.append(((CommandParameter) param).tag()).append(':');
                            sb.append(((CommandParameter) param).type()).append(' ');
                        }
                    }
                }
                sb.append('\n');
            }
        }

        return sb.toString();
    }

}
