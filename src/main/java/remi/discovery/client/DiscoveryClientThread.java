package remi.discovery.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import remi.discovery.client.pojo.ClientRequest;
import remi.discovery.client.pojo.ClientRequestHolder;
import remi.discovery.client.pojo.ClientResponse;


public class DiscoveryClientThread implements Runnable {

	Logger logger = LogManager.getLogger(DiscoveryClientThread.class);
	private static Queue<ClientRequestHolder> requests = new ArrayBlockingQueue<ClientRequestHolder>(50, true);
	private static Thread dt = null;
	DatagramSocket c;

	@Override
	public void run() {

		// Find the server using UDP broadcast
		try {
			//Open a random port to send the package
			c = new DatagramSocket();
			c.setSoTimeout(2000);
			c.setBroadcast(true);

			while(!requests.isEmpty()){
				ClientRequestHolder req = requests.poll();
				if (req == null){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					try{
						sendRequest(req);
					}catch(SocketTimeoutException e){
						// consumes the request
						//requests.offer(req);
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			//Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
		}finally{
			//Close the port!
			c.close();
		}
	}

	private void sendRequest(ClientRequestHolder req) throws IOException {


		byte[] sendData = req.getReq().getMessage().getBytes();

		//Try the 255.255.255.255 first
		try {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
			c.send(sendPacket);
			logger.debug(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
		} catch (Exception e) {
		}

		// Broadcast the message over all the network interfaces
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();

			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue; // Don't want to broadcast to the loopback interface
			}

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast == null) {
					continue;
				}

				// Send the broadcast package!
				try {
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
					c.send(sendPacket);
				} catch (Exception e) {
				}

				logger.debug(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
			}
		}

		logger.debug(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

		//Wait for a response
		byte[] recvBuf = new byte[15000];
		DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
		c.receive(receivePacket);

		//We have a response
		logger.debug(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

		ClientResponse resp = buildResponse(receivePacket);

		req.getCallback().onResponse(resp);


	}

	private ClientResponse buildResponse(DatagramPacket receivePacket) {
		String message = new String(receivePacket.getData()).trim();
		return new ClientResponse(message, receivePacket.getAddress());
	}

	public static void sendRequest(ClientRequest req, OnResponseListener callback) {
		requests.offer(new ClientRequestHolder(req, callback));
		if(dt == null || !dt.isAlive()) {
			dt = new Thread(DiscoveryClientThread.getInstance(), "discoverClientThread");
			dt.start();
		};
	}

	public static DiscoveryClientThread getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {

		private static final DiscoveryClientThread INSTANCE = new DiscoveryClientThread();
	}

}

