package net.spikesync.pingerdaemonrabbitmqclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;

public class PropertiesLoader {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
	private static String FILENAME_PROPERTIES = "/WEB-INF/new_no_springboot.properties";
	private ServletContext servletContext;

	public PropertiesLoader(ServletContext sCtx) {
		this.servletContext = sCtx;
	}

	public Properties loadProperties() {
		Properties configuration = new Properties();

		try (InputStream inputStream = servletContext.getResourceAsStream(FILENAME_PROPERTIES)) {
			if (inputStream != null) {
				configuration.load(inputStream);
			} else {
				System.out.println("Properties file not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;

	}
}
/***** Below is the original body of the method *************/
/*  
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

 * 
 * !!!!!!!! This is the main difference with
 * PingerdaemonRabbitmqClientApplication !!!!!!!!!! 
 * The properties are being read through the classloader method. 
 * In general there are two methods to read the properties file:
 * 
 * PropertiesLoader loader = new PropertiesLoader();
 * 
 * --> Using ClassLoader Properties propertiesFromClasspath =
 * loader.loadPropertiesFromClasspath("yourfile.properties");
 * 
 * --> Using ServletContext Properties propertiesFromServletContext =
 * loader.loadPropertiesFromServletContext(servletContext,
 * "yourfile.properties");
 * 
 * InputStream inputStream = PropertiesLoader.class .getClassLoader()
 * .getResourceAsStream(FILENAME_PROPERTIES);
 * 
 * 
 * 
 * 
 
 */
