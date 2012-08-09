/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * This class represents the base part of MRIM package.
 * 
 * @author Andrey Markelov
 */
class BaseMrimPackage
{
    /**
     * Length of data.
     */
    private int dataLength;

    /**
     * IP address.
     */
    private int from;

    /**
    * Port.
    */
    private int fromPort;

    /**
     * Magic number.
     */
    private int magic;

    /**
     * Message type.
     */
    private int messageType;

    /**
     * Version of protocol.
     */
    private int proto;

    /**
     * The first reserved value.
     */
    private int reserved1;

    /**
     * The second reserved value.
     */
    private int reserved2;

    /**
     * The third reserved value.
     */
    private int reserved3;

    /**
     * The fourth reserved value.
     */
    private int reserved4;

    /**
     * Request real sequence.
     */
    private int seq;

    /**
     * Private constructor.
     */
    public BaseMrimPackage(
        int magic,
        int proto,
        int seq,
        int messageType,
        int dataLength,
        int from,
        int fromPort,
        int reserved1,
        int reserved2,
        int reserved3,
        int reserved4)
    {
        this.magic = magic;
        this.proto = proto;
        this.seq = seq;
        this.messageType = messageType;
        this.dataLength = dataLength;
        this.from = from;
        this.fromPort = fromPort;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.reserved3 = reserved3;
        this.reserved4 = reserved4;
    }

    /**
     * Constructor.
     */
    public BaseMrimPackage(int[] pack)
    {
        this.magic = pack[0];
        this.proto = pack[1];
        this.seq = pack[2];
        this.messageType = pack[3];
        this.dataLength = pack[4];
        this.from = pack[5];
        this.fromPort = pack[6];
        this.reserved1 = pack[7];
        this.reserved2 = pack[8];
        this.reserved3 = pack[9];
        this.reserved4 = pack[10];
    }

    public int getDataLength()
    {
        return dataLength;
    }

    public int getFrom()
    {
        return from;
    }

    public int getFromPort()
    {
        return fromPort;
    }

    public int getMagic()
    {
        return magic;
    }

    public int getMessageType()
    {
        return messageType;
    }

    public int getProto()
    {
        return proto;
    }

    public int getReserved1()
    {
        return reserved1;
    }

    public int getReserved2()
    {
        return reserved2;
    }

    public int getReserved3()
    {
        return reserved3;
    }

    public int getReserved4()
    {
        return reserved4;
    }

    public int getSeq()
    {
        return seq;
    }

    @Override
    public String toString()
    {
        return "MrimPackage[magic=" + magic + ", proto=" + proto + ", seq="
            + seq + ", messageType=" + messageType + ", dataLength="
            + dataLength + ", from=" + from + ", fromPort=" + fromPort
            + ", reserved1=" + reserved1 + ", reserved2=" + reserved2
            + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
    }
}
