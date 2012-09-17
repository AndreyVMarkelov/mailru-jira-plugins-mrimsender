/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.UserPropertyManager;
import com.atlassian.jira.user.util.UserUtil;

/**
 * This class contains utility methods.
 * 
 * @author Andrey Markelov
 */
public class UserPropertyUtils
{
    /**
     * It is Mrim login property of user.
     */
    private static final String USER_MRIM_LOGIN = "USER_MRIM_LOGIN";

    /**
     * It is Mrim status property of user.
     */
    private static final String USER_MRIM_STATUS = "USER_MRIM_STATUS";

    /**
     * Get MRIM login property of the user.
     */
    public static String getUserMrimLogin(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user)
    {
        return getUserProperty(userUtil, userProps, user, USER_MRIM_LOGIN);
    }

    /**
     * Get MRIM status property of the user.
     */
    public static String getUserMrimStatus(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user)
    {
        String status = getUserProperty(userUtil, userProps, user, USER_MRIM_STATUS);
        if (status == null)
        {
            return Boolean.FALSE.toString();
        }
        else
        {
            return status;
        }
    }

    /**
     * Get user property.
     */
    private static String getUserProperty(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user,
        String key)
    {
        User userObj = userUtil.getUserObject(user);
        return userProps.getPropertySet(userObj).getString(key);
    }

    /**
     * Set MRIM login property of the user.
     */
    public static void setUserMrimLogin(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user,
        String value)
    {
        setUserProperty(userUtil, userProps, user, USER_MRIM_LOGIN, value);
    }

    /**
     * Set MRIM status property of the user.
     */
    public static void setUserMrimStatus(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user,
        String value)
    {
        setUserProperty(userUtil, userProps, user, USER_MRIM_STATUS, value);
    }

    /**
     * Set user property.
     */
    private static void setUserProperty(
        UserUtil userUtil,
        UserPropertyManager userProps,
        String user,
        String key,
        String value)
    {
        User userObj = userUtil.getUserObject(user);
        userProps.getPropertySet(userObj).setString(key, value);
    }

    /**
     * Private constructor.
     */
    private UserPropertyUtils() {}
}
