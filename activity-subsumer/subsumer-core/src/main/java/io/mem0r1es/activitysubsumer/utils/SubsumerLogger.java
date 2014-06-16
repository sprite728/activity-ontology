package io.mem0r1es.activitysubsumer.utils;

import org.apache.log4j.*;

/**
 * Logger class for the module
 * @author Ivan Gavrilović
 */
public class SubsumerLogger extends Logger{
    private static SubsumerLogger ourInstance = null;

    public static SubsumerLogger get(String className) {
        if (ourInstance == null) ourInstance = new SubsumerLogger(className);
        return ourInstance;
    }

    private SubsumerLogger(String className) {
        super(className);

        BasicConfigurator.configure();
    }
}
