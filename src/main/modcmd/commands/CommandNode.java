/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.commands;

import static modcmd.commands.CommandManager.MARKER_CHAR;
import modcmd.commands.validators.CommandValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ident = ident.toLowerCase();
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

    public Map<String, CommandNode> getSubnodes() {
        return this.subNodes;
    }

    public void registerCommand(Class command) {
        for (Method m : command.getDeclaredMethods()) {
            if (m.getAnnotation(Command.class) != null && CommandValidator.validate(m)) {
                String name = m.getAnnotation(Command.class).value().toLowerCase();
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
                CommandParameter param = p.getAnnotation(CommandParameter.class);
                if (param != null && ((toComplete.length() < 2) || (param.tag().startsWith(toComplete.substring(1))))) {
                    suggestions.add(MARKER_CHAR + param.tag());
                }
            }
        }

        return suggestions;
    }

    public Deque<String> getHelp(Deque<String> lines) {

        if (!this.identifier.isEmpty()) {
            lines.add("Help for " + this.identifier + ":");
        }

        if (this.subNodes.isEmpty()) {
            if (this.command == null) {
                lines.add(" - No help to give.");
            } else {
                lines.add(" - Usage: " + this.getUsage());
            }
        } else {
            for (CommandNode node : this.subNodes.values()) {
                if (node.identifier.isEmpty()) {
                    node.getHelp(lines);
                } else if (node.command == null) {
                    lines.add(" - Subgroup: " + node.identifier);
                } else {
                    lines.add(" - Subcommand: " + node.identifier);
                    lines.add("   - Usage: " + node.getUsage());
                }
            }
        }

        return lines;
    }

    public String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.identifier).append(" ");
        if (this.command == null) {
            sb.append("[subcommand]");
        } else {
            for (Annotation annos[] : this.command.getParameterAnnotations()) {
                for (Annotation param : annos) {
                    if (param instanceof CommandParameter) {
                        CommandParameter cmd = (CommandParameter) param;
                        if (!cmd.defaultValue().isEmpty()) {
                            sb.append("[");
                        }
                        sb.append('-');
                        sb.append(cmd.tag()).append(':');
                        sb.append(cmd.type()).append(' ');
                        if (!cmd.defaultValue().isEmpty()) {
                            sb.insert(sb.length() - 1, "]");
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

}
