package remi.discovery.client.pojo;

import java.net.InetAddress;

public class ClientResponse {

	private String message;
	private InetAddress inetAddress;

	public ClientResponse(String message, InetAddress inetAddress) {
		this.message =message;
		this.inetAddress = inetAddress;
	}
	
	public String getMessage() {
		return message;
	}
	
	public InetAddress getInetAddress() {
		return inetAddress;
	}

}
