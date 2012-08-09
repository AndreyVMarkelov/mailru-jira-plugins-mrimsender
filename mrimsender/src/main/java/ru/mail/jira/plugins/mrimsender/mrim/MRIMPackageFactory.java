/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains static methods that creates MRIM packages.
 * 
 * @author Andrey Markelov
 */
public class MRIMPackageFactory
{
    /**
     * Create "add contact" package.
     */
    public static byte[] createAddContactPackage(
        int seq,
        String user,
        int host, 
        int port)
    {
        ByteBuffer bb = null;

        try
        {
            String phone = MrimConsts.EMPTY_STRING;
            byte[] flagBytes = MrimDataUtils.intToLE(0);
            byte[] oneLenBytes = MrimDataUtils.intToLE(0);

            byte[] userLenBytes = MrimDataUtils.intToLE(user.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] userBytes = user.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            byte[] userLenBytes1 = MrimDataUtils.intToLE(user.getBytes(MrimConsts.UTF16_MRIM_ENCODING).length);
            byte[] userBytes1 = user.getBytes(MrimConsts.UTF16_MRIM_ENCODING);

            byte[] phoneLenBytes = MrimDataUtils.intToLE(phone.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] phoneBytes = phone.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);

            int dLen =
                flagBytes.length +
                oneLenBytes.length +
                userLenBytes.length + userBytes.length +
                userLenBytes1.length + userBytes1.length +
                phoneLenBytes.length + phoneBytes.length +
                userLenBytes.length + userBytes.length +
                oneLenBytes.length;
            byte[] basePackage = createBaseMrimPackage(seq, MrimConsts.MRIM_CS_ADD_CONTACT, dLen, host, port);

            bb = ByteBuffer.allocate(basePackage.length + dLen);
            bb.put(basePackage);
            bb.put(flagBytes);
            bb.put(oneLenBytes);
            bb.put(userLenBytes);
            bb.put(userBytes);

            bb.put(userLenBytes1);
            bb.put(userBytes1);
            bb.put(phoneLenBytes);
            bb.put(phoneBytes);
            bb.put(userLenBytes);
            bb.put(userBytes);
            bb.put(oneLenBytes);
        }
        catch (UnsupportedEncodingException ex)
        {
            //--> impossible
        }

