/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package permissions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import permissions.exceptions.PermissionException;

/**
 *
 * @author RlonRyan
 */
public class PermissionManager {

    public static final int ERORRED = -1;
    public static final int ALLOWED = 0;
    public static final int DENIED = 1;

    public static final Map<String, Method> permHandlers;

    static {
        permHandlers = new HashMap<>();
    }

    public static boolean canHandle(String perm) {
        int delim = perm.indexOf(".");
        return permHandlers.containsKey(perm.substring(0, delim > 0 ? delim : perm.length()).toLowerCase());
    }

    public static int checkPermission(String perm, String user) throws PermissionException {
        try {
            int delim = perm.indexOf(".");
            Method m = permHandlers.get(perm.substring(0, delim > 0 ? delim : perm.length()).toLowerCase());
            if (m == null) {
                Logger.getLogger(PermissionManager.class.getCanonicalName()).log(Level.SEVERE, "Missing permission handler for: {0}\n\tAutomatically denying.", perm);
                return -1;
            }
            return (boolean) m.invoke(null, perm, user) ? 0 : 1;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exc) {
            exc.printStackTrace();
            throw new PermissionException("Permission Handler Error", user, perm, exc.getLocalizedMessage());
        }
    }

    public static void addPermissionHandler(Class handlerClass) {
        for (Method m : handlerClass.getMethods()) {
            if (m.isAnnotationPresent(PermissionHandler.class)) {
                if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    if (m.isAnnotationPresent(PermissionHandler.class) && m.getReturnType() == boolean.class) {
                        permHandlers.putIfAbsent(m.getAnnotation(PermissionHandler.class).value().toLowerCase(), m);
                    } else {
                        Logger.getLogger(PermissionManager.class.getCanonicalName()).log(Level.SEVERE, "Permission Handler: {1}.{0} does not return a boolean!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                    }
                } else {
                    Logger.getLogger(PermissionManager.class.getCanonicalName()).log(Level.SEVERE, "Permission Handler: {1}.{0} is not public static!", new Object[]{m.getName(), m.getClass().getCanonicalName()});
                }
            }
        }
    }

}
