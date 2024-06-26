package net.spikesync.pingerdaemonrabbitmqclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContext;

public class CoolDownRunnable implements Runnable {

	private volatile boolean isRunning = true;

	private static final Logger logger = LoggerFactory.getLogger(CoolDownRunnable.class);
	private ApplicationContext context;
	private PingMsgReader pingMsgReader;
	private PingHeatMap pingHeatMap;

	public CoolDownRunnable(ServletContext servletContext) {
		// Obtain the Spring application context from the servlet context
		logger.debug("In constructor-CoolDownRunnable");
		this.context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		// The CoolDown service only needs the PingHeatmap, and doesn't read messages from the queue, unlike the PingMessage update service)
		this.pingHeatMap = this.context.getBean(PingHeatMap.class);

	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				logger.debug("Task coolDownTask is running ...");

				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}

				logger.debug("Method run() executed in CoolDownRunnable -- output from LOGGER");
				this.pingHeatMap.coolDownPingHeat();
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			logger.debug("Task readRmqAndUpdatePingHeatMap was INTERRUPTED, stopping thread!!!!!!!!!!!!!!!");
			this.isRunning = false;
			return;
		}
	}


	/*
	 * A method to access the Ping Heatmap, for e.g., displaying it in in a webpage
	 */

	public PingHeatMap getPingHeatMap() {
		return this.pingHeatMap;
	}

	public void stop() {
		logger.debug("Method PingMsgReaderRunnable.stop() was called. Setting isRunning to false!");
		isRunning = false;

	}
	
	public boolean isRunningState() {
		return this.isRunning;
	}
	
	public void restart() {
		isRunning = true;
		run();
	}
}
