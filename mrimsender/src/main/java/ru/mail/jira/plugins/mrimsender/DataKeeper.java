/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender;

/**
 * This structure keeps data for MRIM queue.
 * 
 * @author Andrey Markelov
 */
public class DataKeeper
{
    /*
     * Operation types.
     */
    public static final int SEND_MESSAGE = 0;
    public static final int SEND_MULTI_MESSAGE = 1;
    public static final int PING = 2;
    public static final int ADD_CONTACT = 3;
    public static final int AUTHORIZE = 4;

    /**
     * Data.
     */
    private byte[] data;

    /**
     * Operation type.
     */
    private int operation;

    /**
     * Constructor.
     */
    public DataKeeper(byte[] data, int operation)
    {
        this.data = data;
        this.operation = operation;
    }

    public byte[] getData()
    {
        return data;
    }

    public int getOperation()
    {
        return operation;
    }

    @Override
    public String toString()
    {
        return ("DataKeeper[operation=" + operation + ", data=" + data.toString() + "]");
    }
}
