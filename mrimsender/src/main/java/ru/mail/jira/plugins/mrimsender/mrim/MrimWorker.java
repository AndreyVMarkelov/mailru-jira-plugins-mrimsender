/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * This class is used for working with MRIM packages.
 * 
 * @author Andrey Markelov
 */
public class MrimWorker
{
    /**
     * Internal sleep pause.
     */
    private static final int INTERNAL_SLEEP = 3000;

    /**
     * Client name.
     */
    private static final String JIRA_CLIENT = "client=\"JiraClient\" version=\"0.01\"";

    /**
     * Get instance of <code>MrimSender</code> that is ready to work with packages.
     */
    public static MrimWorker createMrimSender()
    throws MrimException
    {
        MRIMSocket mrimSocket = new MRIMSocket(MrimAddressResolver.getMrinHostPort());
        mrimSocket.writeMrimPackage(
            MRIMPackageFactory.createSayHelloPackage(
                mrimSocket.getIpAddress(),
                mrimSocket.getPort()));
        SayHelloMrimPackage shpk = PackageConverter.convertSayHelloMrimPackage(mrimSocket.readMrimPackage(48));
        if (shpk.getMessageType() == MrimConsts.SAY_HELLO_ASC)
        {
            return new MrimWorker(mrimSocket, shpk.getTimeout(), 1);
        }
        else
        {
            throw new MrimException(MrimErrors.ERROR_SAY_HELLO);
        }
    }

    /**
     * Message actual sequence.
     */
    private int messageSeq;

    /**
     * MRIM socket.
     */
    private MRIMSocket mrimSocket;

    /**
     * MRIM server timeout.
     */
    private int timeout;

    /**
     * Private constructor.
     */
    private MrimWorker(MRIMSocket mrimSocket, int timeout, int messageSeq)
    {
        this.mrimSocket = mrimSocket;
        this.timeout = timeout;
        this.messageSeq = messageSeq;
    }

    /**
     * Add contact to contact list.
     */
    public void addContact(String user)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(
            MRIMPackageFactory.createAddContactPackage(
                messageSeq++,
                user,
                mrimSocket.getIpAddress(),
                mrimSocket.getPort()));
    }

    /**
     * Authorize user.
     */
    public void authorize(String user)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(
            MRIMPackageFactory.createAuthorizePackage(
                messageSeq++,
                user,
                mrimSocket.getIpAddress(),
                mrimSocket.getPort()));
    }

    /**
     * Close.
     */
    public void close()
    throws MrimException
    {
        if (mrimSocket != null)
        {
            mrimSocket.closeSocket();
        }
    }

    /**
     * Get IP address.
     */
    public int getIpAddress()
    {
        return mrimSocket.getIpAddress();
    }

    /**
     * Message number.
     */
    public int getMessageSeq()
    {
        return messageSeq++;
    }

    /**
     * Get port.
     */
    public int getPort()
    {
        return mrimSocket.getPort();
    }

    /**
     * Get timeout.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Login to MRIM server.
     */
    public void login(
        String login,
        String password)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(
            MRIMPackageFactory.createLoginPackage(
                messageSeq++,
                login,
                password,
                JIRA_CLIENT,
                mrimSocket.getIpAddress(),
                mrimSocket.getPort()));
        LoginMrimPackage lmp = PackageConverter.convertLoginMrimPackage(mrimSocket.readMrimPackage(44));
        if (lmp.getDataLength() > 0)
        {
            mrimSocket.readMrimPackage(lmp.getDataLength());
        }

        if (lmp.getMessageType() == MrimConsts.MRIM_CS_LOGIN_ACK)
        {
            readAllAvailable();
            return;
        }
        else if (lmp.getMessageType() == MrimConsts.MRIM_CS_LOGIN_REJ)
        {
            throw new MrimException(MrimErrors.ERROR_AUTH_LOGIN);
        }
        else
        {
            throw new MrimException(MrimErrors.ERROR_LOGIN);
        }
    }

    public void readAllAvailable()
    throws MrimException
    {
        while (mrimSocket.ifA())
        {
            BaseMrimPackage bmp = new BaseMrimPackage(mrimSocket.readMrimPackage(44));
            int[] data;
            if (bmp.getDataLength() > 0)
            {
                data = mrimSocket.readMrimPackage(bmp.getDataLength());
            }
            else
            {
                data = new int[0];
            }

            processPackage(bmp, data);
        }
    }

    private void processPackage(BaseMrimPackage bmp, int[] data)
    throws MrimException
    {
        if (bmp.getMessageType() == MrimConsts.MRIM_CS_MESSAGE_ACK)
        {
            if (data.length < 2)
            {
                return;
            }

            if ((data[1] & MrimConsts.MESSAGE_FLAG_AUTHORIZE) == MrimConsts.MESSAGE_FLAG_AUTHORIZE)
            {
                if (data.length < data[2])
                {
                    return;
                }

                ByteBuffer  bb = ByteBuffer.allocate(4*data[2]);
                for (int i = 0; i < data[2]; i++) bb.put(MrimDataUtils.intToLE(data[3 + i]));
                String to = (new String(bb.array())).substring(0, data[2]);
                write(MRIMPackageFactory.createAuthorizePackage(messageSeq++, to, mrimSocket.getIpAddress(), mrimSocket.getPort()));
            }
        }
    }

    /**
     * Ping MRIM server.
     */
    public void ping()
    throws MrimException
    {
        write(
            MRIMPackageFactory.createPingPackage(
                messageSeq++,
                mrimSocket.getIpAddress(),
                mrimSocket.getPort()));
    }

    /**
     * Request authorize.
     */
    public void requestAuthorize(
        String to,
        String message)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(MRIMPackageFactory.createMessagePackage(
            messageSeq++,
            MrimConsts.MESSAGE_FLAG_NORECV | MrimConsts.MESSAGE_FLAG_AUTHORIZE | MrimConsts.MESSAGE_FLAG_CP1251,
            to,
            message,
            mrimSocket.getIpAddress(),
            mrimSocket.getPort()));
        sendMessage(to, message);
        sleep();
    }

    /**
     * Send the message to receiver.
     */
    public void sendMessage(
        String to,
        String message)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(MRIMPackageFactory.createMessagePackage(
            messageSeq++,
            MrimConsts.MESSAGE_FLAG_NORECV | MrimConsts.MESSAGE_FLAG_CP1251,
            to,
            message,
            mrimSocket.getIpAddress(),
            mrimSocket.getPort()));
        sleep();
    }

    /**
     * Send the message to receiver.
     */
    public void sendMessageMulti(
        Collection<String> to,
        String message)
    throws MrimException
    {
        mrimSocket.writeMrimPackage(MRIMPackageFactory.createMesageMultiPackage(
            messageSeq++,
            to,
            message,
            mrimSocket.getIpAddress(),
            mrimSocket.getPort()));
        sleep();
    }

    /**
     * Default internal sleep.
     */
    public void sleep()
    {
        sleep(INTERNAL_SLEEP);
    }

    /**
     * Internal sleep.
     */
    public void sleep(long mills)
    {
        try
        {
            Thread.sleep(mills);
        }
        catch (InterruptedException e)
        {
            //--> we can ignore it
        }
    }

    public synchronized void write(byte[] data)
    throws MrimException
    {
        readAllAvailable();
        mrimSocket.writeMrimPackage(data);
        sleep(1000);
    }
}
