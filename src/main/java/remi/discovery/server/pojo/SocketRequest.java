package remi.discovery.server.pojo;

public class SocketRequest {

	private String hostAddress;
	private String message;

	public SocketRequest(String hostAddress, String message) {
		this.hostAddress = hostAddress;
		this.message = message;
	}

	
	public String getHostAddress() {
		return hostAddress;
	}
	
	public String getMessage() {
		return message;
	}
}
