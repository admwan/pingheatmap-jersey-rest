package net.spikesync.pingerdaemonrabbitmqclient;

import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class PingMsgReaderRunnable implements Runnable {

	private volatile boolean isRunning = true;

	private static final Logger logger = LoggerFactory.getLogger(PingMsgReaderRunnable.class);
	private ApplicationContext context;
	private PingMsgReader pingMsgReader;
	private PingHeatMap pingHeatMap;

	public PingMsgReaderRunnable() {
		this.context = new GenericXmlApplicationContext("classpath:beans.xml");

		this.pingMsgReader = this.context.getBean(PingMsgReader.class);
		if (this.pingMsgReader != null) {
			logger.debug("PingMsgReader initialized as: " + this.pingMsgReader.toString());
		} else
			logger.debug("PingMsgReader NOT initialized!");

		this.pingHeatMap = this.context.getBean(PingHeatMap.class);
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				logger.debug("Task readRmqAndUpdatePingHeatMap is running ...");

				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}

				logger.debug("Method run() executed in PingMsgReaderThread -- output from LOGGER");
				logger.debug("Calling readRmqAndUpdatePingHeatMap(String... args) from run()");
				readRmqAndUpdatePingHeatMap();
			}
		} catch (InterruptedException e) {
			logger.debug("Task readRmqAndUpdatePingHeatMap was INTERRUPTED, stopping thread!!!!!!!!!!!!!!!");
			this.isRunning = false;
			return;
		}
	}

	public void readRmqAndUpdatePingHeatMap() {



		// In this project everything needed by PingMsgReader is injected at
		// bean-construction time, so it is ready to be used!
			boolean connectionEstablished;
			connectionEstablished = this.pingMsgReader.connectPingMQ();
			if (connectionEstablished) {
				ArrayList<PingEntry> newPingEnties = this.pingMsgReader.createPingEntriesFromRabbitMqMessages();
				this.pingHeatMap.setPingHeat(newPingEnties);
				this.pingHeatMap.printPingHeatMap();
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