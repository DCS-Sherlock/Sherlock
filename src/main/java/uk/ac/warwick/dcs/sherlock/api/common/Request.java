package uk.ac.warwick.dcs.sherlock.api.common;

public abstract class Request<P, R> {

	//private Class<?> handler;
	private P payload;
	private R responce;

	public Request() {}

	public abstract Class<?> getHandler();

	public P getPayload() {
		return this.payload;
	}

	public Request setPayload(P payload) {
		this.payload = payload;
		return this;
	}

	public R getResponce() {
		return this.responce;
	}

	public void setResponce(R res) {
		this.responce = res;
	}

}
