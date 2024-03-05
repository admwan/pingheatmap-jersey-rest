package net.spikesync.webapp;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReader;
import net.spikesync.pingerdaemonrabbitmqclient.PingMsgReaderThread;

public class PingHeatAppThreadContextListener implements ServletContextListener {

	private PingMsgReaderThread pingMsgReaderThread;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		pingMsgReaderThread = new PingMsgReaderThread();
		pingMsgReaderThread.start();
		System.out.println("Started pingMsgReaderThread");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		pingMsgReaderThread.interrupt();
	}
}
