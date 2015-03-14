package remi.discovery;


public interface OnDiscoveryListener {

	void onServiceFound(ServiceInfo service);
	
	//void onServiceLost(ServiceInfo service);
	
}
