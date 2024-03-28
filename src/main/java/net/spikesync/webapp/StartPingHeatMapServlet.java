package net.spikesync.webapp;

import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderRunnable;

public class StartPingHeatMapServlet extends HttpServlet {

	// private static final long serialVersionUID = -2535788919381898485L;
	private static final Logger logger = LoggerFactory.getLogger(StartPingHeatMapServlet.class);

	@Override
	public void init(ServletConfig config) {
		logger.debug("!!!!!!!!!!!!!!!!!! StartPingHeatMapServlet is being initialized ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Process GET request to retrieve user data
		// ... (Retrieve user data from a database or other source)

		// Generate JSON response representing user data
		String jsonResponse = "{\"id\": 1, \"name\": \"StartPingHeatMapServlet!\"}";

		// Set response content type and status
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		// Send JSON data as response
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String serviceCommand = request.getParameter("command");

		PrintWriter writer = response.getWriter();

		if (serviceCommand.equals("start")) {
			ServletContext servletContext = request.getServletContext();
			PingMsgReaderRunnable pingMessageReaderTask = (PingMsgReaderRunnable) servletContext
					.getAttribute("pingMessageReaderTask");
			Executor executor = getExecutor("pingHeatMapThreadpool");
			LifecycleState lifecycleState = null;
			if ((pingMessageReaderTask != null) && (executor != null)) {
				lifecycleState = executor.getState();
				logger.debug("LifecycleState of org.apache.catalina.Executor this.executor: " + lifecycleState.toString());

				if(lifecycleState.equals(LifecycleState.STARTED)) {
				
					logger.debug("State of the executor is STARTED");
					executor.execute(pingMessageReaderTask);
				}
				
				writer.flush();
			} else {
				writer.println("PingMessageReaderTask CANNOT be started!");
				writer.flush();

			}
		} else {
			writer.println("<html>Command given to StartPingHeatMapServlet is NOT RECOGNIZED!</html>");
			writer.flush();
		}
	}

	/*-
	 * A RequestDispatcher is intended to wrap Servlets, so it's not appropriate
	 * here 
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/index.jsp");
		logger.debug("Now in UserServlet.doPost with dispatcher toString: " + dispatcher.toString());
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 */

	private Executor getExecutor(String executorName) {
		Executor executor = null;
		// Obtain the MBean server
		MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);

		try {
			ObjectName objectName = new ObjectName("Catalina", "type", "Server");
			StandardServer server = (StandardServer) mBeanServer.getAttribute(objectName, "managedResource");

			// Get the Service
			Service service = (StandardService) server.findService("Catalina");

			// Get the Executor. It must be defined in
			// /opt/tomcat/apache-tomcat-10.1.17/conf/server.xml !!!
			executor = ((StandardService) service).getExecutor(executorName);

		} catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | AttributeNotFoundException
				| ReflectionException allExceptions) {
			allExceptions.printStackTrace();
		}
		return executor;
	}

	@Override
	public void destroy() {
		logger.debug("DummyServlet is being destroyed");
	}

}
