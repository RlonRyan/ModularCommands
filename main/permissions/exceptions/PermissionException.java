/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package permissions.exceptions;

import commands.exception.CommandException;

/**
 *
 * @author RlonRyan
 */
public class PermissionException extends CommandException {

    /**
     * Creates a new instance of <code>PermissionException</code> without detail
     * message.
     */
    public PermissionException() {
    }

    /**
     * Constructs an instance of <code>PermissionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PermissionException(String msg) {
        super(msg);
    }
}
