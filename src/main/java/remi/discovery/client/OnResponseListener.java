package remi.discovery.client;
import remi.discovery.client.pojo.ClientResponse;


public interface OnResponseListener {

	void onResponse(ClientResponse resp);

}
