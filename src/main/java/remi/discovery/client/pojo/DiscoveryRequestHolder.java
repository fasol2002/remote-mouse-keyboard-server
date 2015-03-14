package remi.discovery.client.pojo;
import java.util.Date;

import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.client.OnResponseListener;


public class DiscoveryRequestHolder {


	private DiscoveryRequest request;
	private OnDiscoveryListener callback;
	private Date requestDate;
	private long timeBeforeNextRequest = 0;

	public DiscoveryRequestHolder(DiscoveryRequest request,
			OnDiscoveryListener callback, Date requestDate) {
		this.request= request;
		this.callback=callback;
		this.requestDate=requestDate;
	}
	
	public DiscoveryRequest getRequest() {
		return request;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public OnDiscoveryListener getCallback() {
		return callback;
	}
	public long getTimeBeforeNextRequest() {
		return timeBeforeNextRequest;
	}
	public void resetTimeBeforeNextRequest() {
		timeBeforeNextRequest = 10;
	}

	public void decreaseTimeBeforeNextRequest() {
		timeBeforeNextRequest--;
	}


}
