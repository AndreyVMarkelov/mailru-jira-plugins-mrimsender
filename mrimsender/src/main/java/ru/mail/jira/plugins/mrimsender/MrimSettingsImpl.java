/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Implementation of <code>MrimSettings</code>.
 * 
 * @author Andrey Markelov
 */
public class MrimSettingsImpl
    implements MrimSettings
{
    /**
     * Plugin key.
     */
    private static final String MRIM_SENDER = "MRIM_SENDER";
    private static final String MRIM_LOGIN = "MRIM_LOGIN_KEY";
    private static final String MRIM_PASSWORD = "MRIM_PASSWORD_KEY";
    private static final String MRIM_CP = "MRIM_CP";

    /**
     * Plugin settings factory.
     */
    private final PluginSettingsFactory mrimPluginSettings;

    /**
     * Constructor.
     */
    public MrimSettingsImpl(PluginSettingsFactory mrimPluginSettings)
    {
        this.mrimPluginSettings = mrimPluginSettings;
    }

    @Override
    public String getLogin()
    {
        return getPluginProperty(MRIM_LOGIN);
    }

    @Override
    public String getPassword()
    {
        return getPluginProperty(MRIM_PASSWORD);
    }

    /**
     * Get plugin setting by key.
     */
    private String getPluginProperty(String key)
    {
        return (String) mrimPluginSettings.createSettingsForKey(MRIM_SENDER).get(key);
    }

    @Override
    public void setLogin(String login)
    {
        setPluginProperty(MRIM_LOGIN, login);
    }

    @Override
    public void setPassword(String password)
    {
        setPluginProperty(MRIM_PASSWORD, password);
    }

    /**
     * Set plugin setting.
     */
    private void setPluginProperty(String key, String value)
    {
        mrimPluginSettings.createSettingsForKey(MRIM_SENDER).put(key, value);
    }

    @Override
    public String getContextPath()
    {
        return getPluginProperty(MRIM_CP);
    }

    @Override
    public void setContextPath(String contextPath)
    {
    	setPluginProperty(MRIM_CP, contextPath);
    }
}
