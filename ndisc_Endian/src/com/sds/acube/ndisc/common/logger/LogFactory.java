package com.sds.acube.ndisc.common.logger;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

public class LogFactory
{
    private static boolean bConfigured;

    // Log4J 환경설정.
    public static void configure()
    {
        configure("/log4j_ndisc.properties");
    }

    // Log4J 환경설정.
    public static void configure(String propertyFile)
    {
        Properties props = new Properties();
        InputStream fis = null;
        try
        {
            Class c = (new LogFactory()).getClass();
            fis = c.getResourceAsStream(propertyFile);

            props.load(fis);
            PropertyConfigurator.configure(props);
            System.out.println("[LOG4J] configuration file loaded : " + propertyFile);
        }
        catch (Exception e)
        {
            System.out.println("[LOG4J] " + e.getClass().getName() + " : " + e.getMessage());
            System.out.println("[LOG4J] Can not read file from CLASSPATH :  " + propertyFile);
            System.out.println("[LOG4J] Now using BasicConfigurator...");

            BasicConfigurator.configure();
        }
        finally
        {
            try {
                fis.close();
            } catch (Exception e) {}
        }

        bConfigured = true;
    }

    public static Logger getLogger(String moduleName)
    {
        if (bConfigured == false) {
            configure();
        }

        return Logger.getLogger(moduleName);
    }

    public static Logger getLogger(Class moduleClass)
    {
        if (bConfigured == false) {
            configure();
        }

        return Logger.getLogger(moduleClass);
    }
}
