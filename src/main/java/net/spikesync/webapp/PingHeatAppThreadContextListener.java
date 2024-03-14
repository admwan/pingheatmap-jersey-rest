package net.spikesync.webapp;

import java.io.InputStream;
import java.rmi.registry.Registry;
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.Executor;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.Service;

import javax.management.*;
//import org.apache.catalina.ServerFactory;
import org.apache.catalina.Executor;

public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderRunnable pingMessageReaderTask;
	private static final Logger logger = LoggerFactory.getLogger(PingHeatAppThreadContextListener.class);
	private ThreadPoolExecutor executor;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();
		pingMessageReaderTask = new PingMsgReaderRunnable();

		PropertiesLoader propLoader = new PropertiesLoader(servletContext);
		Properties prop = propLoader.loadProperties();
		if (prop == null)
			logger.debug(
					"************** ========= Properties not loaded! Check the name of the properties file! ************** ========= ");
		else {
			logger.debug("************** ========= Property test-silvercloud-scnodes is set to: "
					+ prop.getProperty("test-silvercloud-scnodes"));
			logger.debug("All properties: ");
			prop.list(System.out);
		}
		// Obtain the MBean server
		MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);

		// Obtain the Catalina Server object
		Executor executor = null;
		try {
			ObjectName objectName = new ObjectName("Catalina", "type", "Server");
			StandardServer server = (StandardServer) mBeanServer.getAttribute(objectName, "managedResource");

			// Get the Service
			Service service = server.findService("Catalina");

			// Get the Executor
			executor = ((StandardService) service).getExecutor("pingHeatMapThreadpool");

		} catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | AttributeNotFoundException
				| ReflectionException allExceptions) {
			// TODO Auto-generated catch block
			allExceptions.printStackTrace();
		}
		

		if (executor != null) {
			executor.execute(pingMessageReaderTask);
			logger.debug("Started PingMessageReaderTask with managed threadpool!!! $$$$$$$$$$$ grepraaktindewarvandollars ***********");

		}
		else logger.debug("pingMessageReaderTask NOT STARTED &&&@@@");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		executor.shutdown();
		// executor.close();
	}
}
