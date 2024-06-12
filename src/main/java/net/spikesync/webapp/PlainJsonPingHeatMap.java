package net.spikesync.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.spikesync.api.AjaxResponseBody;
import net.spikesync.api.SimplePingHeat;
import net.spikesync.pingerdaemonrabbitmqclient.PingHeatMap;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderRunnable;

public class PlainJsonPingHeatMap extends HttpServlet {

	private static final long serialVersionUID = -2248728755385111853L;
	private static final Logger logger = LoggerFactory.getLogger(PlainJsonPingHeatMap.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jsonHttpMessageConverter;
    
	@Override
	public void init(ServletConfig config) {
		logger.debug("****************** PlainJsonPingHeatMap is being initialized ######################################");
	       try {
			super.init();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Handle GET requests
		logger.debug("In PingHeatMapServlet doGet");

		AjaxResponseBody ajaxReBo = new AjaxResponseBody();
	
		
		// Set response content type and status
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		// Retrieve the pingMessageReaderTask from the ServletContex to obtain the Ping
		// Heatmap and put it in the response.
		ServletContext servletContext = request.getServletContext();
		PingHeatMap pingHeatMap = ((PingMsgReaderRunnable) servletContext.getAttribute("pingMessageReaderTask")).getPingHeatMap();
		
		// We hebben hier nix aan die AjaxResponseBody want we moeten met de servlet writer alles gewoon letterlijk uitschrijven!!
		// NIET DUS want ik moet hier JSON teruggeven (retourneren) en niet iets met de servlet writer schrijven!
		
		ajaxReBo.setPingNodeList(pingHeatMap.getSilverCloudNodeNameList());
		ajaxReBo.setPingMatrixData(pingHeatMap.getPiHeMaAsSimplePingHeatList());
//		ArrayList<String> nodeNameList= pingHeatMap.getSilverCloudNodeNameList();
//		ArrayList<SimplePingHeat> simplePingHeatList = pingHeatMap.getPiHeMaAsSimplePingHeatList();
		
		try {
			jsonHttpMessageConverter.getObjectMapper().writeValue(response.getOutputStream(), ajaxReBo);
		}
		catch (IOException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unable to convert pingHeatMapResponse to JSON\"}");
        }

	}

	

}
