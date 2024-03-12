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

import org.apache.catalina.Executor;
import org.apache.catalina.core.StandardThreadExecutor;

import javax.management.*;
//import org.apache.catalina.ServerFactory;
import org.apache.catalina.Executor;


public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderRunnable pingMessageReaderTask;
	private static final Logger logger = LoggerFactory.getLogger(PingHeatAppThreadContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();
		pingMessageReaderTask = new PingMsgReaderRunnable();

		PropertiesLoader propLoader = new PropertiesLoader(servletContext);
		Properties prop = propLoader.loadProperties();
 		if(prop == null) 
 			logger.debug("************** ========= Properties not loaded! Check the name of the properties file! ************** ========= ");
 		else {
 			logger.debug("************** ========= Property test-silvercloud-scnodes is set to: "  + prop.getProperty("test-silvercloud-scnodes"));
 			logger.debug("All properties: ");
 			prop.list(System.out);
 		}
 		/* The code below tries to retrieve an ExecutorService from the Tomcat server, but the lookup fails. 
 		 * Also, I can't find anything useful on how to fix the lookup, so trying something else 
 		 
 		try {
 			Context ctx = new InitialContext();
            ExecutorService managedExecutorService = (ExecutorService) ctx.lookup("java:comp/DefaultManagedExecutorService");

            managedExecutorService.submit(pingMessageReaderTask);
    		logger.debug("Started pingMessageReaderTask");

 		}
 		catch (NamingException e) {
 			e.printStackTrace();
 		}
		*/
 		/* The method below is too far fetched. A better solution is to use Spring
 		 MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
         ObjectName name = new ObjectName("Catalina:type=Server");
         org.apache.catalina.Server server = (org.apache.catalina.Server) mBeanServer.getAttribute(name, "managedResource");
         Executor executor = server.findExecutor("myExecutor");	
		*/
         Executor managedExecutorService = new StandardThreadExecutor();
 	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		pingMessageReaderTask.notify();
	}
}
