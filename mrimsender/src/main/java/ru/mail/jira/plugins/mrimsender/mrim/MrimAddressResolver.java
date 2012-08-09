/*
 * Created by Andrey Markelov 29-08-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.mrimsender.mrim;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class resolve MRIM actual hostname and port.
 * 
 * @author Andrey Markelov
 */
class MrimAddressResolver
{
    /**
     * MRIM default host.
     */
    private static final String MRIM_HOST = "http://mrim.mail.ru:2042";

    /**
     * Get actual MRIM hostname and port.
     */
    public static MrimHostPort getMrinHostPort()
    throws MrimException
    {
        try
        {
            //--> open connection
            URLConnection urlConn = new URL(MRIM_HOST).openConnection();

            //--> read actual host and port
            InputStream in = urlConn.getInputStream();
            byte[] iBuff = new byte[in.available()];
            in.read(iBuff);

            String iStr = new String(iBuff);
            if (iStr != null) iStr = iStr.trim();

            //--> parse host and port
            int linx = iStr.lastIndexOf(":");

            return new MrimHostPort(
                iStr.substring(0, linx),
                Integer.parseInt(iStr.substring(linx + 1)));
        }
        catch (StringIndexOutOfBoundsException siobex)
        {
            throw new MrimException(siobex, MrimErrors.ERROR_INCORRECT_ACTUAL_MRIM_HOST_NAME);
        }
        catch (NumberFormatException nex)
        {
            throw new MrimException(nex, MrimErrors.ERROR_INCORRECT_ACTUAL_MRIM_HOST_NAME);
        }
        catch (Exception ex)
        {
            throw new MrimException(ex, MrimErrors.ERROR_GET_ACTION_HOSTNAME);
        }
    }
}
