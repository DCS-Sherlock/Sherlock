package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

public class MyEdge {

	private String node1;
	private String node2;
	private int distance;

	public MyEdge(String n1, String n2, int s) {
		this.node1 = n1;
		this.node2 = n2;
		this.distance = s;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getNode1() {
		return node1;
	}

	public void setNode1(String node1) {
		this.node1 = node1;
	}

	public String getNode2() {
		return node2;
	}

	public void setNode2(String node2) {
		this.node2 = node2;
	}

	@Override
	public String toString() {
		return "Edge [" + this.node1 + ", " + this.node2 + "], distance: " + String.valueOf(this.distance);
	}
}
