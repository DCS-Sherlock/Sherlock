package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

class Ngram {

	// since this object only contains primitives and immutable objects, we do not have to worry about returning
	//references
	private String content;
	private int start;
	private int end;

	public Ngram(String c, int s, int e) {
		this.content = c;
		this.start = s;
		this.end = e;
	}

	@Override
	public Ngram clone() {
		return new Ngram(this.content, this.start, this.end);
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String c) {
		this.content = c;
	}

	public int getEnd() {
		return this.end;
	}

	public void setEnd(int e) {
		this.end = e;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int s) {
		this.start = s;
	}

	@Override
	public String toString() {
		return "Lines: (" + String.valueOf(this.start) + "," + String.valueOf(this.end) + ") Content: " + this.content;
	}
}