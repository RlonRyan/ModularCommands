/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modcmd.permissions.exceptions;

import modcmd.commands.exception.CommandException;

/**
 *
 * @author RlonRyan
 */
public class PermissionException extends CommandException {

    public static final String MESSAGE_FORMAT = "Command Permission Issue:%n\t- User: %1$s%n\t- Permission: %2$s%n\t- Reason: %3$s";

    /**
     * Constructs an instance of <code>PermissionException</code> with the
     * specified detail message.
     *
     */
    public PermissionException(String issue, Object user, String permission, String reason) {
        super(String.format(MESSAGE_FORMAT, issue, user, permission, reason));
    }
}
