/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Socket wrapper.
 * 
 * @author Andrey Markelov
 */
class MRIMSocket
{
    /**
     * IP address.
     */
    private int ipAddress;

    /**
     * Socket.
     */
    private Socket mailEcho;

    /**
     * Connection port.
     */
    private int port;

    MRIMSocket(MrimHostPort mhp)
    throws MrimException
    {
        try
        {
            mailEcho = new Socket(mhp.getHost(), mhp.getPort());
            port = mailEcho.getLocalPort();
            byte[] buff = mailEcho.getInetAddress().getAddress();

            ByteBuffer  bb = ByteBuffer.allocate(buff.length);
            int size = (buff.length / 4) + ((buff.length % 4 == 0) ? 0 : 1);
            bb.put(buff);
            bb.rewind();
            IntBuffer ib =  bb.asIntBuffer();
            int [] result = new int [size];
            ib.get(result);
            ipAddress = result[0];
        }
        catch (Exception ex)
        {
            throw new MrimException(ex, MrimErrors.ERROR_OPEN_SOCKET);
        }
    }

    void closeSocket()
    throws MrimException
    {
        try
        {
            mailEcho.close();
        }
        catch (IOException ex)
        {
            throw new MrimException(ex, MrimErrors.ERROR_OPEN_SOCKET);
        }
    }

    int getIpAddress()
    {
        return ipAddress;
    }

    int getPort()
    {
        return port;
    }

    boolean isClosed()
    {
        return mailEcho.isClosed();
    }

    boolean ifA()
    {
        try
        {
            return (mailEcho.getInputStream().available() > 0);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    int[] readMrimPackage(int buffCapasity)
    throws MrimException
    {
        byte[] buff;
        try
        {
            buff = new byte[buffCapasity];
            mailEcho.getInputStream().read(buff);
        }
        catch (IOException ex)
        {
            throw new MrimException(ex, MrimErrors.ERROR_OPEN_SOCKET);
        }

        return MrimDataUtils.bytesToIntArray(buff);
    }

    void writeMrimPackage(byte[] mrimPackage)
    throws MrimException
    {
        try
        {
            mailEcho.getOutputStream().write(mrimPackage);
            mailEcho.getOutputStream().flush();
        }
        catch (IOException ex)
        {
            throw new MrimException(ex, MrimErrors.ERROR_IO_SOCKET);
        }
    }
}
