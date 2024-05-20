package net.spikesync.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.spikesync.pingerdaemonrabbitmqclient.CoolDownRunnable;

public class CoolDownServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(CoolDownServlet.class);

	@Override
	public void init(ServletConfig config) {
		logger.debug("****************** CoolDownServlet is being initialized ######################################");
	}

	/*
	 * For the time being no method doGet.
	 */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		logger.debug("!!!!!!!!!!!!!!! in CoolDownServlet method POST !!!!!!!!!!!!");
		String serviceCommand = request.getParameter("command");

		PrintWriter writer = response.getWriter();

		if (serviceCommand.equals("start")) {

			// The objects below should be initialized by Spring IOC, not like this. TBD!!!
			ServletContext servletContext = request.getServletContext();
			CoolDownRunnable coolDownTask = (CoolDownRunnable) servletContext.getAttribute("coolDownTask");
			ExecutorService executor = (ExecutorService) servletContext.getAttribute("coolDownExecutorService");
			if (executor != null) {

				if (coolDownTask.isRunningState()) { // If the state of the Runnable is in "isRunning" than start it in
														// the
														// Executor

					Future<?> futureCoolDownTask = executor.submit(coolDownTask);
					servletContext.setAttribute("futureCoolDownTask", futureCoolDownTask);
					boolean isdone = futureCoolDownTask.isDone();
					boolean isRunning = coolDownTask.isRunningState();
					if (isdone) {
						logger.debug("FUTURE: futureCoolDownTask IS in state \"done\"");
						writer.println("FUTURE: futureCoolDownTask IS in state \"done\"");
					} else {
						logger.debug("FUTURE: futureCoolDownTask is NOT in state \"done\"");
						writer.println("FUTURE: futureCoolDownTask is NOT in state \"done\"");
					}
					if (isRunning) {
						logger.debug("RUNNABLE: coolDownTask IS in state \"isRunning\"");
						writer.println("RUNNABLE: coolDownTask IS in state \"isRunning\"");
					} else {
						logger.debug("RUNNABLE: coolDownTask is NOT in state \"coolDownTask\"");
						writer.println("RUNNABLE: coolDownTask is NOT in state \"coolDownTask\"");
					}

					logger.debug("coolDownTask submitted to coolDownExecutor!");
					writer.println("coolDownTask submitted to coolDownExecutor!");
					writer.flush();
				} else {
					coolDownTask.restart();
					logger.debug("coolDownTask RESTARTED from a stopped state!");
					writer.println("coolDownTask RESTARTED from a stopped state!");
					writer.flush();

				}
			}
			else {
				logger.debug("CoolDown Executor Service could not be found! Not starting CoolDown Service!");
			}
		} else if (serviceCommand.equals("stop")) {
			ServletContext servletContext = request.getServletContext();

			Future<?> futureCoolDownTask = (Future<?>) servletContext.getAttribute("futureCoolDownTask");

			boolean futureIsDone = futureCoolDownTask.isDone(); // There is no need to test for this condition
			boolean futureCancelled = futureCoolDownTask.isCancelled();
			logger.debug("Future done = " + futureIsDone + ", Future cancelled = " + futureCancelled);
			writer.println("Future done = " + futureIsDone + ", Future cancelled = " + futureCancelled);

			/*- Test for these conditions has undesirable results
			if (futureCoolDownTask != null					
					&& !futureCoolDownTask.isDone()
					&& !futureCoolDownTask.isCancelled()) {
			 */
			futureCoolDownTask.cancel(true);

			CoolDownRunnable coolDownTask = (CoolDownRunnable) servletContext.getAttribute("coolDownTask");
			coolDownTask.stop();

			logger.debug("futureCoolDownExecutor STOPPED after command stop. PingMsgReaderTask interrupted!");
			writer.println("futureCoolDownExecutor STOPPED after command stop. PingMsgReaderTask interrupted!");
			writer.flush();

			/*
			 * } else { logger.debug("futureCoolDownExecutor !!NOT!! STOPPED");
			 * writer.println("futureCoolDownExecutor !!NOT!! STOPPED"); writer.flush();
			 * 
			 * }
			 */

		} else {
			writer.println("Command given to StartPingHeatMapServlet is NOT RECOGNIZED!");
			writer.flush();
		}
	}

	public void destroy() {
		logger.debug("CoolDownServlet is being destroyed");

	}
}
