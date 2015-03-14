package remi.discovery.test;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import remi.discovery.Discovery;
import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.ServiceInfo;


public class ClientTest implements OnDiscoveryListener{

	Logger logger = LogManager.getLogger(ClientTest.class);
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {	    
	    DiscoveryRequest req = new DiscoveryRequest("remi.RemoteControl");
	    Discovery.discover(req, new ClientTest());
	}

	@Override
	public void onServiceFound(ServiceInfo service) {
		logger.debug("------> SERVICE INFO FOUND - " + service.getServiceName() + " -> " + service.getInetAddress());
		for(Entry<String,String> entry : service.getParameters().entrySet()){
			logger.debug("------> " + entry.getKey() + " " +entry.getValue());
		}
		Discovery.stopDiscover(this);
	}

}
