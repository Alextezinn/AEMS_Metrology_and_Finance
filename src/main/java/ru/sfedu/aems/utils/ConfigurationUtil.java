package ru.sfedu.aems.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration utility. Allows to get configuration properties from the
 * default configuration file
 *
 * @author Boris Jmailov
 */
public class ConfigurationUtil {
    private final static Logger log =  LogManager.getLogger(ConfigurationUtil.class);
    private static final String CONFIG_PATH = System.getProperty("config");
    private static final String DEFAULT_CONFIG_PATH = "./src/main/resources/enviroment.properties";
    private static final Properties configuration = new Properties();
    /**
     * Hides default constructor
     */
    public ConfigurationUtil() {
    }

    private static Properties getConfiguration() throws IOException {
        if(configuration.isEmpty()){
            loadConfiguration();
        }
        return configuration;
    }

    /**
     * Loads configuration from <code>DEFAULT_CONFIG_PATH</code>
     * @throws IOException In case of the configuration file read failure
     */
    private static void loadConfiguration() throws IOException{
        File configFile;
        configFile = CONFIG_PATH == null ? new File(DEFAULT_CONFIG_PATH) : new File(CONFIG_PATH);
        InputStream in = new FileInputStream(configFile);
        log.info("Configuration path: " + configFile);
        try {
            configuration.load(in);
        } catch (IOException e) {
            log.error(e);
            throw new IOException(e);
        } finally{
            in.close();
        }
    }
    /**
     * Gets configuration entry value
     * @param key Entry key
     * @return Entry value by key
     * @throws IOException In case of the configuration file read failure
     */
    public static String getConfigurationEntry(String key) throws IOException{
        return getConfiguration().getProperty(key);
    }
}
