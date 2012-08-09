/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This class contains utility methods that converts <code>int</code> arrays to package structures.
 * 
 * @author Andrey Markelov
 */
class PackageConverter
{
    /**
     * Create instance of <code>LoginMrimPackage</code>.
     */
    public static LoginMrimPackage convertLoginMrimPackage(int[] iarr)
    {
        return new LoginMrimPackage(iarr[0], iarr[1], iarr[2], iarr[3], iarr[4], iarr[5], iarr[6], iarr[7], iarr[8], iarr[9], iarr[10], iarr[10]);
    }

    /**
     * Create instance of <code>MessageMrimPackage</code>.
     */
    public static MessageMrimPackage convertMessageMrimPackage(int[] iarr)
    {
        return new MessageMrimPackage(iarr[0], iarr[1], iarr[2], iarr[3], iarr[4], iarr[5], iarr[6], iarr[7], iarr[8], iarr[9], iarr[10], iarr[11]);
    }

    /**
     * Create instance of <code>SayHelloMrimPackage</code>.
     */
    public static SayHelloMrimPackage convertSayHelloMrimPackage(int[] iarr)
    {
        return new SayHelloMrimPackage(iarr[0], iarr[1], iarr[2], iarr[3], iarr[4], iarr[5], iarr[6], iarr[7], iarr[8], iarr[9], iarr[10], iarr[11]);
    }

    /**
     * Private constructor.
     */
    private PackageConverter() {}
}
