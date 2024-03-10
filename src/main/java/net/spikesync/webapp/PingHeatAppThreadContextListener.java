package net.spikesync.webapp;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import net.spikesync.pingerdaemonrabbitmqclient.PingHeatMap;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReader;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderThread;
import net.spikesync.pingerdaemonrabbitmqclient.PropertiesLoader;

public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderThread pingMsgReaderThread;
	private static final Logger logger = LoggerFactory.getLogger(PingHeatAppThreadContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();

		PropertiesLoader propLoader = new PropertiesLoader(servletContext);
		Properties prop = propLoader.loadProperties();
 		if(prop == null) 
 			logger.debug("************** ========= Properties not loaded! Check the name of the properties file! ************** ========= ");
 		else {
 			logger.debug("************** ========= Property test-silvercloud-scnodes is set to: "  + prop.getProperty("test-silvercloud-scnodes"));
 			logger.debug("All properties: ");
 			prop.list(System.out);
 		}
		pingMsgReaderThread = new PingMsgReaderThread();
		pingMsgReaderThread.start();
		System.out.println("Started pingMsgReaderThread");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		pingMsgReaderThread.interrupt();
	}
}
