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

import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.Service;

import javax.management.*;

public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderRunnable pingMessageReaderTask;
	private static final Logger logger = LoggerFactory.getLogger(PingHeatAppThreadContextListener.class);
	private Executor executor;

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
		this.executor = null;
		try {
			ObjectName objectName = new ObjectName("Catalina", "type", "Server");
			StandardServer server = (StandardServer) mBeanServer.getAttribute(objectName, "managedResource");

			// Get the Service
			Service service = server.findService("Catalina");

			// Get the Executor
			this.executor = ((StandardService) service).getExecutor("pingHeatMapThreadpool");

		} catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | AttributeNotFoundException
				| ReflectionException allExceptions) {
			allExceptions.printStackTrace();
		}

		if (executor != null) {
			executor.execute(pingMessageReaderTask);
			logger.debug(
					"Started PingMessageReaderTask with managed threadpool!!! $$$$$$$$$$$ grepraaktindewarvandollars ***********");

		} else
			logger.debug("pingMessageReaderTask NOT STARTED &&&@@@");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		/* When the application is stopped or terminated Tomcat will clean up when the org.apache.catalina.Executor
		 * obtained from JMX is used.
		try {
			this.executor.destroy();
		} catch (LifecycleException e) {
			// From the API doc: this component, i.e., the Catalina Executor, detected a
			// fatal error that prevents this component from being used
			e.printStackTrace();
			logger.debug(
					"ERROR: this component, i.e., the Catalina Executor, detected a fatal error that prevents this component from being used."
							+ "The Thread Executor didn't shutdown properly!");
		}
		*/
		logger.debug("Tomcat JMX cleaned up used resources after contextDestroyed().");
	}
}
