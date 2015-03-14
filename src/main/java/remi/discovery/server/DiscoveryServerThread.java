package remi.discovery.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import remi.discovery.Discovery;
import remi.discovery.server.pojo.SocketRequest;
import remi.discovery.server.pojo.SocketResponse;


public class DiscoveryServerThread implements Runnable {

	Logger logger = LogManager.getLogger(DiscoveryServerThread.class);
	private static AtomicBoolean started = new AtomicBoolean();
	
	DatagramSocket socket;

	@Override
	public void run() {
		try {
			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
			socket.setSoTimeout(10000);
			socket.setBroadcast(true);

			while (started.get()) {
				try{
					//logger.debug(getClass().getName() + ">Ready to receive broadcast packets!");
	
					//Receive a packet
					byte[] recvBuf = new byte[15000];
					DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
					socket.receive(packet);
	
					SocketRequest request = buildRequest(packet);
					SocketResponse response = new SocketResponse();
					Discovery.onReceive(request, response);
	
					if(response.getMessage() != null){
						byte[] sendData = response.getMessage().getBytes();
						//Send a response
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
						socket.send(sendPacket);
						logger.debug(getClass().getName() + ">Sent packet to: " + sendPacket.getAddress().getHostAddress() + "= " + response.getMessage());
					}
				}catch(SocketTimeoutException ste){
					// chance to check if server was requested to stop;
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}



	private SocketRequest buildRequest(DatagramPacket packet) {
		logger.debug(getClass().getName() + ">Discovery packet received from: " + packet.getAddress().getHostAddress());
		logger.debug(getClass().getName() + ">Packet received; data: " + new String(packet.getData()));
		String message = new String(packet.getData()).trim();
		return new SocketRequest(packet.getAddress().getHostAddress(), message);
	}

	public static DiscoveryServerThread getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {

		private static final DiscoveryServerThread INSTANCE = new DiscoveryServerThread();
	}

	public synchronized void start() {
		started.set(true);
	}

	public synchronized void stop() {
		started.set(false);
	}

}

