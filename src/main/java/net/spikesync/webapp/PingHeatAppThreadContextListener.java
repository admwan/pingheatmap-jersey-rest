package net.spikesync.webapp;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import net.spikesync.pingerdaemonrabbitmqclient.PingHeatMap;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReader;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderRunnable;
import net.spikesync.pingerdaemonrabbitmqclient.PropertiesLoader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.ExecutorService;



public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderRunnable pingMessageReaderTask;
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
 		try {
 			Context ctx = new InitialContext();
            ExecutorService managedExecutorService = (ExecutorService) ctx.lookup("java:comp/DefaultManagedExecutorService");

            managedExecutorService.submit(pingMessageReaderTask);
 		}
 		catch (NamingException e) {
 			e.printStackTrace();
 		}
		pingMessageReaderTask = new PingMsgReaderRunnable();
		logger.debug("Started pingMessageReaderTask");
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		pingMessageReaderTask.notify();
	}
}
