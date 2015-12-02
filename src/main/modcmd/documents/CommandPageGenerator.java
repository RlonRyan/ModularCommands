/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.documents;

import modcmd.commands.Command;
import modcmd.commands.CommandManager;
import modcmd.commands.CommandNode;
import modcmd.commands.CommandParameter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author RlonRyan
 */
public class CommandPageGenerator {

    public static String INDEX_TEMPLATE = "modcmd/documents/templates/index_template.md";
    public static String COMMAND_TEMPLATE = "modcmd/documents/templates/command_template.md";

    @Command(
            name = "gendocs",
            about = "Generates command documentation files.",
            checked = true
    )
    public static String generateAllPages(
            @CommandParameter(name = "commandset", tag = "c", description = "Commandset to generate documentation for.", type = "String") String commandset,
            @CommandParameter(name = "path", tag = "p", description = "The folder to output to.", type = "String", defaultValue = "doc") String path
    ) {

        if (!CommandManager.hasCommandSet(commandset)) {
            return "Missing command set.";
        }

        path = path.concat("/").concat(commandset);
        StringBuilder sb = new StringBuilder();

        sb.append("\nDocumentation Output:\n");

        generateAllPages(CommandManager.getCommandSet(commandset), path, sb);

        return sb.toString();
    }

    private static void generateAllPages(CommandNode cmds, String path, StringBuilder traker) {
        generatePage(cmds, path);
        for (CommandNode node : cmds.getSubnodes().values()) {
            if (node.getSubnodes().isEmpty()) {
                traker.append(generatePage(node, path)).append('\n');
            } else {
                generateAllPages(node, path + "/" + node.identifier, traker);
            }
        }
    }

    public static String generatePage(CommandNode cmd, String path) {

        URI templateURI;

        try {
            templateURI = ClassLoader.getSystemClassLoader().getResource(cmd.getSubnodes().isEmpty() ? COMMAND_TEMPLATE : INDEX_TEMPLATE).toURI();
        } catch (URISyntaxException e) {
            return "Improper template filepath syntax.";
        } catch (NullPointerException e) {
            return "Bad reference to the templates folder.";
        }

        Path documentPath = Paths.get(path, cmd.getSubnodes().isEmpty() ? cmd.identifier.concat(".md") : "index.md");

        try {
            if (documentPath.getParent() != null) {
                Files.createDirectories(documentPath.getParent());
            }
        } catch (IOException e) {
            return "Unable to create directory for writing documentation.";
        }

        try (BufferedReader template = Files.newBufferedReader(Paths.get(templateURI));
                BufferedWriter documentWriter = Files.newBufferedWriter(documentPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            String line = template.readLine();

            while (line != null) {
                documentWriter.append(cmd.getSubnodes().isEmpty() ? formatCommand(line, cmd) : formatIndex(line, cmd));
                documentWriter.newLine();
                line = template.readLine();
            }

        } catch (IOException e) {
            return "Unable to open command file: " + e.getMessage();
        }

        return "Command documentation for: " + cmd.identifier + " generated!";
    }

    public static String formatCommand(String line, CommandNode cmd) {

        StringBuilder sb = new StringBuilder();

        for (String e : cmd.getHelp(new ArrayDeque<String>())) {
            sb.append(e).append("\n");
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("\\$\\{command_name\\}", cmd.identifier);
        replacements.put("\\$\\{command_about\\}", cmd.command.about());
        replacements.put("\\$\\{command_usage\\}", cmd.getUsage());
        replacements.put("\\$\\{command_help\\}", sb.toString());
        replacements.put("\\$\\{command_related\\}", String.format("[%1$s](index.md)", cmd.parent.identifier));

        for (String pattern : replacements.keySet()) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(line);
            line = matcher.replaceAll(replacements.get(pattern));
        }

        return line;
    }

    public static String formatIndex(String line, CommandNode cmd) {

        StringBuilder sb = new StringBuilder();

        for (CommandNode e : cmd.getSubnodes().values()) {
            sb.append(" - [").append(e.identifier).append("](").append(e.identifier).append(e.getSubnodes().isEmpty() ? ".md)\n" : "/index.md)\n");
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("\\$\\{command_name\\}", cmd.identifier);
        replacements.put("\\$\\{command_list\\}", sb.toString());
        replacements.put("\\$\\{command_related\\}", String.format(" - [%1$s](../index.md)", cmd.parent.identifier));

        for (String pattern : replacements.keySet()) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(line);
            line = matcher.replaceAll(replacements.get(pattern));
        }

        return line;
    }

}
