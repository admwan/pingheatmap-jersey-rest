package net.spikesync.pingerdaemonrabbitmqclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class PingMsgReaderThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(PingerdaemonRabbitmqClientApplication.class);
	private ApplicationContext context;
	private PingMsgReader pingMsgReader;
	private PingHeatMap pingHeatMap;

	public PingMsgReaderThread() {
		this.context = new GenericXmlApplicationContext("classpath:beans.xml");
		
		this.pingMsgReader = this.context.getBean(PingMsgReader.class);
		if(this.pingMsgReader != null) {
			logger.debug("PingMsgReader initialized as: " + this.pingMsgReader.toString());
			System.out.println("PingMsgReader initialized as: " + this.pingMsgReader.toString());
		}
		else logger.debug("PingMsgReader NOT initialized!");
		
		this.pingHeatMap = this.context.getBean(PingHeatMap.class);
	}

	@Override 
	public void run() {
		logger.debug("Method run() executed in PingMsgReaderThread");
		System.out.println("Method run() executed in PingMsgReaderThread");

	}
}
