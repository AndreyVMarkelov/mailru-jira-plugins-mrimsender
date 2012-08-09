/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This is base MRIM exception.
 * 
 * @author Andrey Markelov
 */
public class MrimException
    extends Exception
{
    /**
     * Serial ID.
     */
    private static final long serialVersionUID = -8983784468153665855L;

    /**
     * Error code.
     */
    private int errorCode;

    /**
     * Constructor.
     */
    public MrimException(int errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * Constructor with cause <code>Exception</code>.
     */
    public MrimException(Throwable cause, int errorCode)
    {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    @Override
    public String toString()
    {
        return "MrimException[errorCode=" + errorCode + "]";
    }
}
