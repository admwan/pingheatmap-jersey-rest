package net.spikesync.webapp;

import java.io.IOException;
import java.io.PrintWriter;

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

public class PingHeatMapServlet extends HttpServlet {

	private static final long serialVersionUID = -2535788907381898485L;

	private static final Logger logger = LoggerFactory.getLogger(PingHeatMapServlet.class);

	@Override
	public void init(ServletConfig config) {
		logger.debug("****************** PingHeatMapServlet is being initialized ######################################");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Handle GET requests
		logger.debug("In PingHeatMapServlet doGet");

		// Set response content type and status
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		// Send JSON data as response
		PrintWriter out = response.getWriter();

		// Retrieve the pingMessageReaderTask from the ServletContex to obtain the Ping
		// Heatmap and put it in the response.
		ServletContext servletContext = request.getServletContext();
		PingMsgReaderRunnable pingMessageReaderTask = (PingMsgReaderRunnable) servletContext.getAttribute("pingMessageReaderTask");
		if (pingMessageReaderTask != null) {
			String jsonResponse = pingMessageReaderTask.getPingHeatMap().getPingHeatMapAsString();
			out.print(jsonResponse);
			out.flush();
		} else
			out.println("ERROR! The pingMessageReaderTask doesn't exist!!");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paramWidth = request.getParameter("width");
		int width = Integer.parseInt(paramWidth);

		String paramHeight = request.getParameter("height");
		int height = Integer.parseInt(paramHeight);

		long area = width * height;

		PrintWriter writer = response.getWriter();
		writer.println("<html>Area of the rectangle is: " + area + "</html>");
		writer.flush();
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/index.jsp");
		System.out.println("Now in PingHeatMapServlet.doPost with dispatcher toString: " + dispatcher.toString());
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		System.out.println("PingHeatMapServlet is being destroyed");
	}

}
