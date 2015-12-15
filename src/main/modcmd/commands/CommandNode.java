/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.commands;

import modcmd.commands.validators.CommandValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modcmd.user.CommandUser;
import static modcmd.commands.CommandManager.MARKER;
import modcmd.suggestors.SuggestorManager;

/**
 *
 * @author RlonRyan
 */
public class CommandNode {

    // Node
    public final String identifier;
    public final CommandNode parent;

    // Command
    public final Command command;
    public final Annotation[] parameters;

    // Private
    final HashMap<String, CommandNode> subNodes;
    final Method commandMethod;

    protected CommandNode(String identifier, CommandNode parent, Method command) {
        this.identifier = identifier.isEmpty() ? "default" : identifier.toLowerCase();
        this.parent = parent;
        this.subNodes = new HashMap<>();
        this.commandMethod = command;
        this.command = command == null ? null : command.getAnnotation(Command.class);

        List<Annotation> params = new ArrayList<>();
        if (this.commandMethod != null) {
            for (Annotation[] annos : this.commandMethod.getParameterAnnotations()) {
                for (Annotation anno : annos) {
                    if (anno instanceof CommandParameter || anno instanceof CommandUser) {
                        params.add(anno);
                    }
                }
            }
        }

        this.parameters = params.toArray(new Annotation[params.size()]);
    }

    public String getFullIdentifier() {
        return this.parent == null ? this.identifier : (this.parent.identifier + "." + this.identifier);
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

    public void registerCommands(Class command) {
        for (Method m : command.getDeclaredMethods()) {
            if (m.getAnnotation(Command.class) != null && CommandValidator.validate(m)) {
                String name = m.getAnnotation(Command.class).name().toLowerCase();
                name = name.isEmpty() ? "default" : name;
                subNodes.putIfAbsent(name, new CommandNode(name, this, m));
            }
        }
    }

    public boolean registerSubset(String identifier) {
        return subNodes.putIfAbsent(identifier.toLowerCase(), new CommandNode(identifier, this, null)) == null;
    }

    public boolean deregisterSubnode(String identifier) {
        return subNodes.remove(identifier.toLowerCase()) != null;
    }

    public List<String> suggestCompletion(ArrayDeque<String> args) {

        List<String> suggestions = new ArrayList<>();

        if (args.isEmpty()) {
            // Misses 1 param commands...
            return suggestions;
        }

        String toComplete = args.pollLast();
        toComplete = (toComplete == null) ? "" : toComplete.toLowerCase();
        
        // Slow, but what other way is there to do it?
        String lastTag = "";
        for (String arg : args) {
            if (arg.charAt(0) == MARKER) {
                lastTag = arg;
            }
        }
        if (!lastTag.isEmpty()) {
            lastTag = lastTag.substring(1).toLowerCase();
        }
        
        for (String key : this.subNodes.keySet()) {
            if (toComplete.isEmpty() || key.startsWith(toComplete)) {
                suggestions.add(key);
            }
        }

        if (this.commandMethod != null) {
            for (Annotation param : parameters) {
                if (param instanceof CommandParameter) {
                    CommandParameter cmdparam = (CommandParameter) param;
                    if (toComplete.isEmpty() || toComplete.charAt(0) == MARKER && cmdparam.tag().startsWith(toComplete.substring(1))) {
                        suggestions.add(MARKER + cmdparam.tag());
                    }
                    if (cmdparam.tag().equals(lastTag) && SuggestorManager.hasSuggestorFor(cmdparam.type())) {
                        SuggestorManager.Suggest(lastTag, toComplete, suggestions);
                    }
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
            if (this.commandMethod == null) {
                lines.add(" - No help to give.");
            } else {
                lines.add(" - Usage: " + this.getUsage());
            }
        } else {
            for (CommandNode node : this.subNodes.values()) {
                if (node.identifier.isEmpty()) {
                    node.getHelp(lines);
                } else if (node.commandMethod == null) {
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
        if (this.commandMethod == null) {
            sb.append("[subcommand]");
        } else {
            for (Annotation annos[] : this.commandMethod.getParameterAnnotations()) {
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
