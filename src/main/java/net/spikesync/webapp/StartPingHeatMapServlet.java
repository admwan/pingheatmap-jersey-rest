package net.spikesync.webapp;

import jakarta.servlet.http.HttpServlet;

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
		
		ServletContext servletContext = request.getServletContext();
		
		String serviceCommand = request.getParameter("command");
		PingMsgReaderRunnable pingMessageReaderTask = (PingMsgReaderRunnable) servletContext.getAttribute("pingMessageReaderTask");
		//pingMessageReaderTask = null;

		PrintWriter writer = response.getWriter();

		if (serviceCommand.equals("start")) {
			if(pingMessageReaderTask==null) {
			writer.println("<html>Start command received for service StartPingHeatMapServlet.\n");
			writer.println("Attempting to start PingMessageReaderTask ...</html>");
			writer.flush();
			}
			else {
				writer.println("<html>Start command received for service StartPingHeatMapServlet.\n");
				writer.println("PingMessageReaderTask ALREADY STARTED!! Command ignored.</html>");
				writer.flush();
				
			}
		} else {
			writer.println("<html>Command given to StartPingHeatMapServlet is NOT RECOGNIZED!</html>");
			writer.flush();
		}
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

	}

	@Override
	public void destroy() {
		logger.debug("DummyServlet is being destroyed");
	}

}
