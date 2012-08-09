/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

/**
 * "message" package.
 * 
 * @author Andrey Markelov
 */
class MessageMrimPackage
    extends BaseMrimPackage
{
    /**
     * Message ID.
     */
    private int messageId;

    /**
     * Constructor.
     */
    public MessageMrimPackage(
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
        int reserved4,
        int messageId)
    {
        super(magic, proto, seq, messageType, dataLength, from, fromPort, reserved1, reserved2, reserved3, reserved4);
        this.messageId = messageId;
    }

    public int getMessageId()
    {
        return messageId;
    }

    @Override
    public String toString()
    {
        return "MessageMrimPackage[messageId=" + messageId + ", getDataLength()=" +
            getDataLength() + ", getFrom()=" + getFrom() + ", getFromPort()=" + getFromPort() +
            ", getMagic()=" + getMagic() + ", getMessageType()=" + getMessageType() +
            ", getProto()=" + getProto() + ", getReserved1()=" + getReserved1() +
            ", getReserved2()=" + getReserved2() + ", getReserved3()=" + getReserved3() +
            ", getReserved4()=" + getReserved4() + ", getSeq()=" + getSeq() + "]";
    }
}
