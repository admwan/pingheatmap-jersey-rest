package net.spikesync.pingerdaemonrabbitmqclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(PingMsgReader.class);
	private static String FILENAME_PROPERTIES = "new_no_springboot.properties";
	
    public static Properties loadProperties() { 
        Properties configuration = new Properties();
        InputStream inputStream = PropertiesLoader.class
          .getClassLoader()
          .getResourceAsStream(FILENAME_PROPERTIES);
        try {
        	if(inputStream!=null) {
        		configuration.load(inputStream);
        		logger.debug("*Properties test in PropertiesLoader* ------- Value of test-pingerdaemon-context: " + configuration.getProperty("test-pingerdaemon-context"));
        		inputStream.close();
        	}
        	else throw (new IOException("------------- The properties file with name: " + FILENAME_PROPERTIES + " CAN NOT BE FOUND!!!!!!\n"
        			+ "Properties for this application will not be read from file!! "));
		} catch (IOException e) {
			e.printStackTrace(); // If the file is not present this will print the message above, otherwise something else is going wrong!!
			return null; // Discard the Properties object: return null instead of an object without properties.
		}
        return configuration;
    }
}
