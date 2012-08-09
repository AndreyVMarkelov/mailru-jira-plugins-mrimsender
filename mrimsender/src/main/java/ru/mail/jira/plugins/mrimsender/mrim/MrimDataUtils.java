/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * This class contains utility methods for working with data.
 * 
 * @author Andrey Markelov
 */
class MrimDataUtils
{
    /**
     * Convert Little Endian <code>byte</code> array to <code>int</code> array.
     */
    public static int[] bytesToIntArray(byte[] buff)
    {
        //Pad the size to multiple of 4
        int size = (buff.length / 4) + ((buff.length % 4 == 0) ? 0 : 1);      

        ByteBuffer bb = ByteBuffer.allocate(size * 4);
        bb.put(buff);

        //Java uses Big Endian. Network program uses Little Endian.
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.rewind();
        IntBuffer ib =  bb.asIntBuffer();
        int [] result = new int [size];
        ib.get(result);

        return result;
    }

    /**
     * Pack integer value to Little Endian.
     */
    public final static byte[] intToLE(int iVal)
    {
        ByteBuffer  bb = ByteBuffer.allocate(Integer.SIZE/8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(iVal);

        return  bb.array();
    }

    /**
     * Pack integer value to Big Endian.
     */
    public final static byte[] intToBE(int iVal)
    {
        ByteBuffer  bb = ByteBuffer.allocate(Integer.SIZE/8);
        bb.putInt(iVal);

        return  bb.array();
    }

    /**
     * Pack integer value to Little Endian.
     */
    public final static byte[] longToLE(long lVal)
    {
        ByteBuffer  bb = ByteBuffer.allocate(Integer.SIZE/8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(lVal);

        return  bb.array();
    }

    /**
     * Private constructor.
     */
    private MrimDataUtils() {}
}
