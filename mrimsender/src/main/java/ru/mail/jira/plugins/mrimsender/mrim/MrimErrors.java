/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This interface contains list of errors that can occur during working with MRIM.
 * 
 * @author Andrey Markelov
 */
public interface MrimErrors
{
    /**
     * This code specifies that the program cannot get actual MRIM host and port.
     */
    int ERROR_GET_ACTION_HOSTNAME = -1000;

    /**
     * This code specifies that actual MRIM host and port have invalid format.
     */
    int ERROR_INCORRECT_ACTUAL_MRIM_HOST_NAME = -1001;

    /**
     * This code specifies that socket cannot be opened.
     */
    int ERROR_OPEN_SOCKET = -1002;

    /**
     * This code specifies that I/O error occurred during working with socket.
     */
    int ERROR_IO_SOCKET = -1003;

    /**
     * This code specifies that MRIM server cannot "say hello".
     */
    int ERROR_SAY_HELLO = -1004;

    /**
     * This code specifies that login or password are incorrect.
     */
    int ERROR_AUTH_LOGIN = -1005;

    /**
     * This code specifies that MRIM server cannot login.
     */
    int ERROR_LOGIN = -1006;

    /**
     * This code specifies that MRIM server cannot process message.
     */
    int ERROR_MESSAGE = -1007;
}
