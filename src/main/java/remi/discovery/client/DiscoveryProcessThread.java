package remi.discovery.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.ServiceInfo;
import remi.discovery.client.pojo.ClientRequest;
import remi.discovery.client.pojo.ClientResponse;
import remi.discovery.client.pojo.DiscoveryRequestHolder;


public class DiscoveryProcessThread implements Runnable {

	private static final List<DiscoveryRequestHolder> listeners = Collections.synchronizedList(new ArrayList<DiscoveryRequestHolder>());

	@Override
	public void run() {
		while(!listeners.isEmpty()){
			Iterator<DiscoveryRequestHolder> iterator = listeners.iterator();
			while(iterator.hasNext()){
				final DiscoveryRequestHolder next = iterator.next();
				if(next.getTimeBeforeNextRequest()<=0){
					DiscoveryRequest req = next.getRequest();
					next.resetTimeBeforeNextRequest();
					DiscoveryClientThread.sendRequest(new ClientRequest(req.getServiceName()), new OnResponseListener() {
						@Override
						public void onResponse(ClientResponse resp) {
							next.getCallback().onServiceFound(buildServiceInfo(resp));
						}
					});
				}else{
					next.decreaseTimeBeforeNextRequest();
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private ServiceInfo buildServiceInfo(ClientResponse resp) {
		ServiceInfo service = ServiceInfo.toObject(resp.getMessage());
		service.setInetAddress(resp.getInetAddress());
		return service;
	}
	
	public static void discover(DiscoveryRequest req, OnDiscoveryListener callback) {
		listeners.add(new DiscoveryRequestHolder(req, callback, new Date()));
	}

	public static void stopDiscover(OnDiscoveryListener callback) {
		Iterator<DiscoveryRequestHolder> iterator = listeners.iterator();
		while(iterator.hasNext()){
			DiscoveryRequestHolder next = iterator.next();
			if(callback.equals(next.getCallback())){
				iterator.remove();
			}
		}
	}
	public static List<DiscoveryRequestHolder> getListeners() {
		return listeners;
	}
}
