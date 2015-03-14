package remi.discovery.test;
import java.util.Map.Entry;

import remi.discovery.Discovery;
import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.ServiceInfo;


public class ServerTest{

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {	    
	   
	    ServerTest main = new ServerTest();
	    ServiceInfo service = new ServiceInfo("remi.RemoteControl");
	    service.getParameters().put("port", "9997");
	    
	    Discovery.registerService(service);
	}

}
