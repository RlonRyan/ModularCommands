/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import static commands.CommandManager.MARKER_CHAR;
import commands.validators.CommandValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        this.identifier = identifier.isEmpty() ? "default" : identifier.toLowerCase();
        this.parent = parent;
        this.subNodes = new HashMap<>();
        this.command = command;
        //Logger.getGlobal().log(Level.INFO, "Registering {0} with ident: {1}", new Object[]{this.command == null ? "command node" : "command", this.getFullIdentifier()});
    }

    public String getFullIdentifier() {
        return this.parent == null ? this.identifier : (this.parentIdent + "." + this.identifier);
    }

    public CommandNode getNearest(String ident) {
        if (ident.isEmpty()) {
            return this;
        } else if (this.subNodes.containsKey(ident)) {
            return this.subNodes.get(ident);
        } else {
            return getNearest(new ArrayDeque<>(Arrays.asList(ident.split("\\s+"))));
        }
    }

    public CommandNode getNearest(ArrayDeque<String> args) {

        //System.out.println(this.getFullIdentifier() + " args: " + args.peek());
        CommandNode node = this.subNodes.get(args.peek());

        if (node == null) {
            node = this.subNodes.getOrDefault("default", this);
        } else {
            args.pop();
            node = node.getNearest(args);
        }

        return node;

    }

    public void registerCommand(Class command) {
        for (Method m : command.getDeclaredMethods()) {
            if (m.getAnnotation(Command.class) != null && CommandValidator.validate(m)) {
                String name = m.getAnnotation(Command.class).value();
                name = name.isEmpty() ? "default" : name;
                subNodes.putIfAbsent(name, new CommandNode(name, this, m));
            }
        }
    }

    public boolean registerSubset(String identifier) {
        return subNodes.putIfAbsent(identifier.toLowerCase(), new CommandNode(identifier, this)) == null;
    }

    public boolean deregisterSubnode(String identifier) {
        return subNodes.remove(identifier.toLowerCase()) != null;
    }

    public List<String> suggestCompletion(ArrayDeque<String> args) {
        //System.out.println(this.identifier);
        //System.out.println(args);
        List<String> suggestions = new ArrayList<>();
        String toComplete = args.pollLast();
        toComplete = (toComplete == null) ? "" : toComplete.toLowerCase();
        //System.out.println('\"' + toComplete + '\"');

        if (toComplete.isEmpty()) {
            suggestions.addAll(this.subNodes.keySet());
        } else if (args.isEmpty()) {
            for (String key : this.subNodes.keySet()) {
                if (key.startsWith(toComplete)) {
                    suggestions.add(key);
                }
            }
        }

        if (this.command != null) {
            for (Parameter p : this.command.getParameters()) {
                toComplete = (!toComplete.isEmpty()) && toComplete.charAt(0) == MARKER_CHAR ? toComplete.substring(1) : toComplete;
                if ((toComplete.length() < 2) || p.getAnnotation(CommandParameter.class).tag().startsWith(toComplete.substring(1))) {
                    suggestions.add(MARKER_CHAR + p.getAnnotation(CommandParameter.class).tag());
                }
            }
        }

        return suggestions;
    }

    public String getHelp() {

        StringBuilder sb = new StringBuilder();

        if (!this.identifier.isEmpty()) {
            sb.append("Help for ").append(this.identifier).append(":\n");
        }

        if (this.subNodes.isEmpty()) {
            if (!this.identifier.isEmpty()) {
                sb.append(" - No help to give.").append('\n');
            }
        } else {
            for (CommandNode node : this.subNodes.values()) {
                if (node.identifier.isEmpty()) {
                    sb.append(node.getHelp());
                } else if (node.command == null) {
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
        }
        return sb.toString();
    }

}
