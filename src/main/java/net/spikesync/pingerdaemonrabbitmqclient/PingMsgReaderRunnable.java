package net.spikesync.pingerdaemonrabbitmqclient;

import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class PingMsgReaderRunnable implements Runnable {
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
		logger.debug("Method run() executed in PingMsgReaderThread -- output from LOGGER");
		logger.debug("Calling readRmqAndUpdatePingHeatMap(String... args) from run()");
		readRmqAndUpdatePingHeatMap("Execute main loop");
	}

	public void readRmqAndUpdatePingHeatMap(String... args) {

		for (int i = 0; i < args.length; ++i) {
			logger.info("args[{}]: {}", i, args[i]);
		}

		logger.debug("Now starting listener with devPingApp..connectPingMQ(context) --------------------**********");

		// In this project everything needed by PingMsgReader is injected at
		// bean-construction time, so it is ready to be used!
		while (true) {
			boolean connectionEstablished;
			connectionEstablished = this.pingMsgReader.connectPingMQ();
			if (connectionEstablished) {
				ArrayList<PingEntry> newPingEnties = this.pingMsgReader.createPingEntriesFromRabbitMqMessages();
				this.pingHeatMap.setPingHeat(newPingEnties);
				this.pingHeatMap.printPingHeatMap();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
