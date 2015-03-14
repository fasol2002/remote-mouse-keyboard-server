package remi.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import remi.discovery.client.DiscoveryProcessThread;
import remi.discovery.server.DiscoveryServerThread;
import remi.discovery.server.pojo.SocketRequest;
import remi.discovery.server.pojo.SocketResponse;


/**
 * 
 * 
 * C1 ----- S1
 * C2 ----- S2
 *    ----- S3
 * 
 * 
 * C1 who's there for "service name X" - S1 + S3
 * C2 who's there for "service name Y" - S2
 * 
 * 
 * @author remi
 *
 */
public class Discovery {

	static Logger logger = LogManager.getLogger(Discovery.class);

	private static Thread discoveryThread = null;
	private static Thread serverThread = null;
	//private static final ExecutorService clientPool= Executors.newFixedThreadPool(1);
	//private static final ExecutorService serverPool= Executors.newFixedThreadPool(1);
	private static final List<ServiceInfo> registeredServices = Collections.synchronizedList(new ArrayList<ServiceInfo>());


	//////////////////////////////////////////////// CLIENT ////////////////////////////////

	public static void discover(DiscoveryRequest req, OnDiscoveryListener callback) {
		logger.info("Service discovery: " + req.getServiceName());
		DiscoveryProcessThread.discover(req, callback);
		if(discoveryThread == null || !discoveryThread.isAlive()){
			discoveryThread = new Thread(new DiscoveryProcessThread(), "discoverProcessThread");
			discoveryThread.start();
		}
	}

	public static void stopDiscover(OnDiscoveryListener callback) {
		DiscoveryProcessThread.stopDiscover(callback);
	}


	//////////////////////////////////////////////// SERVER ////////////////////////////////


	public static synchronized void onReceive(SocketRequest request, SocketResponse response) {
		//"{service:'nameofservice', toto:'truc', titi:'machin'}";
		for(ServiceInfo service : registeredServices){
			if(service.getServiceName().equals(request.getMessage())){
				response.setMessage(ServiceInfo.toString(service));
			}
		}
	}

	public static void registerService(ServiceInfo service) {
		logger.info("Registering Service: " + service.getServiceName());
		if(serverThread ==null || !serverThread.isAlive()){
			DiscoveryServerThread.getInstance().start();
			serverThread = new Thread(DiscoveryServerThread.getInstance(), "discoverServerThread");
			serverThread.start();
		}
		registeredServices.add(service);
	}

	public static void unregisterService(ServiceInfo service) {
		logger.info("Unregistering Service: " + service.getServiceName());
		registeredServices.remove(service);
		if(serverThread.isAlive() && registeredServices.isEmpty()){
			DiscoveryServerThread.getInstance().stop();
		}
	}

}
