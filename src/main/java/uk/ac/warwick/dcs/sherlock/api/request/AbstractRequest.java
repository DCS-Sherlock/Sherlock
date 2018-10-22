package uk.ac.warwick.dcs.sherlock.api.request;

public abstract class AbstractRequest<P, R> {

	private P payload;
	private R responce;

	public AbstractRequest() {}

	public abstract Class<?> getHandler();

	public P getPayload() {
		return this.payload;
	}

	public AbstractRequest setPayload(P payload) {
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
