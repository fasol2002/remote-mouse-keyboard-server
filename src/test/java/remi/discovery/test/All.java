package remi.discovery.test;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import remi.discovery.Discovery;
import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.ServiceInfo;


public class All implements OnDiscoveryListener{

	Logger logger = LogManager.getLogger(All.class);
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {	    
	   
	    All main = new All();
	    ServiceInfo service = new ServiceInfo("remi.RemoteControl");
	    service.getParameters().put("port", "1234");
	    service.getParameters().put("piou", "piou");
	    
	    Discovery.registerService(service);
	    
	    DiscoveryRequest req = new DiscoveryRequest("remi.RemoteControl");
	    Discovery.discover(req, main);
	    
	    Thread.sleep(5000);
	    
	    Discovery.stopDiscover(main);
	    Discovery.unregisterService(service);
	}

	@Override
	public void onServiceFound(ServiceInfo service) {
		logger.debug("------> SERVICE INFO FOUND - " + service.getServiceName() + " -> " + service.getInetAddress());
		for(Entry<String,String> entry : service.getParameters().entrySet()){
			logger.debug("------> " + entry.getKey() + " " +entry.getValue());
		}
		
	}

}
