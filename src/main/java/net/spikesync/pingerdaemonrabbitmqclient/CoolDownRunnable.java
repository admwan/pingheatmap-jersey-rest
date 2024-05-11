package net.spikesync.pingerdaemonrabbitmqclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class CoolDownRunnable implements Runnable {

	private volatile boolean isRunning = true;
	
	private static final Logger logger = LoggerFactory.getLogger(PingMsgReaderRunnable.class);
	private ApplicationContext context;
	private PingMsgReader pingMsgReader;
	private PingHeatMap pingHeatMap;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
