/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This structure keeps host and port.
 * 
 * @author Andrey Markelov
 */
public class MrimHostPort
{
    /**
     * Hostname.
     */
    private String host;

    /**
     * Port.
     */
    private int port;

    /**
     * Constructor.
     */
    public MrimHostPort(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public String toString()
    {
        return "MrimHostPort[host=" + host + ", port=" + port + "]";
    }
}
