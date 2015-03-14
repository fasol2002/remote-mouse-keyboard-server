package remi.discovery;
import java.util.Date;


public class DiscoveryRequest {

	private Date requested = new Date();
	private String serviceName;
	
	public DiscoveryRequest(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public Date getRequested() {
		return requested;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
}
