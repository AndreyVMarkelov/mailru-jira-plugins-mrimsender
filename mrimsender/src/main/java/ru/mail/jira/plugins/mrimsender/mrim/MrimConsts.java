/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This interface contains all contains that are required for working with MRIM.
 * 
 * @author Andrey Markelov
 */
public interface MrimConsts
{
    /**
     * Contact group.
     */
    int CONTACT_FLAG_VISIBLE = 0x00000008;

    /**
     * MRIM magic number.
     */
    int CS_MAGIC = 0xDEADBEEF;

    /**
     * Default encoding that MRIM uses.
     */
    String DEFAULT_MRIM_ENCODING = "cp1251";

    /**
     * Empty string.
     */
    String EMPTY_STRING = " ";

    /**
     * Authorize flag for "message" action.
     */
    int MESSAGE_FLAG_AUTHORIZE = 0x00000008;

    /**
     * Set MRIM messages encoding.
     */
    int MESSAGE_FLAG_CP1251 = 0x00200000;

    /**
     * Multicast send message.
     */
    int MESSAGE_FLAG_MULTICAST = 0x00001000;

    /**
     * No answer flag.
     */
    int MESSAGE_FLAG_NORECV = 0x00000004;

    /**
     * "add contact" action.
     */
    int MRIM_CS_ADD_CONTACT = 0x1019;

    /**
     * "authorize" action.
     */
    int MRIM_CS_AUTHORIZE = 0x1020;

    /**
     * "login" answer.
     */
    int MRIM_CS_LOGIN_ACK = 0x1004;

    /**
     * "login" failed.
     */
    int MRIM_CS_LOGIN_REJ = 0x1005;

    /**
     * "login" action.
     */
    int MRIM_CS_LOGIN2 = 0x1038;

    /**
     * "message" action.
     */
    int MRIM_CS_MESSAGE = 0x1008;

    /**
     * "message" anwer.
     */
    int MRIM_CS_MESSAGE_ACK = 0x1009;

    /**
     * "ping" action.
     */
    int MRIM_CS_PING = 0x1006;

    /**
     * Status: online.
     */
    int MRIM_CS_STATUS_ONLINE = 0x00000001;

    /**
     * Code: request user by filters.
     */
    int MRIM_CS_WP_REQUEST = 0x1029;

    /**
     * Protocol version.
     */
    int PROTO_VERSION = (1 << 16) | 16;

    /**
     * Russian locale.
     */
    String RU_LOCALE = "ru";

    /**
     * "Say hello" to MRIM server.
     */
    int SAY_HELLO = 0x1001;

    /**
     * Success "say hello" answer.
     */
    int SAY_HELLO_ASC = 0x1002;

    /**
     * UTF-16 encoding that MRIM uses.
     */
    String UTF16_MRIM_ENCODING = "UTF-16LE";
}