        return bb.array();
    }

    /**
     * Create "authorize" package.
     */
    public static byte[] createAuthorizePackage(
        int seq,
        String user,
        int host, 
        int port)
    {
        ByteBuffer bb = null;
        try
        {
            byte[] userLenBytes = MrimDataUtils.intToLE(user.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] userBytes = user.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);

            int dLen = userLenBytes.length + userBytes.length;
            byte[] basePackage = createBaseMrimPackage(seq, MrimConsts.MRIM_CS_AUTHORIZE, dLen, host, port);

            bb = ByteBuffer.allocate(basePackage.length + dLen);
            bb.put(basePackage);
            bb.put(userLenBytes);
            bb.put(userBytes);
        }
        catch (UnsupportedEncodingException ex)
        {
            //--> impossible
        }

        return bb.array();
    }

    /**
     * Create base MRIM package.
     */
    public static byte[] createBaseMrimPackage(
        int seq,
        int messType,
        int size,
        int host, 
        int port)
    {
        ByteBuffer  bb = ByteBuffer.allocate(44);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(MrimConsts.CS_MAGIC);
        bb.putInt(MrimConsts.PROTO_VERSION);
        bb.putInt(seq);
        bb.putInt(messType);
        bb.putInt(size);
        bb.put(MrimDataUtils.intToLE(host));
        bb.put(MrimDataUtils.intToLE(port));
        bb.putInt(0);
        bb.putInt(0);
        bb.putInt(0);
        bb.putInt(0);

        return bb.array();
    }

    /**
     * Create "login" package.
     */
    public static byte[] createLoginPackage(
        int seq,
        String login,
        String password,
        String client,
        int host, 
        int port)
    {
        ByteBuffer bb = null;
        try
        {
            //--> login
            byte[] loginLenBytes = MrimDataUtils.intToLE(login.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] loginBytes = login.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            //--> password
            byte[] passLenBytes = MrimDataUtils.intToLE(password.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] passBytes = password.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            //--> DWORD status
            byte[] dStatus = MrimDataUtils.intToLE(1);
            //--> sStatus
            byte[] statusLenBytes = MrimDataUtils.intToLE(MrimConsts.EMPTY_STRING.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] statusBytes = MrimConsts.EMPTY_STRING.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            //--> sStatus title
            byte[] tstatusLenBytes = MrimDataUtils.intToLE(MrimConsts.EMPTY_STRING.getBytes(MrimConsts.UTF16_MRIM_ENCODING).length);
            byte[] tstatusBytes = MrimConsts.EMPTY_STRING.getBytes(MrimConsts.UTF16_MRIM_ENCODING);
            //--> sStatus descr
            byte[] dstatusLenBytes = MrimDataUtils.intToLE(MrimConsts.EMPTY_STRING.getBytes(MrimConsts.UTF16_MRIM_ENCODING).length);
            byte[] dstatusBytes = MrimConsts.EMPTY_STRING.getBytes(MrimConsts.UTF16_MRIM_ENCODING);
            //--> user status
            byte[] userstatusLenBytes= MrimDataUtils.intToLE(MrimConsts.MRIM_CS_STATUS_ONLINE);
            //--> client
            byte[] clientLenBytes = MrimDataUtils.intToLE(client.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] clientBytes = client.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            //--> lang
            byte[] ruLenBytes = MrimDataUtils.intToLE(MrimConsts.RU_LOCALE.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] ruBytes = MrimConsts.RU_LOCALE.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            //--> empty
            byte[] emptyLenBytes = MrimDataUtils.intToLE(MrimConsts.EMPTY_STRING.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] emptyBytes = MrimConsts.EMPTY_STRING.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);

            int dLen =
                loginLenBytes.length + loginBytes.length +
                passLenBytes.length + passBytes.length +
                dStatus.length +
                statusLenBytes.length + statusBytes.length +
                tstatusLenBytes.length + tstatusBytes.length +
                dstatusLenBytes.length + dstatusBytes.length +
                userstatusLenBytes.length +
                clientLenBytes.length + clientBytes.length +
                ruLenBytes.length + ruBytes.length +
                emptyLenBytes.length + emptyBytes.length;
            byte[] basePackage = createBaseMrimPackage(seq, MrimConsts.MRIM_CS_LOGIN2, dLen, host, port);

            bb = ByteBuffer.allocate(basePackage.length + dLen);
            bb.put(basePackage);
            bb.put(loginLenBytes);
            bb.put(loginBytes);
            bb.put(passLenBytes);
            bb.put(passBytes);
            bb.put(dStatus);
            bb.put(statusLenBytes);
            bb.put(statusBytes);
            bb.put(tstatusLenBytes);
            bb.put(tstatusBytes);
            bb.put(dstatusLenBytes);
            bb.put(dstatusBytes);
            bb.put(userstatusLenBytes);
            bb.put(clientLenBytes);
            bb.put(clientBytes);
            bb.put(ruLenBytes);
            bb.put(ruBytes);
            bb.put(emptyLenBytes);
            bb.put(emptyBytes);
        }
        catch (UnsupportedEncodingException ex)
        {
            //--> impossible
        }

        return bb.array();
    }

    /**
     * Create "multi cast message" package.
     */
    public static byte[] createMesageMultiPackage(
        int seq,
        Collection<String> to,
        String message,
        int host, 
        int port)
    {
        ByteBuffer bb = null;

        try
        {
            byte[] flagBytes = MrimDataUtils.intToLE(MrimConsts.MESSAGE_FLAG_MULTICAST | MrimConsts.MESSAGE_FLAG_CP1251);

            int sum = 0;
            Map<Integer, byte[]> list = new HashMap<Integer, byte[]>();
            for (String t : to)
            {
                int len = t.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length;
                byte[] toLenBytes = MrimDataUtils.intToLE(t.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
                byte[] toBytes = t.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
                sum += (toLenBytes.length + toBytes.length);
                list.put(len, toBytes);
            }
            byte[] sumBytes = MrimDataUtils.intToLE(sum);

            byte[] messageLenBytes = MrimDataUtils.intToLE(message.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] messageBytes = message.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            byte[] zeroLenBytes = MrimDataUtils.intToLE(0);

            int dLen = (flagBytes.length + sumBytes.length + sum +
                    messageLenBytes.length + messageBytes.length + zeroLenBytes.length);
            byte[] basePackage = createBaseMrimPackage(seq, MrimConsts.MRIM_CS_MESSAGE, dLen, host, port);

            bb = ByteBuffer.allocate(basePackage.length + dLen);
            bb.put(basePackage);
            bb.put(flagBytes);
            bb.put(sumBytes);
            for (Map.Entry<Integer, byte[]> entry : list.entrySet())
            {
                bb.put(MrimDataUtils.intToLE(entry.getKey()));
                bb.put(entry.getValue());
            }
            bb.put(messageLenBytes);
            bb.put(messageBytes);
            bb.put(zeroLenBytes);
        }
        catch (UnsupportedEncodingException ex)
        {
            //--> impossible
        }

        return bb.array();
    }

    /**
     * Create single message.
     */
    public static byte[] createSimpleMessagePackage(
        int seq,
        String to,
        String message,
        int host, 
        int port)
    {
        return createMessagePackage(seq, MrimConsts.MESSAGE_FLAG_CP1251, to, message, host, port);
    }

    /**
     * Create "message" package.
     */
    public static byte[] createMessagePackage(
        int seq,
        int flag,
        String to,
        String message,
        int host, 
        int port)
    {
        ByteBuffer bb = null;
        try
        {
            byte[] flagBytes = MrimDataUtils.intToLE(flag);
            byte[] toLenBytes = MrimDataUtils.intToLE(to.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] toBytes = to.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            byte[] messageLenBytes = MrimDataUtils.intToLE(message.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING).length);
            byte[] messageBytes = message.getBytes(MrimConsts.DEFAULT_MRIM_ENCODING);
            byte[] zeroLenBytes = MrimDataUtils.intToLE(0);

            int dLen = (
                flagBytes.length +
                toLenBytes.length + toBytes.length +
                messageLenBytes.length + messageBytes.length +
                zeroLenBytes.length);
            byte[] basePackage = createBaseMrimPackage(seq, MrimConsts.MRIM_CS_MESSAGE, dLen, host, port);

            bb = ByteBuffer.allocate(basePackage.length + dLen);
            bb.put(basePackage);
            bb.put(flagBytes);
            bb.put(toLenBytes);
            bb.put(toBytes);
            bb.put(messageLenBytes);
            bb.put(messageBytes);
            bb.put(zeroLenBytes);
        }
        catch (UnsupportedEncodingException ex)
        {
            //--> impossible
        }

        return bb.array();
    }

    /**
     * Create "ping" package.
     */
    public static byte[] createPingPackage(int seq,int host, 
            int port)
    {
        return createBaseMrimPackage(seq, MrimConsts.MRIM_CS_PING, 0, host, port);
    }

    /**
     * Create "say hello" package.
     */
    public static byte[] createSayHelloPackage(
        int host, 
        int port)
    {
        return createBaseMrimPackage(0, MrimConsts.SAY_HELLO, 0, host, port);
    }

    /**
     * Private constructor.
     */
    private MRIMPackageFactory() {}
}
