package org.foden.utils;

import org.foden.constants.FrameworkConstants;
import org.foden.enums.ConfigProperties;
import org.foden.exceptions.FrameworkException;
import org.foden.exceptions.InvalidPathForPropertyFileException;
import org.foden.exceptions.PropertyFileUsageException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class PropertyUtils {

    private PropertyUtils(){}
    private static Properties property = new Properties();
    private static final Map<String,String> CONFIGMAP = new HashMap<>();
    static {
        try{
            FileInputStream file = new FileInputStream(FrameworkConstants.getConfigFilePath());
            property.load(file);

            for (Map.Entry<Object, Object> entry : property.entrySet()){
                CONFIGMAP.put(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()).trim());
            }
        }
        catch (FileNotFoundException e){
            StackTraceElement[] a = e.getStackTrace();
            a[0] = new StackTraceElement("org.foden.utils.PropertyUtils","get","PropertyUtils.java",22);
            e.setStackTrace(a);
            throw new InvalidPathForPropertyFileException("Property file you trying to read is not found",e);
        }
        catch (IOException e){
            throw new FrameworkException("Some IOException happened while reading the property file!!!");
        }
    }

    /**
     * Get string.
     *
     * @param key the key
     * @return the string
     */
    public static String get(ConfigProperties key) {
        if (Objects.isNull(key) || Objects.isNull(CONFIGMAP.get(key.name().toLowerCase()))){
            try {
                throw new PropertyFileUsageException("Property name: " + key + " was not found !!!. Please check config.properties");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return CONFIGMAP.get(key.name().toLowerCase());
    }
}
