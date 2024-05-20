package net.spikesync.webapp;

import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderRunnable;

public class StartPingHeatMapServlet extends HttpServlet {

	private static final long serialVersionUID = -2535788919381898485L;
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
		PrintWriter writer = response.getWriter();
		writer.print(jsonResponse);
		writer.flush();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String serviceCommand = request.getParameter("command");

		PrintWriter writer = response.getWriter();

		if (serviceCommand.equals("start")) {

			ServletContext servletContext = request.getServletContext();
			PingMsgReaderRunnable pingMessageReaderTask = (PingMsgReaderRunnable) servletContext.getAttribute("pingMessageReaderTask");
			if (pingMessageReaderTask.isRunningState()) { // If the state of the Runnable is in "isRunning" than start it in the Executor
				ExecutorService executor = (ExecutorService) servletContext.getAttribute("pingHeatMapExecutor");

				Future<?> futurePingMessageReaderTask = executor.submit(pingMessageReaderTask);
				servletContext.setAttribute("futurePingMessageReaderTask", futurePingMessageReaderTask);
				boolean isdone = futurePingMessageReaderTask.isDone();
				boolean isRunning = pingMessageReaderTask.isRunningState();
				if (isdone) {
					logger.debug("FUTURE: futurePingMessageReaderTask IS in state \"done\"");
					writer.println("FUTURE: futurePingMessageReaderTask IS in state \"done\"");
				} else {
					logger.debug("FUTURE: futurePingMessageReaderTask is NOT in state \"done\"");
					writer.println("FUTURE: futurePingMessageReaderTask is NOT in state \"done\"");
				}
				if (isRunning) {
					logger.debug("RUNNABLE: pingMessageReaderTask IS in state \"isRunning\"");
					writer.println("RUNNABLE: pingMessageReaderTask IS in state \"isRunning\"");
				} else {
					logger.debug("RUNNABLE: pingMessageReaderTask is NOT in state \"pingMessageReaderTask\"");
					writer.println("RUNNABLE: pingMessageReaderTask is NOT in state \"pingMessageReaderTask\"");
				}

				logger.debug("pingMessageReaderTask submitted to pingHeatMapExecutor!");
				writer.println("pingMessageReaderTask submitted to pingHeatMapExecutor!");
				writer.flush();
			} else {
				pingMessageReaderTask.restart();
				logger.debug("pingMessageReaderTask RESTARTED from a stopped state!");
				writer.println("pingMessageReaderTask RESTARTED from a stopped state!");
				writer.flush();

			}

		} else if (serviceCommand.equals("stop")) {
			ServletContext servletContext = request.getServletContext();

			Future<?> futurePingMessageReaderTask = (Future<?>) servletContext.getAttribute("futurePingMessageReaderTask");

			boolean futureIsDone = futurePingMessageReaderTask.isDone(); // There is no need to test for this condition
			boolean futureCancelled = futurePingMessageReaderTask.isCancelled();
			logger.debug("Future done = " + futureIsDone + ", Future cancelled = " + futureCancelled);
			writer.println("Future done = " + futureIsDone + ", Future cancelled = " + futureCancelled);

			/*- Test for these conditions has undesirable results
			if (futurePingMessageReaderTask != null					
					&& !futurePingMessageReaderTask.isDone()
					&& !futurePingMessageReaderTask.isCancelled()) {
			 */
			futurePingMessageReaderTask.cancel(true);

			PingMsgReaderRunnable pingMessageReaderTask = (PingMsgReaderRunnable) servletContext.getAttribute("pingMessageReaderTask");
			pingMessageReaderTask.stop();

			logger.debug("futurePingHeatMapExecutor STOPPED after command stop. PingMsgReaderTask interrupted!");
			writer.println("futurePingHeatMapExecutor STOPPED after command stop. PingMsgReaderTask interrupted!");
			writer.flush();

			/*
			 * } else { logger.debug("futurePingHeatMapExecutor !!NOT!! STOPPED");
			 * writer.println("futurePingHeatMapExecutor !!NOT!! STOPPED"); writer.flush();
			 * 
			 * }
			 */

		} else {
			writer.println("Command given to StartPingHeatMapServlet is NOT RECOGNIZED!");
			writer.flush();
		}
	}

	public void destroy() {
		logger.debug("StartPingheatMapServlet is being destroyed");

	}
}

/*- The LifecyleState based logic is not applicable to ScheduledExecutorService.
 * This is code from a previous iteration when made use of Executor instead of ExecutorService.
 * ExecutorService has many advantages over the Executor class.

LifecycleState lifecycleState = null;

if ((pingMessageReaderTask != null) && (executor != null)) {
	lifecycleState = ((Executor) executor).getState();
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
} */

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

/*- Executor is Deprecated!!
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

	} catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException allExceptions) {
		allExceptions.printStackTrace();
	}
	return executor;
}
*/
