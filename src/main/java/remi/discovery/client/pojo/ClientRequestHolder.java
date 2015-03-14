package remi.discovery.client.pojo;
import remi.discovery.client.OnResponseListener;


public class ClientRequestHolder {
	private ClientRequest req;
	private OnResponseListener callback;

	public ClientRequestHolder(ClientRequest req,
			OnResponseListener callback) {

		this.req=req;
		this.callback=callback;

	}

	public OnResponseListener getCallback() {
		return callback;
	}

	public ClientRequest getReq() {
		return req;
	}
}
