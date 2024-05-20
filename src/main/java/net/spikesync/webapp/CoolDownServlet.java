package net.spikesync.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.spikesync.pingerdaemonrabbitmqclient.CoolDownRunnable;

public class CoolDownServlet extends HttpServlet {

	
	private static final Logger logger = LoggerFactory.getLogger(CoolDownServlet.class);

	@Override
	public void init(ServletConfig config) {
		logger.debug("****************** CoolDownServlet is being initialized ######################################");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Handle GET requests
		logger.debug("In CoolDownServlet doGet");

		// Set response content type and status
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		// Send JSON data as response
		PrintWriter out = response.getWriter();

		// Retrieve the pingMessageReaderTask from the ServletContex to obtain the Ping
		// Heatmap and put it in the response.
		ServletContext servletContext = request.getServletContext();
		CoolDownRunnable coolDownTask = (CoolDownRunnable) servletContext.getAttribute("coolDownTask");
		if (coolDownTask != null) {
			String jsonResponse = coolDownTask.getPingHeatMap().getPingHeatMapAsString();
			out.print(jsonResponse);
			out.flush();
		} else
			out.println("ERROR! The coolDownTask doesn't exist!!");
	}


}
