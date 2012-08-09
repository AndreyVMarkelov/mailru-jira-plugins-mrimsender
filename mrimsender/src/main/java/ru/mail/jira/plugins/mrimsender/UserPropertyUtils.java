/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

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
    public static String getUserMrimLogin(UserUtil userUtil, String user)
    {
        return getUserProperty(userUtil, user, USER_MRIM_LOGIN);
    }

    /**
     * Get MRIM status property of the user.
     */
    public static String getUserMrimStatus(UserUtil userUtil, String user)
    {
        String status = getUserProperty(userUtil, user, USER_MRIM_STATUS);
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
    @SuppressWarnings("deprecation")
    private static String getUserProperty(UserUtil userUtil, String user, String key)
    {
        return userUtil.getUser(user).getPropertySet().getString(key);
    }

    /**
     * Set MRIM login property of the user.
     */
    public static void setUserMrimLogin(UserUtil userUtil, String user, String value)
    {
        setUserProperty(userUtil, user, USER_MRIM_LOGIN, value);
    }

    /**
     * Set MRIM status property of the user.
     */
    public static void setUserMrimStatus(UserUtil userUtil, String user, String value)
    {
        setUserProperty(userUtil, user, USER_MRIM_STATUS, value);
    }

    /**
     * Set user property.
     */
    @SuppressWarnings("deprecation")
    private static void setUserProperty(UserUtil userUtil, String user, String key, String value)
    {
        userUtil.getUser(user).getPropertySet().setString(key, value);
    }

    /**
     * Private constructor.
     */
    private UserPropertyUtils() {}
}
