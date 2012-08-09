/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

/**
 * Interface for Jira MRIM settings.
 * 
 * @author Andrey Markelov
 */
public interface MrimSettings
{
    /**
     * Get context path.
     */
    public String getContextPath();

    /**
     * Get MRIM login.
     */
    public String getLogin();

    /**
     * Get MRIM password for login.
     */
    public String getPassword();

    /**
     * Set context path.
     */
    public void setContextPath(String contextPath);

    /**
     * Set MRIM login.
     */
    public void setLogin(String login);

    /**
     * Set MRIM password for login.
     */
    public void setPassword(String password);
}
